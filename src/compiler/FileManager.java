package compiler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** Gerencia as operações de arquivo: novo, abrir e salvar. */
public class FileManager {

    private static final FileNameExtensionFilter TXT_FILTER =
            new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt");

    private final Component    parent;
    private final EditorPanel  editor;
    private final MessagePanel messages;
    private final StatusBar    statusBar;

    private File currentFile = null;

    public FileManager(Component parent, EditorPanel editor, MessagePanel messages, StatusBar statusBar) {
        this.parent    = parent;
        this.editor    = editor;
        this.messages  = messages;
        this.statusBar = statusBar;
    }

    public void novo() {
        editor.textArea().setText("");
        messages.clear();
        statusBar.clear();
        currentFile = null;
    }

    public void abrir() {
        File file = showOpenDialog();
        if (file == null) return; // mantém estado anterior se cancelado

        try {
            editor.textArea().setText(new String(Files.readAllBytes(file.toPath())));
            editor.textArea().setCaretPosition(0);
            messages.clear();
            currentFile = file;
            statusBar.showPath(file.getAbsolutePath());
        } catch (IOException e) {
            showError("Erro ao abrir arquivo: " + e.getMessage());
        }
    }

    public void salvar() {
        if (currentFile == null) {
            salvarComo();
        } else {
            salvarNoArquivoAtual();
        }
    }

    private void salvarComo() {
        File file = showSaveDialog();
        if (file == null) return;

        file = withTxtExtension(file);

        try {
            Files.write(file.toPath(), editor.textArea().getText().getBytes());
            currentFile = file;
            messages.clear();
            statusBar.showPath(file.getAbsolutePath());
        } catch (IOException e) {
            showError("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    private void salvarNoArquivoAtual() {
        try {
            Files.write(currentFile.toPath(), editor.textArea().getText().getBytes());
            messages.clear();
            // barra de status mantida
        } catch (IOException e) {
            showError("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    private File showOpenDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(TXT_FILTER);
        return chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile() : null;
    }

    private File showSaveDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(TXT_FILTER);
        return chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION
                ? chooser.getSelectedFile() : null;
    }

    private File withTxtExtension(File file) {
        return file.getName().toLowerCase().endsWith(".txt")
                ? file : new File(file.getAbsolutePath() + ".txt");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(parent, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
