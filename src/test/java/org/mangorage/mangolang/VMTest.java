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

    public static void main(String[] args) throws InterruptedException {
        new VMTest().testPrintStrEmits();
    }

    @Test
    public void testPrintStrEmits() throws InterruptedException {
        // Compile and run a small program using the Compiler and createEnv
        Compiler compiler = new Compiler(MangoLang.createEnv());

        String program = """
                # Testing stuff!
                let x = 0
                
                function testFunc()  # Simple function here!
                    printstr "Func called"
                end
                
                call testFunc()
                
                
                printstr "Hello, World!"
                
                if (x == 0) then
                    printstr "X is zero"
                else
                    printstr "X is not zero"
                end
                
                if (x != 1) then
                    printstr "X is not one"
                end
                
                # Testing stuff!
                let y = 50
                
                while do
                    
                    if (y == 0) then
                        break
                    end
                    
                    call testFunc()
                    
                    load y
                    decrement
                    store y
               
                end
                
                printstr "Ended"
                """;

        int[] bytecode = compiler.compile(program);

        VM vm = new VM(MangoLang.createEnv());
        vm.setTerminal(
                DeferredTerminal.of(
                        List.of(
                                ConsoleTerminal.getInstance(),
                                out::add
                        )
                )
        );

        vm.run(bytecode);

        // Join outputs
        String joined = String.join("\n", out);
        Assertions.assertTrue(joined.contains("Hello, World!"), "Expected 'Hello, World!' in terminal output: " + joined);
        Assertions.assertTrue(joined.contains("X is zero"), "Expected 'X is zero' in terminal output: " + joined);
    }
}

