package org.mangorage.mangolang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.DeferredTerminal;
import org.mangorage.mangolang.vm.VM;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class VMTest {

    private final List<String> out = new ArrayList<>();

    @Test
    public void testPrintStrEmits() throws InterruptedException {
        // Compile and run a small program using the Compiler and createEnv
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

        VM vm = new VM(bytecode, MangoLang.createEnv());
        vm.setTerminal(
                DeferredTerminal.of(
                        List.of(
                                ConsoleTerminal.getInstance(),
                                out::add
                        )
                )
        );

        final var executors = Executors.newCachedThreadPool();

        for (int i = 0; i < 100; i++) {
            executors.submit(() -> vm.run());
        }

        Thread.sleep(10000);

        // Join outputs
        String joined = String.join("", out);
        Assertions.assertTrue(joined.contains("Hello, World!"), "Expected 'Hello, World!' in terminal output: " + joined);
    }
}

