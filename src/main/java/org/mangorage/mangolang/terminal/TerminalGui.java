package org.mangorage.mangolang.terminal;

import javax.swing.*;
import java.awt.*;

public final class TerminalGui implements Terminal {

    private static TerminalGui instance; // The singleton

    private final JFrame frame;
    private final JTextArea textArea;

    private TerminalGui() {
        frame = new JFrame("MangoLang TerminalGui");
        textArea = new JTextArea();

        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.GREEN);

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);

        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /** Static getter — creates the TerminalGui if it doesn’t exist */
    public static TerminalGui getInstance() {
        if (instance == null) {
            instance = new TerminalGui();
        }
        return instance;
    }

    @Override
    public void println(String text) {
        textArea.append(text + "\n");
    }
}
