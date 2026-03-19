package org.mangorage.mangolang.instruction.impl.comparison;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public class GreaterThanZero implements Instruction {
    @Override
    public void execute(VM vm) {
        int val = vm.getStack().pop();
        vm.getStack().push(val > 0 ? 1 : 0);
    }
    @Override
    public int getArgCount() { return 0; }
    @Override
    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {}
}