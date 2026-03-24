package org.mangorage.mangolang;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.DeferredTerminal;
import org.mangorage.mangolang.terminal.TerminalGui;
import org.mangorage.mangolang.vm.VM;
import org.reflections.Reflections;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;

public final class MangoLang {

    public static String loadProgram(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read program file: " + path, e);
        }
    }

    public static InstructionSet createEnv() {
        InstructionSet set = new InstructionSet();

        Reflections reflections = new Reflections(
                "org.mangorage.mangolang.instruction.impl"
        );

        set.register(
                reflections.getSubTypesOf(Instruction.class).stream()
                        .sorted(Comparator.comparing(Class::getName))
                        .toList()
        );

        return set;
    }

    public static void main(String[] args) {
        final var env = createEnv();

        Compiler compiler = new Compiler(env);

        String program = loadProgram("example.ml");

        byte[] bytecode = compiler.compile(program);

        Util.saveProgram("example.ml.class", bytecode);

        bytecode = Util.loadProgram("example.ml.class");

        // Print as unsigned ints for readability
        int[] asInts = new int[bytecode.length];
        for (int i = 0; i < bytecode.length; i++) asInts[i] = bytecode[i] & 0xFF;

        System.out.println(Arrays.toString(asInts));

        for (int i = 0; i < asInts.length; i++) {
            Integer v = asInts[i];
            String name = env.getName(v);
            if (name != null) System.out.printf("%04d: %d %s\n", i, v, name);
            else System.out.printf("%04d: %d\n", i, v);
        }

        System.out.println("--- Instruction set mapping ---");
        for (int i = 1; i <= 40; i++) {
            String n = env.getName(i);
            if (n != null) System.out.printf("%02d: %s\n", i, n);
        }


        VM vm = new VM(env);

        vm.setTerminal(
                DeferredTerminal.of(
                        List.of(
                                TerminalGui.getInstance(),
                                ConsoleTerminal.getInstance()
                        )
                )
        );

        vm.run(bytecode);
    }
}