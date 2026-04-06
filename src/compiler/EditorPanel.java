package compiler;

import javax.swing.*;
import java.awt.*;

/** Editor de código com numeração de linhas e scrollbars sempre visíveis. */
public class EditorPanel {

    private static final Font EDITOR_FONT = new Font("Monospaced", Font.PLAIN, 13);

    private final JTextArea  textArea;
    private final JScrollPane scrollPane;

    public EditorPanel() {
        textArea = new JTextArea();
        textArea.setFont(EDITOR_FONT);
        textArea.setTabSize(4);

        scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        scrollPane.setRowHeaderView(new LineNumberPanel(textArea));
    }

    public JTextArea   textArea()   { return textArea; }
    public JScrollPane scrollPane() { return scrollPane; }
}
