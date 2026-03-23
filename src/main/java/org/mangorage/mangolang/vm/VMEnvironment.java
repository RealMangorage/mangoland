package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.terminal.Terminal;
import java.util.Stack;

public final class VMEnvironment {
    private final VM vm;
    private final int[] code;
    // Stack stores MangolangObject values (wrappers for ints, strings, etc.)
    private final Stack<MangolangObject> stack = new Stack<>();
    private final Stack<Frame> callStack = new Stack<>();
    private int ip = 0;
    private boolean running = false;

    public VMEnvironment(VM vm, int[] code) {
        this.vm = vm;
        this.code = code;
    }

    public void start() {
        this.running = true;
        this.ip = 0;
        this.stack.clear();
        this.callStack.clear();
        // Initial entry frame
        this.callStack.push(new Frame(-1, 256));

        while (running && ip < code.length) {
            int currentIp = ip;
            int opcode = next();

            if (System.getProperty("mangolang.vmdebug") != null) {
                String name = vm.getInstructionSet().getName(opcode);
                System.out.printf("[VM] ip=%d opcode=%d %s stack=%s%n",
                        currentIp, opcode, (name == null ? "" : "(" + name + ")"), stack);
            }

            Instruction inst = vm.getInstructionSet().get(opcode);
            if (inst == null) {
                throw new RuntimeException("Unknown opcode " + opcode + " at " + currentIp);
            }

            // Instructions now take the Environment as the context
            inst.execute(this);
        }

        this.running = false;
    }

    public int next() {
        if (ip < 0 || ip >= code.length) {
            throw new RuntimeException("VM instruction pointer out of bounds: " + ip);
        }
        return code[ip++];
    }

    // Accessors for Instructions to use
    public Stack<MangolangObject> getStack() {
        return stack;
    }

    public Stack<Frame> getCallStack() {
        return callStack;
    }

    public Terminal getTerminal() {
        return vm.getTerminal();
    }

    public int getIp() {
        return ip;

    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public void setLocal(int index, MangolangObject value) {
        if (callStack.isEmpty()) throw new RuntimeException("No active frame");
        callStack.peek().locals[index] = value;
    }

    public MangolangObject getLocal(int index) {
        if (callStack.isEmpty()) throw new RuntimeException("No active frame");
        return callStack.peek().locals[index];
    }
}