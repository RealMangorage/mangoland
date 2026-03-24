package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.Frame;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Call implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        // Addresses are emitted as two bytes: low, high
        int low = env.next() & 0xFF;
        int high = env.next() & 0xFF;
        int argCount = env.next() & 0xFF;
        int addr = (high << 8) | low;

        if (argCount > Frame.LOCAL_CAPACITY) {
            throw new RuntimeException("Function call has too many arguments: " + argCount);
        }

        Frame caller = env.getCallStack().peek();
        Frame newFrame = new Frame(env.getIp(), caller.globals);

        for (int i = argCount - 1; i >= 0; i--) {
            newFrame.locals[i] = env.getStack().pop();
        }

        env.getCallStack().push(newFrame);
        env.setIp(addr);
    }

    public void emitBytecode(List<Byte> out, CompilerContext ctx, Object... args) {
        int addr = ctx.getFunction((String) args[0]);
        out.add((byte) (addr & 0xFF));
        out.add((byte) ((addr >> 8) & 0xFF));
        out.add((byte) Integer.parseInt(args[1].toString()));
    }
}
