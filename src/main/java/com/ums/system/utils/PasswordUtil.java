package com.ums.system.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt
 */
public class PasswordUtil {

    // BCrypt cost factor (higher = more secure but slower)
    private static final int BCRYPT_COST = 12;

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.withDefaults().hashToString(BCRYPT_COST, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }

        try {
            BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
            return result.verified;
        } catch (Exception e) {
            return false;
        }
    }
}

