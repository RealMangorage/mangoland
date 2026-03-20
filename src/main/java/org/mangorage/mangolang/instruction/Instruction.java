package org.mangorage.mangolang.instruction;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.vm.VM;
import org.mangorage.mangolang.vm.VMEnvironment;

import java.util.List;

public interface Instruction {
    void execute(VMEnvironment env);

    int getArgCount();

    void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args);
}
