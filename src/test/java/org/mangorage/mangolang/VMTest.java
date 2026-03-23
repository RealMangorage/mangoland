package org.mangorage.mangolang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.DeferredTerminal;
import org.mangorage.mangolang.vm.VM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VMTest {

    private final List<String> out = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        new VMTest().testPrintStrEmits();
    }

    @Test
    public void testPrintStrEmits() throws InterruptedException {
        // Compile and run a small program using the Compiler and createEnv
        final var env = MangoLang.createEnv();
        Compiler compiler = new Compiler(env);

        String program = """
                # Testing stuff!
                let x = 0
                                printstr "Hello, World!"
                
                function testFunc()  #Simple function here!
                    printstr "Func called"
                    return
                end
                
                call testFunc()
                
               
                
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


        Util.saveProgram("VMTest.ml.class", bytecode);

        bytecode = Util.loadProgram("VMTest.ml.class");

        System.out.println(
                Arrays.toString(
                        bytecode
                )
        );

        for (int i = 0; i < bytecode.length; i++) {
            Integer v = bytecode[i];
            String name = env.getName(v);
            if (name != null) System.out.printf("%04d: %d %s\n", i, v, name);
            else System.out.printf("%04d: %d\n", i, v);
        }


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

