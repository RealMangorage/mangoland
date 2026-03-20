package org.mangorage.mangolang;

import org.mangorage.mangolang.compiler.Compiler;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.instruction.impl.arithmetic.Add;
import org.mangorage.mangolang.instruction.impl.control.Call;
import org.mangorage.mangolang.instruction.impl.arithmetic.Decrement;
import org.mangorage.mangolang.instruction.impl.stack.Dup;
import org.mangorage.mangolang.instruction.impl.comparison.Equals;
import org.mangorage.mangolang.instruction.impl.comparison.GreaterThanZero;
import org.mangorage.mangolang.instruction.impl.control.Halt;
import org.mangorage.mangolang.instruction.impl.control.Jump;
import org.mangorage.mangolang.instruction.impl.control.JumpStatement;
import org.mangorage.mangolang.instruction.impl.memory.Let;
import org.mangorage.mangolang.instruction.impl.memory.Load;
import org.mangorage.mangolang.instruction.impl.arithmetic.Multiply;
import org.mangorage.mangolang.instruction.impl.output.Print;
import org.mangorage.mangolang.instruction.impl.output.PrintStr;
import org.mangorage.mangolang.instruction.impl.stack.Push;
import org.mangorage.mangolang.instruction.impl.control.Return;
import org.mangorage.mangolang.instruction.impl.timing.Sleep;
import org.mangorage.mangolang.instruction.impl.memory.Store;
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
                        Halt.class,
                        PrintStr.class,
                        Push.class,
                        Store.class,
                        Print.class,
                        Add.class,
                        Load.class,
                        Let.class,
                        Return.class,
                        Call.class,
                        Sleep.class,
                        Jump.class,
                        Dup.class,
                        Decrement.class,
                        Equals.class,
                        Multiply.class
                )
        );
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

        int[] bytecode = compiler.compile(program);

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