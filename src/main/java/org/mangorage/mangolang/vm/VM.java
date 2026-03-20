package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.Terminal;

public final class VM {
    private final int[] code;
    private final InstructionSet instructionSet;
    private Terminal terminal = ConsoleTerminal.getInstance();

    public VM(int[] code, InstructionSet instructionSet) {
        this.code = code;
        this.instructionSet = instructionSet;
    }

    /**
     * Creates a new environment and runs the code.
     * This makes the VM perfectly re-runnable.
     */
    public void run() {
        VMEnvironment env = new VMEnvironment(this);
        env.start();
    }

    /**
     * Alternatively, allow external code to manage the environment life-cycle.
     */
    public VMEnvironment createEnvironment() {
        return new VMEnvironment(this);
    }

    public int[] getCode() {
        return code;
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