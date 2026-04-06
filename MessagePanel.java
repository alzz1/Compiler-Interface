package compiler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Área de mensagens somente leitura com scrollbars sempre visíveis. */
public class MessagePanel {

    private static final String MSG_EQUIPE =
            "Equipe de desenvolvimento:\nAndré Luiz\nMatheus Cordeiro\nMiguel Muller";

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

    public void clear()        { textArea.setText(""); }
    public void showEquipe()   { textArea.setText(MSG_EQUIPE); }

    /** Exibe resultado de compilação com sucesso: lista de tokens + mensagem final. */
    public void showTokens(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-22s %s%n", "linha", "classe", "lexema"));
        sb.append("-".repeat(55)).append("\n");
        for (Token t : tokens) {
            sb.append(String.format("%-8d %-22s %s%n", t.linha, t.classe.descricao(), t.lexema));
        }
        sb.append("\nprograma compilado com sucesso");
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }

    /** Exibe mensagem de erro léxico. */
    public void showErro(String mensagem) {
        textArea.setText(mensagem);
        textArea.setCaretPosition(0);
    }

    public JScrollPane scrollPane() { return scrollPane; }
}