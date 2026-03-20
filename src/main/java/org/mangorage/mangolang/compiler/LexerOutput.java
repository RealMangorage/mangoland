package org.mangorage.mangolang.compiler;

public record LexerOutput(String name, boolean doContinue) {
    public LexerOutput(boolean doContinue) {
        this(null, doContinue);
    }
}
