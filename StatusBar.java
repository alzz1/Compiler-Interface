package compiler;

import javax.swing.*;
import java.awt.*;

/** Barra de status que exibe o caminho do arquivo aberto. */
public class StatusBar extends JLabel {

    public StatusBar() {
        setText(" ");
        setPreferredSize(new Dimension(0, 25));
        setBorder(BorderFactory.createEtchedBorder());
        setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    public void clear()              { setText(" "); }
    public void showPath(String path) { setText(" " + path); }
}
