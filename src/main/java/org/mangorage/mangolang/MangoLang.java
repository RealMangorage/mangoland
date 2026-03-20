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
import org.mangorage.mangolang.instruction.impl.control.JumpInst;
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
                "jump_if_true", new JumpInst(1)
        );

        set.register(
                "jump_if_false", new JumpInst(0)
        );

        set.register(
                "greater_than_zero", new GreaterThanZero()
        );

        return set;
    }

    public static void main(String[] args) {
        Compiler compiler = new Compiler(createEnv());

        String program = loadProgram("example.ml");

        int[] bytecode = compiler.compile(program);

        VM vm = new VM(bytecode, createEnv());
        vm.setTerminal(
                DeferredTerminal.of(
                        List.of(
                                TerminalGui.getInstance(),
                                ConsoleTerminal.getInstance()
                        )
                )
        );
        vm.run();
    }
}