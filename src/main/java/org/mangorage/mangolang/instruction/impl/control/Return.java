package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public class Return implements Instruction {

    @Override
    public void execute(VM vm) {
        if (vm.callStack.size() <= 1) {
            throw new RuntimeException("Return with no caller");
        }

        VM.Frame frame = vm.callStack.pop();
        vm.ip = frame.returnIp;
    }

    @Override
    public int getArgCount() { return 0; }

    @Override
    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {

    }
}