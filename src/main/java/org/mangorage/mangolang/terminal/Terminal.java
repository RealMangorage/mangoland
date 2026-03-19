package org.mangorage.mangolang.terminal;

import javax.swing.*;
import java.awt.*;

public class Terminal {

    private static Terminal instance; // The singleton
    private final JFrame frame;
    private final JTextArea textArea;

    private Terminal() {
        frame = new JFrame("MangoLang Terminal");
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

    /** Static getter — creates the Terminal if it doesn’t exist */
    public static Terminal getInstance() {
        if (instance == null) {
            instance = new Terminal();
        }
        return instance;
    }

    /** Print text without newline */
    public void print(String text) {
        textArea.append(text);
    }

    /** Print text with newline */
    public void println(String text) {
        textArea.append(text + "\n");
    }

    /** Clear the screen */
    public void clear() {
        textArea.setText("");
    }
}