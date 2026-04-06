package compiler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Toolbar extends JToolBar {

    private static final Dimension BUTTON_SIZE = new Dimension(90, 60);
    private static final Font      BUTTON_FONT = new Font("SansSerif", Font.PLAIN, 10);

    private final EditorPanel  editor;
    private final FileManager  fileManager;
    private final MessagePanel messages;

    public Toolbar(EditorPanel editor, FileManager fileManager, MessagePanel messages) {
        this.editor      = editor;
        this.fileManager = fileManager;
        this.messages    = messages;

        setFloatable(false);
        setPreferredSize(new Dimension(0, 70));

        addButton("novo [ctrl-n]",     Icons.NEW,     () -> fileManager.novo());
        addButton("abrir [ctrl-o]",    Icons.OPEN,    () -> fileManager.abrir());
        addButton("salvar [ctrl-s]",   Icons.SAVE,    () -> fileManager.salvar());
        addSeparator();
        addButton("copiar [ctrl-c]",   Icons.COPY,    () -> editor.textArea().copy());
        addButton("colar [ctrl-v]",    Icons.PASTE,   () -> editor.textArea().paste());
        addButton("recortar [ctrl-x]", Icons.CUT,     () -> editor.textArea().cut());
        addSeparator();
        addButton("compilar [F7]",     Icons.COMPILE, this::compilar);
        addSeparator();
        addButton("equipe [F1]",       Icons.TEAM,    () -> messages.showEquipe());
    }

    private void compilar() {
        String fonte = editor.textArea().getText();
        AnalisadorLexico lexer = new AnalisadorLexico(fonte);
        try {
            List<Token> tokens = lexer.analisar();
            messages.showTokens(tokens);
        } catch (ErroLexico e) {
            messages.showErro(e.getMessage());
        }
    }

    private void addButton(String label, Icons icon, Runnable action) {
        JButton button = new JButton(Icons.get(icon));
        button.setText("<html><center>" + label + "</center></html>");
        button.setFont(BUTTON_FONT);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);
        button.setToolTipText(label);
        button.addActionListener(e -> action.run());
        add(button);
    }

    enum Icons {
        NEW, OPEN, SAVE, COPY, PASTE, CUT, COMPILE, TEAM;

        static Icon get(Icons type) {
            BufferedImage img = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            draw(g, type);
            g.dispose();
            return new ImageIcon(img);
        }

        private static void draw(Graphics2D g, Icons type) {
            switch (type) {
                case NEW -> {
                    g.setColor(new Color(70, 130, 180));
                    g.fillRect(4, 2, 12, 16);
                    g.setColor(Color.WHITE);
                    g.fillPolygon(new int[]{12, 16, 16}, new int[]{2, 6, 2}, 3);
                    g.setColor(new Color(50, 100, 150));
                    g.drawRect(4, 2, 12, 16);
                    g.drawLine(12, 2, 16, 6);
                    g.drawLine(12, 6, 16, 6);
                    g.drawLine(12, 2, 12, 6);
                }
                case OPEN -> {
                    g.setColor(new Color(210, 180, 50));
                    g.fillRect(2, 8, 20, 12);
                    g.fillRect(2, 6, 10, 4);
                    g.setColor(new Color(160, 130, 30));
                    g.drawRect(2, 8, 20, 12);
                    g.drawRect(2, 6, 10, 4);
                }
                case SAVE -> {
                    g.setColor(new Color(70, 130, 180));
                    g.fillRect(2, 2, 20, 20);
                    g.setColor(Color.WHITE);
                    g.fillRect(6, 2, 8, 8);
                    g.setColor(new Color(200, 200, 200));
                    g.fillRect(5, 12, 14, 8);
                    g.setColor(new Color(50, 100, 150));
                    g.drawRect(2, 2, 20, 20);
                }
                case COPY -> {
                    g.setColor(new Color(100, 180, 100));
                    g.fillRect(6, 2, 12, 14);
                    g.fillRect(2, 6, 12, 14);
                    g.setColor(Color.WHITE);
                    g.fillRect(3, 7, 10, 12);
                    g.setColor(new Color(60, 140, 60));
                    g.drawRect(6, 2, 12, 14);
                    g.drawRect(2, 6, 12, 14);
                }
                case PASTE -> {
                    g.setColor(new Color(180, 100, 180));
                    g.fillRect(8, 0, 8, 4);
                    g.fillRect(4, 4, 16, 18);
                    g.setColor(Color.WHITE);
                    g.fillRect(6, 8, 12, 12);
                    g.setColor(new Color(140, 60, 140));
                    g.drawRect(4, 4, 16, 18);
                    g.drawRect(8, 0, 8, 4);
                }
                case CUT -> {
                    g.setColor(new Color(200, 80, 80));
                    g.setStroke(new BasicStroke(3));
                    g.drawLine(4, 4, 20, 20);
                    g.drawLine(4, 20, 20, 4);
                    g.fillOval(2, 18, 6, 6);
                    g.fillOval(16, 18, 6, 6);
                }
                case COMPILE -> {
                    g.setColor(new Color(80, 180, 80));
                    g.fillPolygon(new int[]{4, 20, 4}, new int[]{2, 12, 22}, 3);
                    g.setColor(new Color(40, 140, 40));
                    g.drawPolygon(new int[]{4, 20, 4}, new int[]{2, 12, 22}, 3);
                }
                case TEAM -> {
                    g.setColor(new Color(70, 130, 180));
                    g.fillOval(8, 2, 8, 8);
                    g.fillOval(2, 8, 6, 6);
                    g.fillOval(16, 8, 6, 6);
                    g.fillArc(6, 12, 12, 10, 0, 180);
                    g.fillArc(0, 16, 10, 8, 0, 180);
                    g.fillArc(14, 16, 10, 8, 0, 180);
                }
            }
        }
    }
}