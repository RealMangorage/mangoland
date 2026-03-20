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

    public Type type;
    public int startAddress;       // Where to jump back to (for loops) or skip to (for functions)
    public int condJumpAddress;    // The index of the jump_if_false placeholder
    public int elseJumpAddress = -1; // placeholder index for the unconditional jump over the else-body
    public List<Integer> breaks = new ArrayList<>(); // Track all breaks in this loop

    public BlockContext(Type type, int startAddress) {
        this.type = type;
        this.startAddress = startAddress;
    }
}
