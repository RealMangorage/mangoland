package org.mangorage.mangolang;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.instruction.impl.comparison.GreaterThanZero;
import org.mangorage.mangolang.instruction.impl.comparison.Equals;
import org.mangorage.mangolang.instruction.impl.control.Halt;
import org.mangorage.mangolang.instruction.impl.control.Jump;
import org.mangorage.mangolang.instruction.impl.control.JumpStatement;
import org.mangorage.mangolang.instruction.impl.control.Call;
import org.mangorage.mangolang.instruction.impl.control.Return;
import org.mangorage.mangolang.instruction.impl.memory.Let;
import org.mangorage.mangolang.instruction.impl.memory.Load;
import org.mangorage.mangolang.instruction.impl.memory.Store;
import org.mangorage.mangolang.instruction.impl.output.Print;
import org.mangorage.mangolang.instruction.impl.stack.Push;
import org.mangorage.mangolang.instruction.impl.stack.Dup;
import org.mangorage.mangolang.instruction.impl.arithmetic.Add;
import org.mangorage.mangolang.instruction.impl.arithmetic.Decrement;
import org.mangorage.mangolang.instruction.impl.arithmetic.Multiply;
import org.mangorage.mangolang.instruction.impl.timing.Sleep;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.DeferredTerminal;
import org.mangorage.mangolang.terminal.TerminalGui;
import org.mangorage.mangolang.vm.VM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        set.register(
                List.of(
                        Let.class,
                        Push.class,
                        Load.class,
                        Store.class,
                        Call.class,
                        Return.class,
                        Jump.class,
                        Print.class,
                        Add.class,
                        Decrement.class,
                        Multiply.class,
                        Equals.class,
                        Dup.class,
                        Sleep.class,
                        Halt.class
                )
        );
        // Alias for legacy/compiler token "printstr" -> use Print instruction
        set.register("printstr", new Print());
        set.register(
                "jump_if_true", new JumpStatement(1)
        );

        set.register(
                "jump_if_false", new JumpStatement(0)
        );

        set.register(
                "greater_than_zero", new GreaterThanZero()
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