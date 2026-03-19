package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.instruction.InstructionSet;
import org.mangorage.mangolang.terminal.Terminal;

import java.util.Stack;

public final class VM {
    private final int[] code;
    public int ip = 0;

    private final Stack<Integer> stack = new Stack<>();
    public final Stack<Frame> callStack = new Stack<>();

    private boolean running = true;
    private final InstructionSet set;

    public VM(int[] code, InstructionSet set) {
        this.code = code;
        this.set = set;
    }

    public static class Frame {
        public int returnIp;
        public int[] locals;

        public Frame(int returnIp, int localSize) {
            this.returnIp = returnIp;
            this.locals = new int[localSize];
        }
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
        return Terminal.getInstance();
    }

    public Stack<Frame> getCallStack() {
        return callStack;
    }

    public void setLocal(int index, int value) {
        callStack.peek().locals[index] = value;
    }

    public int getLocal(int index) {
        return callStack.peek().locals[index];
    }

    public void setRunning(boolean running) {
        this.running = running;
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