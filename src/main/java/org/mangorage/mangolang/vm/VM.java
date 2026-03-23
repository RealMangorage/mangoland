package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.Terminal;

public final class VM {
    private final InstructionSet instructionSet;
    private Terminal terminal = ConsoleTerminal.getInstance();

    public VM(InstructionSet instructionSet) {
        this.instructionSet = instructionSet;
    }

    /**
     * Creates a new environment and runs the code.
     * This makes the VM perfectly re-runnable.
     */
    public void run(byte[] code) {
        VMEnvironment env = new VMEnvironment(this, code);
        env.start();
    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}