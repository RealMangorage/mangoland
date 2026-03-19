package org.mangorage.mangolang.instruction.impl.control;

import org.mangorage.mangolang.compiler.CompilerContext;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.vm.VM;
import java.util.List;

public class JumpIfFalse implements Instruction {
    public void execute(VM vm) {
        final var booleanValue = vm.getStack().pop();

        System.out.println(booleanValue);

        int addr = vm.next();

        if (booleanValue == 1) {
            vm.getCallStack().push(new VM.Frame(vm.ip, 256));
            vm.ip = addr;
        }
    }

    public int getArgCount() {
        return 1;
    }

    public void emitBytecode(List<Integer> out, CompilerContext ctx, Object... args) {
        out.add(ctx.getFunction((String) args[0]));
    }
}
