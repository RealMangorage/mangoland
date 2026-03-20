package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.Frame;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public final class Return implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        if (env.getCallStack().size() <= 1) {
            throw new RuntimeException("Return with no caller");
        }

        Frame frame = env.getCallStack().pop();
        env.setIp(frame.returnIp);
    }

    @Override
    public int getArgCount() { return 0; }

    @Override
    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {

    }
}