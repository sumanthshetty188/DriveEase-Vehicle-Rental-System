package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * UITheme - Centralized color palette and font definitions.
 * Gives the app a consistent, modern look.
 */
public class UITheme {

    // ── Colour Palette ─────────────────────────────────
    public static final Color PRIMARY        = new Color(41, 128, 185);   // Blue
    public static final Color PRIMARY_DARK   = new Color(21, 67, 96);
    public static final Color ACCENT         = new Color(39, 174, 96);    // Green
    public static final Color DANGER         = new Color(231, 76, 60);    // Red
    public static final Color WARNING        = new Color(243, 156, 18);   // Orange
    public static final Color SIDEBAR_BG     = new Color(28, 40, 51);
    public static final Color SIDEBAR_HOVER  = new Color(41, 60, 75);
    public static final Color CARD_BG        = Color.WHITE;
    public static final Color PAGE_BG        = new Color(236, 240, 241);
    public static final Color TABLE_HEADER   = new Color(52, 73, 94);
    public static final Color TABLE_ALT_ROW  = new Color(245, 248, 250);
    public static final Color TEXT_PRIMARY   = new Color(44, 62, 80);
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    // ───────────────────────────────────────────────────

    // ── Fonts ───────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD,  13);
    // ───────────────────────────────────────────────────

    /** Styled primary action button. */
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        addHoverEffect(btn, PRIMARY, PRIMARY_DARK);
        return btn;
    }

    /** Styled danger/delete button. */
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        addHoverEffect(btn, DANGER, new Color(192, 57, 43));
        return btn;
    }

    /** Styled success/green button. */
    public static JButton successButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        addHoverEffect(btn, ACCENT, new Color(30, 132, 73));
        return btn;
    }

    /** Styled warning/orange button. */
    public static JButton warningButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(WARNING);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 36));
        addHoverEffect(btn, WARNING, new Color(194, 117, 6));
        return btn;
    }

    private static void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(normal); }
        });
    }

    /** Rounded card panel. */
    public static JPanel cardPanel() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(cardBorder());
        return p;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        );
    }

    /** Styled text field. */
    public static JTextField styledField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return tf;
    }

    /** Styled password field. */
    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return pf;
    }

    /** Styled combo box. */
    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(FONT_BODY);
        cb.setBackground(Color.WHITE);
        return cb;
    }

    /** Styled table. */
    public static void styleTable(javax.swing.JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        // Alternate row coloring
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    javax.swing.JTable t, Object val,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, val, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    /** Set global Swing look-and-feel defaults. */
    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.messageFont", FONT_BODY);
            UIManager.put("OptionPane.buttonFont",  FONT_BUTTON);
        } catch (Exception ignored) {}
    }
}
