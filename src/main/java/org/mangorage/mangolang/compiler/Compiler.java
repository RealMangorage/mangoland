package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Compiler {
    private final InstructionSet set;

    // Helper class to track nested functions and loops
    private static class BlockContext {
        enum Type { FUNCTION, WHILE, IF }
        Type type;
        int startAddress;       // Where to jump back to (for loops) or skip to (for functions)
        int condJumpAddress;    // The index of the jump_if_false placeholder
        int elseJumpAddress = -1; // placeholder index for the unconditional jump over the else-body
        List<Integer> breaks = new ArrayList<>(); // Track all breaks in this loop

        BlockContext(Type type, int startAddress) {
            this.type = type;
            this.startAddress = startAddress;
        }
    }

    public Compiler(InstructionSet set) {
        this.set = set;
    }

    public int[] compile(String source) {
        CompilerContext ctx = new CompilerContext();
        List<Integer> out = new ArrayList<>();

        // Upgrade from `boolean inFunction` to a stack to support nesting!
        Stack<BlockContext> blocks = new Stack<>();

        String[] lines = source.split("\\n");

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            int commentIndex = line.indexOf('#');
            if (commentIndex != -1)
                line = line.substring(0, commentIndex).trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String name = parts[0].toLowerCase();

            // ===== FUNCTION START =====
            if (name.equals("function")) {
                String funcName = parts[1];
                BlockContext b = new BlockContext(BlockContext.Type.FUNCTION, out.size());
                blocks.push(b);

                // Note: Using 'jump' instead of 'call' to skip over the function body
                // prevents accidentally pushing a junk frame to your callStack!
                out.add(set.requireOpcode("jump"));
                out.add(0); // placeholder

                ctx.registerFunction(funcName, out.size());
                continue;
            }

            // ===== WHILE START =====
            if (name.equals("while")) {
                // Save the exact address where the condition evaluation begins
                blocks.push(new BlockContext(BlockContext.Type.WHILE, out.size()));
                continue;
            }

            // ===== DO (Evaluates the while condition) =====
            if (name.equals("do")) {
                BlockContext b = blocks.peek();
                if (b == null || b.type != BlockContext.Type.WHILE) {
                    throw new RuntimeException("Unexpected 'do' without 'while'");
                }
                b.condJumpAddress = out.size();
                out.add(set.requireOpcode("jump_if_false"));
                out.add(0); // true-target placeholder (patched at 'end' to loop exit)
                out.add(0); // false-target placeholder (points to instruction after these two placeholders)
                continue;
            }

            // ===== IF START =====
            if (name.equals("if")) {
                // If the 'if' is inline and contains 'do' on the same line, try to parse a simple condition
                int delimIdx = -1;
                String delim = null;
                for (int i = 1; i < parts.length; i++) {
                    if (parts[i].equalsIgnoreCase("do") || parts[i].equalsIgnoreCase("then")) {
                        delimIdx = i;
                        delim = parts[i].toLowerCase();
                        break;
                    }
                }

                if (delimIdx != -1) {
                    // Join the condition tokens between 'if' and the delimiter (do/then)
                    String condStr = String.join(" ", Arrays.copyOfRange(parts, 1, delimIdx));

                    // Simple pattern: (var == value) allowing optional surrounding parens
                    Pattern p = Pattern.compile("\\(?\\s*([a-zA-Z_]\\w*)\\s*(==|!=)\\s*([0-9]+)\\s*\\)?");
                    Matcher m = p.matcher(condStr);
                    if (!m.matches()) {
                        throw new RuntimeException("Unsupported inline if condition: " + condStr);
                    }

                    String var = m.group(1);
                    String op = m.group(2);
                    String val = m.group(3);

                    // Emit the equivalent instructions for the simple condition
                    // load var
                    int opLoad = set.requireOpcode("load");
                    out.add(opLoad);
                    Instruction instLoad = set.get(opLoad);
                    instLoad.emitBytecode(out, ctx, new Object[]{var});

                    // push value
                    int opPush = set.requireOpcode("push");
                    out.add(opPush);
                    set.get(opPush).emitBytecode(out, ctx, new Object[]{val});

                    // equals (we only support '==' for now; '!=' handled by comparing result to 0 later)
                    int opEq = set.requireOpcode("equals");
                    out.add(opEq);
                    set.get(opEq).emitBytecode(out, ctx, new Object[]{});

                    if (op.equals("!=")) {
                        // Invert the boolean: equals produced 1 when equal; we want 1 when not equal.
                        // We'll emit: push 0 ; equals  -> compares (equalsResult == 0)
                        int opPush0 = set.requireOpcode("push");
                        out.add(opPush0);
                        set.get(opPush0).emitBytecode(out, ctx, new Object[]{"0"});

                        int opEq2 = set.requireOpcode("equals");
                        out.add(opEq2);
                        set.get(opEq2).emitBytecode(out, ctx, new Object[]{});
                    }

                    // Now emit the conditional jump placeholder to skip the then-body when false
                    BlockContext b = new BlockContext(BlockContext.Type.IF, out.size());
                    blocks.push(b);
                    b.condJumpAddress = out.size();
                    out.add(set.requireOpcode("jump_if_false"));
                    out.add(0); // true-target placeholder (patched to else/exit)
                    out.add(0); // false-target placeholder (points to instruction after these placeholders)
                    // If the delimiter was 'then' and there are tokens after it on the same line,
                    // we should continue processing the rest of this line as normal instructions.
                    if ("then".equals(delim) && delimIdx + 1 < parts.length) {
                        // Rebuild the remainder of the line and process it immediately
                        String[] remainder = Arrays.copyOfRange(parts, delimIdx + 1, parts.length);
                        // Create a pseudo-line and fall through to normal instruction handling by
                        // replacing 'parts' and 'name' for this iteration.
                        parts = remainder;
                        name = parts[0].toLowerCase();
                        // fall through to emit this instruction below
                    } else {
                        continue;
                    }
                }

                // Non-inline: push IF context and expect a separate 'then' token later
                blocks.push(new BlockContext(BlockContext.Type.IF, out.size()));
                continue;
            }

            // ===== THEN (marks end of condition, start of then-body) =====
            if (name.equals("then") || (name.equals("end") && blocks.peek() != null && blocks.peek().type == BlockContext.Type.IF)) {
                BlockContext b = blocks.peek();
                if (b == null || b.type != BlockContext.Type.IF) {
                    throw new RuntimeException("Unexpected 'then' without 'if'");
                }
                // Emit conditional jump placeholder; if condition is false, skip the then body
                b.condJumpAddress = out.size();
                out.add(set.requireOpcode("jump_if_false"));
                out.add(0); // true-target placeholder
                out.add(0); // false-target placeholder
                if (!name.equals("end")) {
                    continue;
                }
            }

            // ===== ELSE =====
            if (name.equals("else")) {
                BlockContext b = blocks.peek();
                if (b == null || b.type != BlockContext.Type.IF) {
                    throw new RuntimeException("Unexpected 'else' without 'if'");
                }

                // We are at the boundary between then-body and else-body. We'll emit
                // an unconditional jump here (to skip the else body) which occupies
                // two slots (opcode + placeholder). Therefore the actual start of
                // the else-body will be current out.size() + 2.
                int elseStart = out.size() + 2;

                // Patch the conditional jump to point to the start of the else-body
                out.set(b.condJumpAddress + 1, elseStart);
                // Ensure false-target jumps into the then-body (immediately after the two placeholders)
                out.set(b.condJumpAddress + 2, b.condJumpAddress + 3);

                // Emit an unconditional jump to skip the else body after then-body
                out.add(set.requireOpcode("jump"));
                out.add(0); // placeholder to be patched at 'end'
                b.elseJumpAddress = out.size() - 1; // index of the placeholder value
                continue;
            }

            // ===== BREAK =====
            if (name.equals("break")) {
                // Search down the stack to find the nearest loop (allows breaking out of a loop inside an if/function)
                BlockContext loop = null;
                for (int i = blocks.size() - 1; i >= 0; i--) {
                    if (blocks.get(i).type == BlockContext.Type.WHILE) {
                        loop = blocks.get(i);
                        break;
                    }
                }
                if (loop == null) throw new RuntimeException("Cannot 'break' outside of a loop");

                loop.breaks.add(out.size());
                out.add(set.requireOpcode("jump"));
                out.add(0); // placeholder, patched at 'end'
                continue;
            }

            // ===== END =====
            if (name.equals("end")) {
                if (blocks.isEmpty()) throw new RuntimeException("Unexpected 'end'");
                BlockContext b = blocks.pop();

                if (b.type == BlockContext.Type.FUNCTION) {
                    out.add(set.requireOpcode("return"));
                    // Patch the jump so the VM skips over the function definition
                    out.set(b.startAddress + 1, out.size());
                }
                else if (b.type == BlockContext.Type.WHILE) {
                    // Unconditional jump back to the 'while' condition
                    out.add(set.requireOpcode("jump"));
                    out.add(b.startAddress);

                    int loopExitAddress = out.size();

                    // 1. Patch the 'do' conditional jump
                    // condJumpAddress points at opcode; +1 is true-target placeholder, +2 is false-target
                    out.set(b.condJumpAddress + 1, loopExitAddress);
                    out.set(b.condJumpAddress + 2, loopExitAddress);

                    // 2. Patch all 'break' statements inside this loop
                    for (int breakAddr : b.breaks) {
                        out.set(breakAddr + 1, loopExitAddress);
                    }
                }
                else if (b.type == BlockContext.Type.IF) {
                    // If there was an ELSE branch, patch its unconditional jump placeholder
                    if (b.elseJumpAddress != -1) {
                        out.set(b.elseJumpAddress, out.size());
                        // Also patch the original conditional's false-target to point to the start of the then-body
                        out.set(b.condJumpAddress + 2, b.condJumpAddress + 3);
                    } else {
                        // No ELSE: patch the conditional jump to skip the then-body
                        out.set(b.condJumpAddress + 1, out.size());
                        out.set(b.condJumpAddress + 2, out.size());
                    }
                }
                continue;
            }

            // ===== NORMAL INSTRUCTION =====
            if (System.getProperty("mangolang.debug") != null) {
                System.out.println("[Compiler] line='" + line + "' name='" + name + "' args='" + Arrays.toString(Arrays.copyOfRange(parts, 1, parts.length)) + "'");
            }
            int opcode = set.requireOpcode(name);
            out.add(opcode);

            Instruction inst = set.get(opcode);
            Object[] args = Arrays.copyOfRange(parts, 1, parts.length);
            inst.emitBytecode(out, ctx, args);
        }

        if (!blocks.isEmpty()) throw new RuntimeException("Missing 'end' for block");

        out.add(set.requireOpcode("halt"));

        // ===== SANITY CHECK =====
        for (int i = 0; i < out.size(); i++) {
            if (out.get(i) == null) {
                throw new RuntimeException("Null bytecode at index " + i);
            }
        }

        // Optional debug: dump raw bytecode when system property is set
        if (System.getProperty("mangolang.dumpbytecode") != null) {
            System.out.println("[Compiler] raw bytecode: " + out);
        }

        return out.stream().mapToInt(i -> i).toArray();
    }
}