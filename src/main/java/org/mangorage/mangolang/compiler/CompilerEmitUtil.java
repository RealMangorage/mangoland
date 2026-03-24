package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjectCompiler;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CompilerEmitUtil {
    private static final String INLINE_LITERAL_PATTERN = "(?:true|false|-?(?:\\d+[lL]|(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?[fFdD]?|\\d+)|\"[^\"]*\")";
    private static final Pattern INLINE_CONDITION_PATTERN = Pattern.compile(
            "\\(?\\s*([a-zA-Z_]\\w*|" + INLINE_LITERAL_PATTERN + ")\\s*(==|!=)\\s*([a-zA-Z_]\\w*|" + INLINE_LITERAL_PATTERN + ")\\s*\\)?"
    );

    private CompilerEmitUtil() {
    }

    public static void emitValuePush(String rawValue, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        String value = rawValue.trim();
        if (value.isEmpty()) {
            throw new RuntimeException("Missing value expression");
        }

        MangolangObject literal = MangolangObjectCompiler.literalFromToken(value);
        if (literal != null) {
            out.add((byte) set.requireOpcode("push"));
            MangolangObjectCompiler.emitObject(out, literal);
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

    public static void emitSimpleCondition(String condition, List<Byte> out, CompilerContext ctx, InstructionSet set) {
        Matcher matcher = INLINE_CONDITION_PATTERN.matcher(condition.trim());
        if (!matcher.matches()) {
            throw new RuntimeException("Unsupported inline condition: " + condition);
        }

        String left = matcher.group(1);
        String operator = matcher.group(2);
        String right = matcher.group(3);

        emitValuePush(left, out, ctx, set);
        emitValuePush(right, out, ctx, set);

        int equalsOpcode = set.requireOpcode("equals");
        out.add((byte) equalsOpcode);
        set.get(equalsOpcode).emitBytecode(out, ctx);

        if ("!=".equals(operator)) {
            int pushOpcode = set.requireOpcode("push");
            out.add((byte) pushOpcode);
            set.get(pushOpcode).emitBytecode(out, ctx, "false");

            out.add((byte) equalsOpcode);
            set.get(equalsOpcode).emitBytecode(out, ctx);
        }
    }
}
