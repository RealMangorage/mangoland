package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.ConsoleTerminal;
import org.mangorage.mangolang.terminal.Terminal;
import java.util.Stack;

public final class VM {
    private final int[] code;
    public int ip = 0;

    private final Stack<Integer> stack = new Stack<>();
    private final Stack<Frame> callStack = new Stack<>();
    private final InstructionSet set;

    private Terminal terminal = ConsoleTerminal.getInstance();
    private boolean running = true;

    public VM(int[] code, InstructionSet set) {
        this.code = code;
        this.set = set;
    }

    public int next() {
        if (ip < 0 || ip >= code.length) {
            throw new RuntimeException("VM instruction pointer out of bounds: " + ip);
        }
        return code[ip++];
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public Stack<Frame> getCallStack() {
        return callStack;
    }

    public void setLocal(int index, int value) {
        callStack.firstElement().locals[index] = value;
    }

    public int getLocal(int index) {
        return callStack.firstElement().locals[index];
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public void run() {
        callStack.push(new Frame(-1, 256)); // main frame

        while (running && ip < code.length) {
            int opcode = next();
            Instruction inst = set.get(opcode);
            if (inst == null) throw new RuntimeException("Unknown opcode " + opcode);
            inst.execute(this);
        }
    }
}