package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtil - SHA-256 password hashing utility.
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     * @param plainText raw password
     * @return 64-character hex string
     */
    public static String hash(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plainText.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available.", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     */
    
    /**public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);*/
    public static boolean verify(String plainPassword, String storedPassword) {
        return plainPassword.equals(storedPassword);
    }
    
    }

