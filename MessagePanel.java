package compiler;

import javax.swing.*;
import java.awt.*;

/** Área de mensagens somente leitura com scrollbars sempre visíveis. */
public class MessagePanel {

    private static final String MSG_COMPILAR = "compilação de programas ainda não foi implementada";
    private static final String MSG_EQUIPE   =
            "Equipe de desenvolvimento:\nAndré Luiz \nMAtheus Cordeiro \nMiguel Muller ";

    private final JTextArea  textArea;
    private final JScrollPane scrollPane;

    public MessagePanel() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    public void clear()          { textArea.setText(""); }
    public void showCompilar()   { textArea.setText(MSG_COMPILAR); }
    public void showEquipe()     { textArea.setText(MSG_EQUIPE); }

    public JScrollPane scrollPane() { return scrollPane; }
}
