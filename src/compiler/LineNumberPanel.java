package compiler;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

/**
 * Painel de numeração de linhas para um JTextComponent.
 * Destaca a linha atual em vermelho e se ajusta dinamicamente ao número de dígitos.
 */
public class LineNumberPanel extends JPanel
        implements CaretListener, DocumentListener, PropertyChangeListener {

    private static final int    MIN_DIGITS   = 3;
    private static final int    BORDER_GAP   = 5;
    private static final int    PANEL_HEIGHT = Integer.MAX_VALUE - 1_000_000;
    private static final Border OUTER_BORDER = new MatteBorder(0, 0, 0, 2, Color.GRAY);

    private final JTextComponent component;
    private final HashMap<String, FontMetrics> fontCache = new HashMap<>();

    private int lastDigits;
    private int lastHeight;
    private int lastLine;

    public LineNumberPanel(JTextComponent component) {
        this.component = component;
        setFont(component.getFont());
        setForeground(Color.BLACK);
        applyBorder();
        component.getDocument().addDocumentListener(this);
        component.addCaretListener(this);
        component.addPropertyChangeListener("font", this);
        updatePreferredWidth();
    }

    // ── Painting ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        FontMetrics fm      = component.getFontMetrics(component.getFont());
        Insets      insets  = getInsets();
        int         width   = getSize().width - insets.left - insets.right;
        Rectangle   clip    = g.getClipBounds();

        int startOffset = offsetAt(clip.y);
        int endOffset   = offsetAt(clip.y + clip.height);

        while (startOffset <= endOffset) {
            try {
                String label = lineLabel(startOffset);
                if (!label.isEmpty()) {
                    g.setColor(isCurrentLine(startOffset) ? Color.RED : getForeground());
                    int x = insets.left + (int) ((width - fm.stringWidth(label)) * 1.0f);
                    int y = baselineY(startOffset, fm);
                    g.drawString(label, x, y);
                }
                startOffset = Utilities.getRowEnd(component, startOffset) + 1;
            } catch (Exception e) {
                break;
            }
        }
    }

    private int offsetAt(int y) {
        int offset = (int) component.viewToModel2D(new Point(0, y));
        return offset < 0 ? 0 : offset;
    }

    private String lineLabel(int offset) {
        Element root  = component.getDocument().getDefaultRootElement();
        int     index = root.getElementIndex(offset);
        return root.getElement(index).getStartOffset() == offset ? String.valueOf(index + 1) : "";
    }

    private boolean isCurrentLine(int offset) {
        Element root = component.getDocument().getDefaultRootElement();
        return root.getElementIndex(offset) == root.getElementIndex(component.getCaretPosition());
    }

    private int baselineY(int offset, FontMetrics fm) throws BadLocationException {
        Rectangle r = component.modelToView(offset);
        int descent = (r.height == fm.getHeight()) ? fm.getDescent() : maxDescentForLine(offset);
        return r.y + r.height - descent;
    }

    private int maxDescentForLine(int offset) {
        Element root  = component.getDocument().getDefaultRootElement();
        Element line  = root.getElement(root.getElementIndex(offset));
        int     max   = 0;
        for (int i = 0; i < line.getElementCount(); i++) {
            AttributeSet attrs      = line.getElement(i).getAttributes();
            String       family     = (String)  attrs.getAttribute(StyleConstants.FontFamily);
            Integer      size       = (Integer) attrs.getAttribute(StyleConstants.FontSize);
            String       cacheKey   = family + size;
            FontMetrics  fm         = fontCache.computeIfAbsent(cacheKey,
                    k -> component.getFontMetrics(new Font(family, Font.PLAIN, size)));
            max = Math.max(max, fm.getDescent());
        }
        return max;
    }

    // ── Width ─────────────────────────────────────────────────────────────────

    private void updatePreferredWidth() {
        int lines  = component.getDocument().getDefaultRootElement().getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), MIN_DIGITS);

        if (digits == lastDigits) return;
        lastDigits = digits;

        int charWidth = getFontMetrics(getFont()).charWidth('0');
        Insets insets = getInsets();
        int preferred = insets.left + insets.right + charWidth * digits;

        Dimension d = getPreferredSize();
        d.setSize(preferred, PANEL_HEIGHT);
        setPreferredSize(d);
        setSize(d);
    }

    private void applyBorder() {
        setBorder(new CompoundBorder(OUTER_BORDER, new EmptyBorder(0, BORDER_GAP, 0, BORDER_GAP)));
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    @Override
    public void caretUpdate(CaretEvent e) {
        int line = component.getDocument().getDefaultRootElement()
                            .getElementIndex(component.getCaretPosition());
        if (line != lastLine) {
            lastLine = line;
            getParent().repaint();
        }
    }

    @Override public void insertUpdate(DocumentEvent e)  { onDocumentChanged(); }
    @Override public void removeUpdate(DocumentEvent e)  { onDocumentChanged(); }
    @Override public void changedUpdate(DocumentEvent e) { onDocumentChanged(); }

    private void onDocumentChanged() {
        SwingUtilities.invokeLater(() -> {
            try {
                Rectangle r = component.modelToView(component.getDocument().getLength());
                if (r != null && r.y != lastHeight) {
                    lastHeight = r.y;
                    updatePreferredWidth();
                    repaint();
                }
            } catch (BadLocationException ignored) {}
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() instanceof Font) repaint();
    }
}
