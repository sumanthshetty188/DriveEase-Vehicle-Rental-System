package dao;

import db.DBConnection;
import model.User;
import util.PasswordUtil;

import java.sql.*;

/**
 * UserDAO - All database operations for the users table.
 */
public class UserDAO {

    /** Authenticate user by username + password. Returns null if invalid. */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password_hash");
                if (PasswordUtil.verify(password, stored)) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setRole(rs.getString("role"));
                    u.setFullName(rs.getString("full_name"));
                    u.setEmail(rs.getString("email"));
                    u.setSecurityQuestion(rs.getString("security_question"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Auth error: " + e.getMessage());
        }
        return null;
    }

    /** Finds user by username for password reset flow. */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setSecurityQuestion(rs.getString("security_question"));
                u.setSecurityAnswer(rs.getString("security_answer"));
                return u;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByUsername error: " + e.getMessage());
        }
        return null;
    }

    /** Updates password hash for a given username. */
    public boolean updatePassword(String username, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updatePassword error: " + e.getMessage());
            return false;
        }
    }

    /** Registers a new user account (USER role). */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, " +
                     "security_question, security_answer) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getSecurityQuestion());
            ps.setString(7, user.getSecurityAnswer());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerUser error: " + e.getMessage());
            return false;
        }
    }

    /** Counts total staff/admin users. */
    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] countAdmins error: " + e.getMessage());
        }
        return 0;
    }
}
