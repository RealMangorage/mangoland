package org.mangorage.mangolang.terminal;

public final class ConsoleTerminal implements Terminal {

    private static final Terminal INSTANCE = new ConsoleTerminal();

    public static Terminal getInstance() {
        return INSTANCE;
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }
}
