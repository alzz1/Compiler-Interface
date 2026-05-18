package compiler;

import javax.swing.*;
import java.awt.*;

public class MessagePanel {

    private static final String MSG_EQUIPE =
            "Equipe de desenvolvimento:\nAndré Luiz\nMatheus Cordeiro\nMiguel Muller";

    private static final String MSG_SUCESSO = "programa compilado com sucesso";

    private final JTextArea   textArea;
    private final JScrollPane scrollPane;

    public MessagePanel() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    public void clear()       { textArea.setText(""); }
    public void showEquipe()  { textArea.setText(MSG_EQUIPE); }
    public void showSucesso() { textArea.setText(MSG_SUCESSO); }

    public void showErro(String mensagem) {
        textArea.setText(mensagem);
        textArea.setCaretPosition(0);
    }

    public JScrollPane scrollPane() { return scrollPane; }
}