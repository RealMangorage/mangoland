package org.mangorage.mangolang.instruction.impl.timing;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Sleep implements Instruction {

    @Override
    public void execute(VMEnvironment env) {
        int durationMs = env.getStack().pop(); // pop the value from the stack
        try {
            Thread.sleep(durationMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Sleep interrupted!");
        }
    }

    @Override
    public int getArgCount() {
        return 1; // one argument: duration in ms
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
    }
}