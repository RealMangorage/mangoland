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
        int addr = (high << 8) | low;
        // Push a new frame that inherits the caller's locals so the function can access outer variables
        Frame caller = env.getCallStack().peek();
        int localSize = caller.locals.length;
        Frame newFrame = new Frame(env.getIp(), localSize);
        // Share caller locals with callee so functions can access / modify global variables
        newFrame.locals = caller.locals;
        env.getCallStack().push(newFrame);
        env.setIp(addr);
    }

    public void emitBytecode(List<Byte> out, CompilerContext ctx, Object... args) {
        int addr = ctx.getFunction((String) args[0]);
        out.add((byte) (addr & 0xFF));
        out.add((byte) ((addr >> 8) & 0xFF));
    }
}
