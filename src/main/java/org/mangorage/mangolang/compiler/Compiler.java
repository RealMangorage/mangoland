package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.compiler.impl.BreakLexerNode;
import org.mangorage.mangolang.compiler.impl.EndLexerNode;
import org.mangorage.mangolang.compiler.impl.FunctionLexerNode;
import org.mangorage.mangolang.compiler.impl.IfStatementLexerNode;
import org.mangorage.mangolang.compiler.impl.WhileLexerNode;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Compiler {
    private static final Pattern PAREN_CALL_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)\\s*\\((.*)\\)");
    private static final Pattern LET_ASSIGNMENT_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)\\s*=\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("([a-zA-Z_]\\w*)\\s*=\\s*(.+)");
    private static final Set<String> PRINT_INSTRUCTION_NAMES = Set.of("print", "println");

    private record BinaryOperator(int index, int width, String token) {
    }

    private record FunctionCall(String name, List<String> arguments) {
    }

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

            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String possibleLabel = line.substring(0, colonIndex).trim();
                String remainder = line.substring(colonIndex + 1).trim();
                if (!remainder.isEmpty() && remainder.startsWith("while ")) {
                    if (!possibleLabel.matches("[a-zA-Z_]\\w*")) {
                        throw new RuntimeException("Invalid loop label: " + possibleLabel);
                    }
                    ctx.setPendingLoopLabel(possibleLabel);
                    line = remainder;
                }
            }

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
        if ("let".equals(name) && emitLetAssignment(line, out, ctx)) {
            return true;
        }

        if (emitAssignment(line, name, out, ctx)) {
            return true;
        }

        if ("return".equals(name)) {
            emitReturnInstruction(line, out, ctx);
            return true;
        }

        if ("call".equals(name)) {
            emitCallInstruction(line, out, ctx);
            return true;
        }

        if (emitPrintInstruction(line, parts, name, out, ctx)) {
            return true;
        }

        if (isImplicitFunctionCall(line)) {
            emitCallInstruction(line, out, ctx);
            return true;
        }

        return false;
    }

    private boolean emitPrintInstruction(String line, String[] parts, String name, List<Byte> out, CompilerContext ctx) {
        if (PRINT_INSTRUCTION_NAMES.contains(name)) {
            emitPrintExpression(joinArgs(parts, 1), out, ctx);
            return true;
        }

        Matcher matcher = PAREN_CALL_PATTERN.matcher(line.trim());
        if (!matcher.matches() || !PRINT_INSTRUCTION_NAMES.contains(matcher.group(1).toLowerCase(Locale.ROOT))) {
            return false;
        }

        List<String> arguments = splitCommaSeparatedRespectingQuotes(matcher.group(2));
        if (arguments.size() > 1) {
            throw new RuntimeException("Print expects at most 1 argument but got " + arguments.size());
        }

        emitPrintExpression(arguments.isEmpty() ? "" : arguments.get(0), out, ctx);
        return true;
    }

    private void emitPrintExpression(String rawExpression, List<Byte> out, CompilerContext ctx) {
        String expression = rawExpression.trim();
        if (!expression.isEmpty()) {
            emitExpressionToStack(expression, out, ctx);
        }

        out.add((byte) set.requireOpcode("print"));
    }

    private boolean emitLetAssignment(String line, List<Byte> out, CompilerContext ctx) {
        String assignment = line.substring("let".length()).trim();
        Matcher matcher = LET_ASSIGNMENT_PATTERN.matcher(assignment);
        if (!matcher.matches()) {
            return false;
        }

        String variableName = matcher.group(1);
        String expression = matcher.group(2).trim();

        if (ctx.hasVariable(variableName)) {
            throw new RuntimeException("Variable already declared: " + variableName);
        }

        emitExpressionToStack(expression, out, ctx);

        int opcode = set.requireOpcode("store");
        out.add((byte) opcode);
        set.get(opcode).emitBytecode(out, ctx, variableName);
        return true;
    }

    private boolean emitAssignment(String line, String name, List<Byte> out, CompilerContext ctx) {
        if ("let".equals(name) || "if".equals(name) || "while".equals(name) || "function".equals(name)) {
            return false;
        }

        Matcher matcher = ASSIGNMENT_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return false;
        }

        String variableName = matcher.group(1);
        String expression = matcher.group(2).trim();

        if (!ctx.hasVariable(variableName)) {
            throw new RuntimeException("Unknown variable: " + variableName);
        }

        emitExpressionToStack(expression, out, ctx);
        int opcode = set.requireOpcode("store");
        out.add((byte) opcode);
        set.get(opcode).emitBytecode(out, ctx, variableName);
        return true;
    }

    private void emitReturnInstruction(String line, List<Byte> out, CompilerContext ctx) {
        String rawValue = line.substring("return".length()).trim();
        if (!rawValue.isEmpty()) {
            emitExpressionToStack(rawValue, out, ctx);
        }

        out.add((byte) set.requireOpcode("return"));
    }

    private void emitCallInstruction(String line, List<Byte> out, CompilerContext ctx) {
        FunctionCall functionCall = parseFunctionCall(line);
        String functionName = functionCall.name();
        List<String> arguments = functionCall.arguments();

        CompilerContext.FunctionInfo functionInfo = ctx.getFunctionInfo(functionName);
        if (arguments.size() != functionInfo.parameterCount()) {
            throw new RuntimeException("Function '" + functionName + "' expects " + functionInfo.parameterCount() + " arguments but got " + arguments.size());
        }

        for (String argument : arguments) {
            emitExpressionToStack(argument, out, ctx);
        }

        int opcode = set.requireOpcode("call");
        out.add((byte) opcode);
        set.get(opcode).emitBytecode(out, ctx, functionName, Integer.toString(arguments.size()));
    }

    private void emitExpressionToStack(String rawExpression, List<Byte> out, CompilerContext ctx) {
        String expression = stripEnclosingParentheses(rawExpression.trim());
        if (expression.isEmpty()) {
            throw new RuntimeException("Missing expression");
        }

        BinaryOperator operator = findTopLevelBinaryOperator(expression, false);
        if (operator == null) {
            operator = findTopLevelBinaryOperator(expression, true);
        }
        if (operator != null) {
            String left = expression.substring(0, operator.index()).trim();
            String right = expression.substring(operator.index() + operator.width()).trim();
            if (left.isEmpty() || right.isEmpty()) {
                throw new RuntimeException("Invalid binary expression: " + expression);
            }

            emitExpressionToStack(left, out, ctx);
            emitExpressionToStack(right, out, ctx);
            out.add((byte) set.requireOpcode(mapBinaryOperatorToInstruction(operator.token())));
            return;
        }

        if (expression.regionMatches(true, 0, "call", 0, "call".length())
                && (expression.length() == 4 || Character.isWhitespace(expression.charAt(4)))) {
            emitCallInstruction(expression, out, ctx);
            return;
        }

        if (isImplicitFunctionCall(expression)) {
            emitCallInstruction(expression, out, ctx);
            return;
        }

        emitValuePush(expression, out, ctx);
    }

    private void emitValuePush(String rawValue, List<Byte> out, CompilerContext ctx) {
        CompilerEmitUtil.emitValuePush(rawValue, out, ctx, set);
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

    private BinaryOperator findTopLevelBinaryOperator(String raw, boolean highPrecedence) {
        boolean inQuotes = false;
        int parenthesisDepth = 0;
        for (int i = raw.length() - 1; i >= 0; i--) {
            char c = raw.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes) {
                if (c == ')') {
                    parenthesisDepth++;
                    continue;
                }

                if (c == '(') {
                    if (parenthesisDepth == 0) {
                        throw new RuntimeException("Unmatched '(' in expression: " + raw);
                    }

                    parenthesisDepth--;
                    continue;
                }

                if (parenthesisDepth > 0) {
                    continue;
                }

                if (!highPrecedence) {
                    if (c == '+') {
                        return new BinaryOperator(i, 1, "+");
                    }

                    if (c == '-' && isBinaryMinus(raw, i)) {
                        return new BinaryOperator(i, 1, "-");
                    }

                    if (c == '.' && i + 1 < raw.length() && raw.charAt(i + 1) == '.') {
                        return new BinaryOperator(i, 2, "..");
                    }
                }

                if (highPrecedence) {
                    if (c == '*') {
                        return new BinaryOperator(i, 1, "*");
                    }

                    if (c == '/') {
                        return new BinaryOperator(i, 1, "/");
                    }
                }
            }
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + raw);
        }

        if (parenthesisDepth != 0) {
            throw new RuntimeException("Unmatched ')' in expression: " + raw);
        }

        return null;
    }

    private boolean isBinaryMinus(String raw, int index) {
        if (index <= 0) {
            return false;
        }

        for (int i = index - 1; i >= 0; i--) {
            char previous = raw.charAt(i);
            if (Character.isWhitespace(previous)) {
                continue;
            }

            return previous != '+'
                    && previous != '-'
                    && previous != '*'
                    && previous != '/'
                    && previous != '.'
                    && previous != '(';
        }

        return false;
    }

    private String mapBinaryOperatorToInstruction(String token) {
        return switch (token) {
            case "+", ".." -> "add";
            case "-" -> "subtract";
            case "*" -> "multiply";
            case "/" -> "divide";
            default -> throw new RuntimeException("Unsupported operator token: " + token);
        };
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
        int parenthesisDepth = 0;

        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
                continue;
            }

            if (!inQuotes) {
                if (c == '(') {
                    parenthesisDepth++;
                    current.append(c);
                    continue;
                }

                if (c == ')') {
                    if (parenthesisDepth == 0) {
                        throw new RuntimeException("Unmatched ')' in call arguments: " + raw);
                    }
                    parenthesisDepth--;
                    current.append(c);
                    continue;
                }
            }

            if (!inQuotes && parenthesisDepth == 0 && c == ',') {
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

        if (parenthesisDepth != 0) {
            throw new RuntimeException("Unmatched '(' in call arguments: " + raw);
        }

        String trailing = current.toString().trim();
        if (!trailing.isEmpty()) {
            tokens.add(trailing);
        }

        return tokens;
    }


    private String normalizeFunctionName(String token) {
        String trimmed = token.trim();
        if (trimmed.endsWith("()")) {
            return trimmed.substring(0, trimmed.length() - 2);
        }
        return trimmed;
    }

    private FunctionCall parseFunctionCall(String line) {
        String rawCall = line.trim();
        if (rawCall.regionMatches(true, 0, "call", 0, "call".length())
                && (rawCall.length() == 4 || Character.isWhitespace(rawCall.charAt(4)))) {
            rawCall = rawCall.substring("call".length()).trim();
        }

        if (rawCall.isEmpty()) {
            throw new RuntimeException("Call requires a function name");
        }

        Matcher matcher = PAREN_CALL_PATTERN.matcher(rawCall);
        if (matcher.matches()) {
            return new FunctionCall(
                    normalizeFunctionName(matcher.group(1)),
                    splitCommaSeparatedRespectingQuotes(matcher.group(2))
            );
        }

        List<String> tokens = splitWhitespaceRespectingQuotes(rawCall);
        if (tokens.isEmpty()) {
            throw new RuntimeException("Call requires a function name");
        }

        return new FunctionCall(
                normalizeFunctionName(tokens.get(0)),
                tokens.size() <= 1 ? List.of() : new ArrayList<>(tokens.subList(1, tokens.size()))
        );
    }

    private boolean isFunctionCallExpression(String rawExpression) {
        String expression = rawExpression.trim();
        if (expression.isEmpty()) {
            return false;
        }

        if (expression.regionMatches(true, 0, "call", 0, "call".length())
                && (expression.length() == 4 || Character.isWhitespace(expression.charAt(4)))) {
            return true;
        }

        return isImplicitFunctionCall(expression);
    }

    private boolean isImplicitFunctionCall(String rawExpression) {
        Matcher matcher = PAREN_CALL_PATTERN.matcher(rawExpression.trim());
        return matcher.matches() && set.getOpcode(matcher.group(1)) == null;
    }

    private String stripEnclosingParentheses(String rawExpression) {
        String expression = rawExpression;
        while (isWrappedInMatchingParentheses(expression)) {
            expression = expression.substring(1, expression.length() - 1).trim();
        }
        return expression;
    }

    private boolean isWrappedInMatchingParentheses(String rawExpression) {
        if (rawExpression.length() < 2 || rawExpression.charAt(0) != '(' || rawExpression.charAt(rawExpression.length() - 1) != ')') {
            return false;
        }

        boolean inQuotes = false;
        int parenthesisDepth = 0;
        for (int i = 0; i < rawExpression.length(); i++) {
            char c = rawExpression.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                continue;
            }

            if (inQuotes) {
                continue;
            }

            if (c == '(') {
                parenthesisDepth++;
            } else if (c == ')') {
                parenthesisDepth--;
                if (parenthesisDepth == 0 && i < rawExpression.length() - 1) {
                    return false;
                }
                if (parenthesisDepth < 0) {
                    throw new RuntimeException("Unmatched ')' in expression: " + rawExpression);
                }
            }
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + rawExpression);
        }

        if (parenthesisDepth != 0) {
            throw new RuntimeException("Unmatched '(' in expression: " + rawExpression);
        }

        return true;
    }
}