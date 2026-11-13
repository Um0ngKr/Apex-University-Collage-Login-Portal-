package com.apex.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for password hashing and verification.
 * Uses BCrypt (org.mindrot.jbcrypt.BCrypt) with seamless fallback to salted SHA-256.
 */
public class PasswordUtil {

    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());
    private static boolean bCryptAvailable = false;

    static {
        try {
            Class.forName("org.mindrot.jbcrypt.BCrypt");
            bCryptAvailable = true;
        } catch (ClassNotFoundException e) {
            bCryptAvailable = false;
            LOGGER.info("jbcrypt not on classpath; fallback to SHA-256 with salt.");
        }
    }

    /**
     * Hashes plain text password.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            return "";
        }
        if (bCryptAvailable) {
            try {
                return org.mindrot.jbcrypt.BCrypt.hashpw(plainPassword, org.mindrot.jbcrypt.BCrypt.gensalt(10));
            } catch (Throwable t) {
                LOGGER.log(Level.WARNING, "BCrypt hash failed, using SHA-256 fallback", t);
            }
        }
        return hashSha256(plainPassword);
    }

    /**
     * Verifies plain text password against stored hash.
     * Supports:
     * - BCrypt ($2a$, $2b$, $2y$)
     * - Salted SHA-256 ($sha256$salt$hash)
     * - Plain SHA-256 hex
     * - Default initial fallback comparison
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || plainPassword.isEmpty() || storedHash.isEmpty()) {
            return false;
        }

        // 1. If stored hash starts with BCrypt prefix ($2a$, $2b$, $2y$)
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            if (bCryptAvailable) {
                try {
                    return org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, storedHash);
                } catch (IllegalArgumentException e) {
                    LOGGER.log(Level.WARNING, "Invalid BCrypt hash format: " + e.getMessage());
                    return false;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "BCrypt verification failed", e);
                    return false;
                }
            } else {
                LOGGER.severe("BCrypt hash encountered but org.mindrot.jbcrypt is not available on classpath!");
                return false;
            }
        }

        // 2. If stored hash uses custom salted format $sha256$salt$hash
        if (storedHash.startsWith("$sha256$")) {
            String[] parts = storedHash.split("\\$");
            if (parts.length == 4) {
                String salt = parts[2];
                String expectedHash = parts[3];
                String computed = computeSha256(salt + plainPassword);
                return computed.equals(expectedHash);
            }
        }

        // 3. Fallback: direct SHA-256 hex match (64 hex characters)
        if (storedHash.length() == 64 && storedHash.matches("^[a-fA-F0-9]+$")) {
            String directSha = computeSha256(plainPassword);
            return directSha.equalsIgnoreCase(storedHash);
        }

        return false;
    }

    private static String hashSha256(String password) {
        byte[] saltBytes = new byte[16];
        new SecureRandom().nextBytes(saltBytes);
        String salt = Base64.getEncoder().encodeToString(saltBytes);
        String hash = computeSha256(salt + password);
        return "$sha256$" + salt + "$" + hash;
    }

    public static String computeSha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
