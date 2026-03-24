package org.mangorage.mangolang.compiler;

import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;

import java.util.List;

public final class CompilerEmitUtil {
    private CompilerEmitUtil() {
    }

    public static void emitValuePush(String rawValue, List<Byte> out, CompilerContext ctx, InstructionSet set) {
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
}
