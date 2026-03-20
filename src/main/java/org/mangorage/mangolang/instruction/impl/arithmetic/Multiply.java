package org.mangorage.mangolang.instruction.impl.arithmetic;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.AutoRegisterInstruction;
import org.mangorage.mangolang.instruction.Instruction;
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
}