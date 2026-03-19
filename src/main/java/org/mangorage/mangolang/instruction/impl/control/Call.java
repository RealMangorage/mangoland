package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.Frame;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public class Call implements Instruction {
    @Override
    public void execute(VM vm) {
        int addr = vm.next();
        vm.getCallStack().push(new Frame(vm.ip, 0));
        vm.ip = addr;
    }

    public int getArgCount() {
        return 1;
    }

    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {
        out.add(ctx.getFunction((String) args[0]));
    }
}
