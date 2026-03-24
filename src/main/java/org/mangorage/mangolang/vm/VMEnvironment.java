package org.mangorage.mangolang.vm;

import org.mangorage.mangolang.instruction.Instruction;
import org.mangorage.mangolang.object.MangolangObject;
import org.mangorage.mangolang.object.MangolangObjects;
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

    public byte next() {
        if (ip < 0 || ip >= code.length) {
            throw new RuntimeException("VM instruction pointer out of bounds: " + ip);
        }
        // return unsigned value of the byte
        return code[ip++];
    }

    /** Peek next byte (unsigned) without advancing the instruction pointer. Returns -1 if out of bounds. */
    public int peek() {
        if (ip < 0 || ip >= code.length) return -1;
        return code[ip] & 0xFF;
    }

    /**
     * Read a MangolangObject previously emitted into the bytecode.
     * Encoding:
     *  - [0xFF][tag][lenLo][lenHi][payload...]
     *  - tag 1: integer -> 4 byte big-endian payload
     *  - tag 2: string  -> UTF-8 payload
     *  - tag 3: boolean -> 1 byte payload (0 or 1)
     */
    public MangolangObject readObject() {
        int prefix = next() & 0xFF;
        if (prefix != MangolangObjects.OBJECT_PREFIX) {
            throw new RuntimeException("Expected object prefix " + MangolangObjects.OBJECT_PREFIX + " but found " + prefix + " at ip=" + (ip - 1));
        }

        int tag = next() & 0xFF;
        int len = (next() & 0xFF) | ((next() & 0xFF) << 8);

        byte[] payload = new byte[len];
        for (int i = 0; i < len; i++) {
            payload[i] = next();
        }

        try {
            return MangolangObjects.decode(tag, payload);
        } catch (RuntimeException exception) {
            throw new RuntimeException(exception.getMessage() + " at ip=" + (ip - 1), exception);
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