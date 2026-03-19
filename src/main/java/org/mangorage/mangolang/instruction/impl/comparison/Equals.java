package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public class Equals implements Instruction {
    @Override
    public void execute(VM vm) {
        int b = vm.getStack().pop();
        int a = vm.getStack().pop();
        vm.getStack().push(a == b ? 1 : 0);
    }

    @Override
    public int getArgCount() {
        return 0;
    }

    @Override
    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {

    }
}
