package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.terminal.Terminal;
import java.util.Stack;

public final class VMEnvironment {
    private final VM vm;
    private final byte[] code;
    // Stack stores MangolangObject values (wrappers for ints, strings, etc.)
    private final Stack<MangolangObject> stack = new Stack<>();
    private final Stack<Frame> callStack = new Stack<>();
    private int ip = 0;
    private boolean running = false;

    public VMEnvironment(VM vm, byte[] code) {
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
                System.out.printf("[VM] ip=%d opcode=%d %s stack=%s%n", currentIp, opcode, (name == null ? "" : "(" + name + ")"), stack);
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
        // return unsigned value of the byte
        return code[ip++] & 0xFF;
    }

    /**
     * Read a MangolangObject previously emitted into the bytecode.
     * Encoding:
     *  - tag 1: integer -> [tag=1][len=4][4 bytes big-endian]
     *  - tag 2: string  -> [tag=2][len<=64][len bytes]
     */
    public org.mangorage.mangolang.object.MangolangObject readObject() {
        int tag = next();
        if (tag == 1) {
            int len = next(); // expected 4
            int b1 = next();
            int b2 = next();
            int b3 = next();
            int b4 = next();
            int val = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
            return new org.mangorage.mangolang.object.impl.IntegerMLObject(val);
        } else if (tag == 2) {
            int len = next();
            byte[] bytes = new byte[len];
            for (int i = 0; i < len; i++) bytes[i] = (byte) next();
            return new org.mangorage.mangolang.object.impl.StringMLObject(new String(bytes));
        } else {
            throw new RuntimeException("Unknown object tag: " + tag + " at ip=" + (ip - 1));
        }
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