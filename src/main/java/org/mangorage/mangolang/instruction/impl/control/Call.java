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
        int addr = env.next();
        env.getCallStack().push(new Frame(env.getIp(), 0));
        env.setIp(addr);
    }

    public int getArgCount() {
        return 1;
    }

    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {
        out.add(ctx.getFunction((String) args[0]));
    }
}
