package util;

import java.util.regex.Pattern;

/**
 * ValidationUtil - Centralized input validation helpers.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");   // Indian 10-digit mobile

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isPositiveDecimal(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Shows a styled error message (used across all panels).
     */
    public static void showError(java.awt.Component parent, String message) {
        javax.swing.JOptionPane.showMessageDialog(
            parent, message, "Validation Error",
            javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccess(java.awt.Component parent, String message) {
        javax.swing.JOptionPane.showMessageDialog(
            parent, message, "Success",
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
