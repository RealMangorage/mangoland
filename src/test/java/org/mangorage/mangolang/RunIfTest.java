package org.mangorage.mangolang;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.DeferredTerminal;
import org.mangorage.mangolang.vm.VM;

import java.util.List;

public class RunIfTest {
    public static void main(String[] args) {
        Compiler compiler = new Compiler(MangoLang.createEnv());

        String program = """
                let x = 0

                if (x == 0) then
                    printstr "X is zero"
                else
                    printstr "X is not zero"
                end

                printstr "Hello, World!"
                """;

        int[] bytecode = compiler.compile(program);
        System.out.println("Bytecode: " + java.util.Arrays.toString(bytecode));
        InstructionSet set = MangoLang.createEnv();

        for (int i = 0; i < bytecode.length; i++) {
            Integer v = bytecode[i];
            String name = set.getName(v);
            if (name != null) System.out.printf("%04d: %d %s\n", i, v, name);
            else System.out.printf("%04d: %d\n", i, v);
        }

        VM vm = new VM(bytecode, set);
        vm.setTerminal(DeferredTerminal.of(List.of(ConsoleTerminal.getInstance())));
        vm.run();
    }
}



