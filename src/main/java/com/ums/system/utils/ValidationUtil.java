package com.ums.system.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!*]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    public static String getPasswordRequirements() {
        return "Password Requirements:\n" +
               "  - Minimum 8 characters\n" +
               "  - At least one uppercase letter (A-Z)\n" +
               "  - At least one lowercase letter (a-z)\n" +
               "  - At least one digit (0-9)\n" +
               "  - At least one special character (@#$%^&+=!*)";
    }


    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format. Please use format: user@example.com";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit.";
        }
        if (!password.matches(".*[@#$%^&+=!*].*")) {
            return "Password must contain at least one special character (@#$%^&+=!*).";
        }
        return null;
    }

}

