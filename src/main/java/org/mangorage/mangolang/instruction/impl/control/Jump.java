package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public class Jump implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        // Update the instruction pointer to the argument provided
        env.setIp(env.next());
    }

    @Override
    public int getArgCount() { return 1; }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        // Handled directly by the Compiler control flow
    }
}