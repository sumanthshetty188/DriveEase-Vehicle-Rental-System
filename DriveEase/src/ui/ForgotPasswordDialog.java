package ui;

import service.AuthService;
import util.PasswordUtil;
import util.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * ForgotPasswordDialog - 3-step password reset flow.
 * Step 1: Enter username
 * Step 2: Answer security question
 * Step 3: Enter + confirm new password
 */
public class ForgotPasswordDialog extends JDialog {

    private final AuthService authService;

    private JTextField     usernameField;
    private JLabel         questionLabel;
    private JTextField     answerField;
    private JPasswordField newPassField, confirmPassField;
    private JButton        nextBtn;
    private JPanel         step1Panel, step2Panel, step3Panel;
    private CardLayout     stepLayout;
    private JPanel         stepContainer;

    private String currentUsername;

    public ForgotPasswordDialog(JFrame parent, AuthService authService) {
        super(parent, "Reset Password", true);
        this.authService = authService;
        initUI();
    }

    private void initUI() {
        setSize(440, 340);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        JLabel title = new JLabel("🔒 Forgot Password");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        root.add(title, BorderLayout.NORTH);

        stepLayout    = new CardLayout();
        stepContainer = new JPanel(stepLayout);
        stepContainer.add(buildStep1(), "STEP1");
        stepContainer.add(buildStep2(), "STEP2");
        stepContainer.add(buildStep3(), "STEP3");
        root.add(stepContainer, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildStep1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        p.add(label("Enter your username:"));
        p.add(Box.createVerticalStrut(6));
        usernameField = UITheme.styledField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);
        p.add(usernameField);
        p.add(Box.createVerticalStrut(16));

        JButton btn = UITheme.primaryButton("Next →");
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> handleStep1());
        p.add(btn);
        return p;
    }

    private JPanel buildStep2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        questionLabel = new JLabel("Security Question:");
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        questionLabel.setForeground(UITheme.TEXT_PRIMARY);
        questionLabel.setAlignmentX(LEFT_ALIGNMENT);
        p.add(questionLabel);
        p.add(Box.createVerticalStrut(8));

        p.add(label("Your Answer:"));
        p.add(Box.createVerticalStrut(6));
        answerField = UITheme.styledField(20);
        answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        answerField.setAlignmentX(LEFT_ALIGNMENT);
        p.add(answerField);
        p.add(Box.createVerticalStrut(16));

        JButton btn = UITheme.primaryButton("Verify →");
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> handleStep2());
        p.add(btn);
        return p;
    }

    private JPanel buildStep3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);

        p.add(label("New Password (min 6 chars):"));
        p.add(Box.createVerticalStrut(4));
        newPassField = UITheme.styledPasswordField(20);
        newPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        newPassField.setAlignmentX(LEFT_ALIGNMENT);
        p.add(newPassField);
        p.add(Box.createVerticalStrut(10));

        p.add(label("Confirm Password:"));
        p.add(Box.createVerticalStrut(4));
        confirmPassField = UITheme.styledPasswordField(20);
        confirmPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        confirmPassField.setAlignmentX(LEFT_ALIGNMENT);
        p.add(confirmPassField);
        p.add(Box.createVerticalStrut(16));

        JButton btn = UITheme.successButton("Reset Password");
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> handleStep3());
        p.add(btn);
        return p;
    }

    private void handleStep1() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) { ValidationUtil.showError(this, "Enter your username."); return; }
        String question = authService.getSecurityQuestion(username);
        if (question == null) { ValidationUtil.showError(this, "Username not found."); return; }
        currentUsername = username;
        questionLabel.setText("<html><b>Q:</b> " + question + "</html>");
        stepLayout.show(stepContainer, "STEP2");
    }

    private void handleStep2() {
        String answer = answerField.getText().trim();
        if (answer.isEmpty()) { ValidationUtil.showError(this, "Enter your answer."); return; }
        // Quick verify just to proceed; final verification happens at reset
        stepLayout.show(stepContainer, "STEP3");
    }

    private void handleStep3() {
        String newPass    = new String(newPassField.getPassword());
        String confirmPass= new String(confirmPassField.getPassword());
        if (!ValidationUtil.isValidPassword(newPass)) {
            ValidationUtil.showError(this, "Password must be at least 6 characters."); return; }
        if (!newPass.equals(confirmPass)) {
            ValidationUtil.showError(this, "Passwords do not match."); return; }

        boolean ok = authService.resetPassword(
            currentUsername, answerField.getText().trim().toLowerCase(), newPass);
        if (ok) {
            ValidationUtil.showSuccess(this, "Password reset successfully! Please login.");
            dispose();
        } else {
            ValidationUtil.showError(this, "Security answer is incorrect.");
        }
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_SMALL);
        l.setForeground(UITheme.TEXT_PRIMARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }
}
