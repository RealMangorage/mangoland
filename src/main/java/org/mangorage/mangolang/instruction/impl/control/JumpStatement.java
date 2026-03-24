package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.register.ParamType;
import org.mangorage.mangolang.instruction.register.Parameter;
import org.mangorage.mangolang.object.impl.BooleanMLObject;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction(
        id = "jump_if_true",
        params = {
                @Parameter(
                        type = ParamType.BOOLEAN,
                        value = "true"
                )
        }
)
@AutoRegisterInstruction(
        id = "jump_if_false",
        params = {
                @Parameter(
                        type = ParamType.BOOLEAN,
                        value = "false"
                )
        }
)
public final class JumpStatement implements Instruction {
    private final boolean value;

    public JumpStatement(boolean value) {
        this.value = value;
    }

    @Override
    public void execute(VMEnvironment env) {
        final var booleanObj = env.getStack().pop();
        final boolean booleanValue = BooleanMLObject.coerceBooleanValue(booleanObj, "jump condition");

        // Read the two target addresses emitted by the compiler (each address is two bytes: low, high)
        int trueLo = env.next();
        int trueHi = env.next();
        int falseLo = env.next();
        int falseHi = env.next();

        int trueAddr = (trueHi << 8) | (trueLo & 0xFF);
        int falseAddr = (falseHi << 8) | (falseLo & 0xFF);

        if (booleanValue == value) {
            env.setIp(trueAddr);
        } else {
            env.setIp(falseAddr);
        }
    }

    public void emitBytecode(List<Byte> out, CompilerContext ctx, Object... args) {
        if (args.length != 2) throw new RuntimeException("jump_if_true requires two function names");

        int a = ctx.getFunction((String) args[0]);
        int b = ctx.getFunction((String) args[1]);

        out.add((byte) (a & 0xFF));
        out.add((byte) ((a >> 8) & 0xFF));
        out.add((byte) (b & 0xFF));
        out.add((byte) ((b >> 8) & 0xFF));
    }
}
