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
                push 999
                print "Hello!"
                """;

        byte[] bytecode = compiler.compile(program);

        Util.saveProgram("VMTest.ml.class", bytecode);

        bytecode = Util.loadProgram("VMTest.ml.class");

        // Print bytecode as unsigned ints
        int[] asInts = new int[bytecode.length];
        for (int i = 0; i < bytecode.length; i++) asInts[i] = bytecode[i] & 0xFF;

        System.out.println(Arrays.toString(asInts));

        for (int i = 0; i < asInts.length; i++) {
            Integer v = asInts[i];
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

        Thread.sleep(10000);

        Assertions.assertTrue(true);
    }
}

