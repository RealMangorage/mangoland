package org.mangorage.mangolang.compiler;

import java.util.ArrayList;
import java.util.List;

// Helper class to track nested functions and loops
public final class BlockContext {
    public enum Type {
        FUNCTION,
        WHILE,
        IF
    }

    private Type type;
    private int startAddress;       // Where to jump back to (for loops) or skip to (for functions)
    private int condJumpAddress;    // The index of the jump_if_false placeholder
    private int elseJumpAddress = -1; // placeholder index for the unconditional jump over the else-body
    private final List<Integer> breaks = new ArrayList<>(); // Track all breaks in this loop

    public BlockContext(Type type, int startAddress) {
        this.type = type;
        this.startAddress = startAddress;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getCondJumpAddress() {
        return condJumpAddress;
    }

    public void setCondJumpAddress(int condJumpAddress) {
        this.condJumpAddress = condJumpAddress;
    }

    public int getElseJumpAddress() {
        return elseJumpAddress;
    }

    public void setElseJumpAddress(int elseJumpAddress) {
        this.elseJumpAddress = elseJumpAddress;
    }

    /** Add a break placeholder address to this block */
    public void addBreak(int addr) {
        breaks.add(addr);
    }

    /** Return the list of breaks (modifiable) */
    public List<Integer> getBreaks() {
        return breaks;
    }
}
