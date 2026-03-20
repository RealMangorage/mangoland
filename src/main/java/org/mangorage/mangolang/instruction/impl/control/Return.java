package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.Frame;
import org.mangorage.mangolang.vm.VMEnvironment;

public final class Return implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        if (env.getCallStack().size() <= 1) {
            throw new RuntimeException("Return with no caller");
        }

        Frame frame = env.getCallStack().pop();
        env.setIp(frame.returnIp);
    }
}