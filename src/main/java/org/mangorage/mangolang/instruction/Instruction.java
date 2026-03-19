package org.mangorage.mangolang.instruction;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public interface Instruction {
    void execute(VM vm);

    int getArgCount();

    void emitBytecode(List<Integer> output, CompilerContext ctx, Object... args);
}
