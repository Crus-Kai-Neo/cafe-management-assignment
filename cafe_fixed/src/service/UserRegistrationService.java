package service;

import dao.UserDAO;
import model.Role;
import model.User;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class UserRegistrationService {
    private final UserDAO userDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final int MIN_PASSWORD_LENGTH = 6;

    public UserRegistrationService(UserDAO userDAO) { this.userDAO = userDAO; }

    public RegistrationResult register(String username, String email, String password, String confirmPassword) {
        if (username == null || username.isBlank())
            return fail("Username is required.");
        if (username.length() < 3)
            return fail("Username must be at least 3 characters long.");
        if (username.length() > 50)
            return fail("Username must not exceed 50 characters.");
        if (!username.matches("^[a-zA-Z0-9_]+$"))
            return fail("Username can only contain letters, numbers, and underscores.");
        if (email == null || email.isBlank())
            return fail("Email is required.");
        if (!EMAIL_PATTERN.matcher(email).matches())
            return fail("Please enter a valid email address.");
        if (password == null || password.isBlank())
            return fail("Password is required.");
        if (password.length() < MIN_PASSWORD_LENGTH)
            return fail("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        if (!password.matches(".*[A-Za-z].*"))
            return fail("Password must contain at least one letter.");
        if (!password.matches(".*\\d.*"))
            return fail("Password must contain at least one number.");
        if (confirmPassword == null || confirmPassword.isBlank())
            return fail("Please confirm your password.");
        if (!password.equals(confirmPassword))
            return fail("Passwords do not match.");

        try {
            if (userDAO.usernameExists(username))
                return fail("Username already exists. Please choose a different one.");
        } catch (SQLException e) { return fail("Database error. Please try again."); }

        try {
            if (userDAO.emailExists(email))
                return fail("Email already registered. Please use a different email or login.");
        } catch (SQLException e) { return fail("Database error. Please try again."); }

        try {
            User newUser = new User(username, password, email, Role.CUSTOMER);
            int userId   = userDAO.create(newUser);
            if (userId > 0) {
                User registered = new User(userId, username, password, email, Role.CUSTOMER);
                return new RegistrationResult(true, "Registration successful! You can now log in.", registered);
            }
            return fail("Failed to create user. Please try again.");
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return fail("Database error. Please try again.");
        }
    }

    private RegistrationResult fail(String msg) { return new RegistrationResult(false, msg); }

    public static class RegistrationResult {
        private final boolean success;
        private final String  message;
        private final User    user;

        public RegistrationResult(boolean success, String message)           { this(success, message, null); }
        public RegistrationResult(boolean success, String message, User user) {
            this.success = success; this.message = message; this.user = user;
        }

        public boolean isSuccess()  { return success; }
        public String getMessage()  { return message; }
        public User getUser()       { return user; }
    }
}