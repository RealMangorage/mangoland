package org.mangorage.mangolang.instruction.impl.output;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public final class Print implements Instruction {

    @Override
    public void execute(VM vm) {
        int value = vm.getStack().pop(); // pop dynamically
        vm.getTerminal().println(value + "");
    }

    @Override
    public int getArgCount() {
        return 0; // no args
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {
        // Print takes no args, ignore anything passed
    }
}