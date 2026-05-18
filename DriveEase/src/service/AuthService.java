package service;

import dao.UserDAO;
import model.User;
import util.PasswordUtil;
import util.ValidationUtil;

/**
 * AuthService - Business logic for login, logout, and password reset.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static User currentUser = null;

    /**
     * Attempts login. Returns authenticated User or null.
     */
    public User login(String username, String password) {
        if (ValidationUtil.isNullOrEmpty(username) ||
            ValidationUtil.isNullOrEmpty(password)) return null;
        User user = userDAO.authenticate(username.trim(), password);
        if (user != null) currentUser = user;
        return user;
    }

    public void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() { return currentUser; }

    /**
     * Initiates forgot-password: verifies username exists and returns the
     * security question.
     */
    public String getSecurityQuestion(String username) {
        User u = userDAO.findByUsername(username);
        return (u != null) ? u.getSecurityQuestion() : null;
    }

    /**
     * Resets password after verifying the security answer.
     * @return true if reset succeeded
     */
    public boolean resetPassword(String username, String answer, String newPassword) {
        if (!ValidationUtil.isValidPassword(newPassword)) return false;
        User u = userDAO.findByUsername(username);
        if (u == null) return false;
        if (!PasswordUtil.verify(answer.trim().toLowerCase(), u.getSecurityAnswer())) return false;
        String newHash = PasswordUtil.hash(newPassword);
        return userDAO.updatePassword(username, newHash);
    }

    /**
     * Seeds default admin if none exists (first-run setup).
     * Admin: admin / Admin@123
     */
    public void seedDefaultAdmin() {
        if (userDAO.countAdmins() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(PasswordUtil.hash("Admin@123"));
            admin.setRole("ADMIN");
            admin.setFullName("System Administrator");
            admin.setEmail("admin@driveease.com");
            admin.setSecurityQuestion("What is your pet name?");
            admin.setSecurityAnswer(PasswordUtil.hash("driveease"));
            userDAO.registerUser(admin);
            System.out.println("[AuthService] Default admin seeded.");
        }
    }
}
