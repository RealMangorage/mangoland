package org.mangorage.mangolang.terminal;

import java.util.List;

public final class DeferredTerminal implements Terminal {

    public static Terminal of(List<Terminal> terminals) {
        return new DeferredTerminal(terminals);
    }


    private final List<Terminal> terminals;

    DeferredTerminal(List<Terminal> terminals) {
        this.terminals = terminals;
    }


    @Override
    public void println(String text) {
        for (Terminal terminal : terminals) {
            terminal.println(text);
        }
    }
}
