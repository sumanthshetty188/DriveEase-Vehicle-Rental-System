package ui;

import model.User;
import service.AuthService;
import util.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginFrame - Application entry point UI.
 * Features: Admin/User login, password show/hide, forgot password link.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JCheckBox      showPassCB;

    public LoginFrame() {
        authService.seedDefaultAdmin();   // Ensure admin exists
        initUI();
    }

    private void initUI() {
        setTitle("DriveEase – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Root split panel ─────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildFormPanel(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    /** Left blue brand panel. */
    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.PRIMARY_DARK);
        panel.setPreferredSize(new Dimension(380, 560));
        panel.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("🚗");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("DriveEase");
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Vehicle Rental System");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(new Color(189, 215, 238));
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><center>Drive Smart.<br>Rent Easy.</center></html>");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tagline.setForeground(new Color(149, 165, 166));
        tagline.setAlignmentX(CENTER_ALIGNMENT);
        tagline.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(title);
        inner.add(Box.createVerticalStrut(6));
        inner.add(sub);
        inner.add(tagline);

        panel.add(inner);
        return panel;
    }

    /** Right white login form panel. */
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(380, 400));

        JLabel heading = new JLabel("Welcome Back 👋");
        heading.setFont(UITheme.FONT_TITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        heading.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subHeading = new JLabel("Sign in to your account");
        subHeading.setFont(UITheme.FONT_BODY);
        subHeading.setForeground(UITheme.TEXT_SECONDARY);
        subHeading.setAlignmentX(LEFT_ALIGNMENT);

        form.add(heading);
        form.add(Box.createVerticalStrut(4));
        form.add(subHeading);
        form.add(Box.createVerticalStrut(28));

        // Username
        form.add(fieldLabel("Username"));
        usernameField = UITheme.styledField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(usernameField);
        form.add(Box.createVerticalStrut(14));

        // Password
        form.add(fieldLabel("Password"));
        passwordField = UITheme.styledPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(8));

        // Show password checkbox
        showPassCB = new JCheckBox("Show password");
        showPassCB.setFont(UITheme.FONT_SMALL);
        showPassCB.setBackground(Color.WHITE);
        showPassCB.setForeground(UITheme.TEXT_SECONDARY);
        showPassCB.setAlignmentX(LEFT_ALIGNMENT);
        showPassCB.addActionListener(e -> {
            passwordField.setEchoChar(showPassCB.isSelected() ? (char) 0 : '•');
        });
        form.add(showPassCB);
        form.add(Box.createVerticalStrut(16));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(8));

        // Login button
        JButton loginBtn = UITheme.primaryButton("  Sign In  ");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(LEFT_ALIGNMENT);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginBtn.addActionListener(e -> handleLogin());
        form.add(loginBtn);
        form.add(Box.createVerticalStrut(14));

        // Forgot password link
        JLabel forgotLink = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotLink.setFont(UITheme.FONT_SMALL);
        forgotLink.setForeground(UITheme.PRIMARY);
        forgotLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLink.setAlignmentX(LEFT_ALIGNMENT);
        forgotLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordDialog(LoginFrame.this, authService).setVisible(true);
            }
        });
        form.add(forgotLink);

        // Default credentials hint
        form.add(Box.createVerticalStrut(20));
        JLabel hint = new JLabel("<html><small>Default: <b>admin</b> / <b>Admin@123</b></small></html>");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_SECONDARY);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        form.add(hint);

        // Enter key triggers login
        passwordField.addActionListener(e -> handleLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        panel.add(form);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (ValidationUtil.isNullOrEmpty(username)) {
            errorLabel.setText("⚠ Please enter your username.");
            return;
        }
        if (ValidationUtil.isNullOrEmpty(password)) {
            errorLabel.setText("⚠ Please enter your password.");
            return;
        }

        User user = authService.login(username, password);
        if (user != null) {
            errorLabel.setText(" ");
            dispose();
            new DashboardFrame(user).setVisible(true);
        } else {
            errorLabel.setText("⚠ Invalid username or password.");
            passwordField.setText("");
        }
    }

    public static void main(String[] args) {
        UITheme.applyGlobalTheme();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
