package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.*;

public final class Compiler {
    private final InstructionSet set;

    // Helper class to track nested functions and loops
    private static class BlockContext {
        enum Type { FUNCTION, WHILE }
        Type type;
        int startAddress;       // Where to jump back to (for loops) or skip to (for functions)
        int condJumpAddress;    // The index of the jump_if_false placeholder
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
                out.add(0); // placeholder to skip the loop body, patched at 'end'
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
                    out.set(b.condJumpAddress + 1, loopExitAddress);

                    // 2. Patch all 'break' statements inside this loop
                    for (int breakAddr : b.breaks) {
                        out.set(breakAddr + 1, loopExitAddress);
                    }
                }
                continue;
            }

            // ===== NORMAL INSTRUCTION =====
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

        return out.stream().mapToInt(i -> i).toArray();
    }
}