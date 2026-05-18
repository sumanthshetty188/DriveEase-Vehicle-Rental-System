package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton database connection manager.
 * Provides a single shared JDBC connection to MySQL.
 */
public class DBConnection {

    // ── Configure these to match your local MySQL setup ──
    private static final String URL      = "jdbc:mysql://localhost:3306/driveease_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "Sumanth@1222";
    // ─────────────────────────────────────────────────────

    private static Connection connection = null;

    /** Returns a singleton Connection instance. */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL Driver not found: " + e.getMessage());
            throw new RuntimeException("MySQL Driver missing. Add mysql-connector-j to lib/", e);
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed. Check credentials.", e);
        }
        return connection;
    }

    /** Closes the connection if open. Call on application exit. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
