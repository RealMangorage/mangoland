package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

@AutoRegisterInstruction
public final class Multiply implements Instruction {
    @Override
    public void execute(VMEnvironment env) {
        int b = env.getStack().pop();
        int a = env.getStack().pop();
        env.getStack().push(a * b);
    }
    @Override
    public int getArgCount() { return 0; }
    @Override
    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {}
}