package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.compiler.impl.BreakLexerNode;
import org.mangorage.mangolang.compiler.impl.EndLexerNode;
import org.mangorage.mangolang.compiler.impl.FunctionLexerNode;
import org.mangorage.mangolang.compiler.impl.IfStatementLexerNode;
import org.mangorage.mangolang.compiler.impl.WhileLexerNode;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Compiler {
    private static final Pattern PAREN_CALL_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)\\s*\\((.*)\\)");

    private final InstructionSet set;

    public Compiler(InstructionSet set) {
        this.set = set;
    }

    public byte[] compile(String source) {
        List<LexerNode> nodes = List.of(
                new BreakLexerNode(),
                new EndLexerNode(),
                new FunctionLexerNode(),
                new IfStatementLexerNode(),
                new WhileLexerNode()
        );

        CompilerContext ctx = new CompilerContext();
        List<Byte> out = new ArrayList<>();


        // Upgrade from `boolean inFunction` to a stack to support nesting!
        Stack<BlockContext> blocks = new Stack<>();

        String[] lines = source.split("\\n");

        main: for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            int commentIndex = line.indexOf('#');
            if (commentIndex != -1)
                line = line.substring(0, commentIndex).trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String name = parts[0].toLowerCase();


            for (LexerNode node : nodes) {
                final var output = node.handle(
                        parts,
                        name,
                        blocks,
                        out,
                        ctx,
                        set
                );

                if (output.doContinue())
                    continue main;
            }

            if (emitSpecialInstruction(line, parts, name, out, ctx)) {
                continue;
            }

            // ===== NORMAL INSTRUCTION =====
            if (System.getProperty("mangolang.debug") != null) {
                System.out.println("[Compiler] line='" + line + "' name='" + name + "' args='" + Arrays.toString(Arrays.copyOfRange(parts, 1, parts.length)) + "'");
            }
            int opcode = set.requireOpcode(name);
            boolean debug = System.getProperty("mangolang.debug") != null;
            if (debug) {
                System.out.println("[Compiler] emitting opcode " + opcode + " for '" + name + "' at out.size=" + out.size());
            }
            out.add((byte) opcode);

            Instruction inst = set.get(opcode);
            Object[] args = Arrays.copyOfRange(parts, 1, parts.length);
            inst.emitBytecode(out, ctx, args);
            if (debug) {
                System.out.println("[Compiler] after emit out.size=" + out.size());
                System.out.print("[Compiler] bytes=");
                for (int i = 0; i < out.size(); i++) System.out.print((out.get(i) & 0xFF) + (i + 1 < out.size() ? "," : ""));
                System.out.println();
            }
        }

        if (!blocks.isEmpty()) throw new RuntimeException("Missing 'end' for block");

        out.add((byte) set.requireOpcode("halt"));

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

        byte[] arr = new byte[out.size()];
        for (int i = 0; i < out.size(); i++) arr[i] = out.get(i);
        return arr;
    }

    private boolean emitSpecialInstruction(String line, String[] parts, String name, List<Byte> out, CompilerContext ctx) {
        if ("call".equals(name)) {
            emitCallInstruction(line, out, ctx);
            return true;
        }

        if ("print".equals(name)) {
            String rawArgs = joinArgs(parts, 1);
            if (rawArgs.contains("..")) {
                emitConcatenatedPrint(rawArgs, out, ctx);
                return true;
            }
        }

        return false;
    }

    private void emitCallInstruction(String line, List<Byte> out, CompilerContext ctx) {
        String rawCall = line.substring("call".length()).trim();
        if (rawCall.isEmpty()) {
            throw new RuntimeException("Call requires a function name");
        }

        String functionName;
        List<String> arguments;
        Matcher matcher = PAREN_CALL_PATTERN.matcher(rawCall);
        if (matcher.matches()) {
            functionName = matcher.group(1);
            arguments = splitCommaSeparatedRespectingQuotes(matcher.group(2));
        } else {
            List<String> tokens = splitWhitespaceRespectingQuotes(rawCall);
            functionName = normalizeFunctionName(tokens.get(0));
            arguments = tokens.size() <= 1 ? List.of() : new ArrayList<>(tokens.subList(1, tokens.size()));
        }

        CompilerContext.FunctionInfo functionInfo = ctx.getFunctionInfo(functionName);
        if (arguments.size() != functionInfo.parameterCount()) {
            throw new RuntimeException("Function '" + functionName + "' expects " + functionInfo.parameterCount() + " arguments but got " + arguments.size());
        }

        for (String argument : arguments) {
            emitValuePush(argument, out, ctx);
        }

        int opcode = set.requireOpcode("call");
        out.add((byte) opcode);
        set.get(opcode).emitBytecode(out, ctx, functionName, Integer.toString(arguments.size()));
    }

    private void emitConcatenatedPrint(String rawArgs, List<Byte> out, CompilerContext ctx) {
        List<String> operands = splitConcatenationOperands(rawArgs);
        if (operands.isEmpty()) {
            throw new RuntimeException("Print concatenation requires at least one operand");
        }

        emitValuePush(operands.get(0), out, ctx);
        for (int i = 1; i < operands.size(); i++) {
            emitValuePush(operands.get(i), out, ctx);
            out.add((byte) set.requireOpcode("add"));
        }

        out.add((byte) set.requireOpcode("print"));
    }

    private void emitValuePush(String rawValue, List<Byte> out, CompilerContext ctx) {
        String value = rawValue.trim();
        if (value.isEmpty()) {
            throw new RuntimeException("Missing value expression");
        }

        MangolangObject literal = MangolangObjects.literalFromToken(value);
        if (literal != null) {
            out.add((byte) set.requireOpcode("push"));
            MangolangObjects.emitObject(out, literal);
            return;
        }

        if (ctx.hasVariable(value)) {
            int opcode = set.requireOpcode("load");
            out.add((byte) opcode);
            set.get(opcode).emitBytecode(out, ctx, value);
            return;
        }

        throw new RuntimeException("Unsupported value expression: " + value);
    }

    private String joinArgs(String[] parts, int startIndex) {
        if (parts.length <= startIndex) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            if (i > startIndex) {
                builder.append(' ');
            }
            builder.append(parts[i]);
        }
        return builder.toString();
    }

    private List<String> splitWhitespaceRespectingQuotes(String raw) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
                continue;
            }

            if (!inQuotes && Character.isWhitespace(c)) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }

            current.append(c);
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + raw);
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    private List<String> splitCommaSeparatedRespectingQuotes(String raw) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
                continue;
            }

            if (!inQuotes && c == ',') {
                String token = current.toString().trim();
                if (!token.isEmpty()) {
                    tokens.add(token);
                }
                current.setLength(0);
                continue;
            }

            current.append(c);
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + raw);
        }

        String trailing = current.toString().trim();
        if (!trailing.isEmpty()) {
            tokens.add(trailing);
        }

        return tokens;
    }

    private List<String> splitConcatenationOperands(String raw) {
        List<String> operands = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
                continue;
            }

            if (!inQuotes && c == '.' && i + 1 < raw.length() && raw.charAt(i + 1) == '.') {
                String operand = current.toString().trim();
                if (operand.isEmpty()) {
                    throw new RuntimeException("Invalid concatenation expression: " + raw);
                }
                operands.add(operand);
                current.setLength(0);
                i++;
                continue;
            }

            current.append(c);
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + raw);
        }

        String trailing = current.toString().trim();
        if (trailing.isEmpty()) {
            throw new RuntimeException("Invalid concatenation expression: " + raw);
        }
        operands.add(trailing);
        return operands;
    }

    private String normalizeFunctionName(String token) {
        String trimmed = token.trim();
        if (trimmed.endsWith("()")) {
            return trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }
}