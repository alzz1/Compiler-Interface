package compiler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CompilerWindow extends JFrame {

    private final EditorPanel  editor;
    private final MessagePanel messages;
    private final StatusBar    statusBar;
    private final FileManager  fileManager;

    public CompilerWindow() {
        editor     = new EditorPanel();
        messages   = new MessagePanel();
        statusBar  = new StatusBar();
        fileManager = new FileManager(this, editor, messages, statusBar);

        setTitle("Compilador");
        setSize(1500, 800);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new Toolbar(editor, fileManager, messages), BorderLayout.NORTH);
        add(buildSplitPane(), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        registerShortcuts();
    }

    private JSplitPane buildSplitPane() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor.scrollPane(), messages.scrollPane());
        split.setDividerLocation(500);
        split.setDividerSize(6);
        split.setResizeWeight(0.7);
        return split;
    }

    private void registerShortcuts() {
        bindKey(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK, "novo",     () -> fileManager.novo());
        bindKey(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK, "abrir",    () -> fileManager.abrir());
        bindKey(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK, "salvar",   () -> fileManager.salvar());
        bindKey(KeyEvent.VK_F7, 0,                        "compilar", () -> messages.showCompilar());
        bindKey(KeyEvent.VK_F1, 0,                        "equipe",   () -> messages.showEquipe());
    }

    private void bindKey(int keyCode, int modifiers, String name, Runnable action) {
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, modifiers);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, name);
        getRootPane().getActionMap().put(name, new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { action.run(); }
        });
    }
}
