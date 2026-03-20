package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Halt implements Instruction {
    public void execute(VMEnvironment env) {
        env.setRunning(false);
    }

    public int getArgCount() {
        return 0;
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {

    }
}
