package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.register.AutoRegisterInstruction;
import org.mangorage.mangolang.vm.Frame;
import org.mangorage.mangolang.vm.VMEnvironment;

@AutoRegisterInstruction
public final class Return implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        if (env.getCallStack().size() <= 1) {
            // No caller frame: treat this as a top-level return and stop the VM
            env.setRunning(false);
            return;
        }

        Frame frame = env.getCallStack().pop();
        env.setIp(frame.returnIp);
    }
}