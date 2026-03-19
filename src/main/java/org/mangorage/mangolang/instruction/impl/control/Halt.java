package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.instruction.Instruction;

import java.util.List;

public class Halt implements Instruction {
    public void execute(VM vm) {
        vm.setRunning(false);
    }

    public int getArgCount() {
        return 0;
    }

    @Override
    public void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args) {

    }
}
