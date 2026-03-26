package service;

import dao.UserDAO;
import model.Role;
import model.User;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class UserRegistrationService {
    private final UserDAO userDAO;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final int MIN_PASSWORD_LENGTH = 6;
    
    public UserRegistrationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    public RegistrationResult register(String username, String email, String password, String confirmPassword) {
        // Validate input
        if (username == null || username.isBlank()) {
            return new RegistrationResult(false, "Username is required.");
        }
        
        if (username.length() < 3) {
            return new RegistrationResult(false, "Username must be at least 3 characters long.");
        }
        
        if (username.length() > 50) {
            return new RegistrationResult(false, "Username must not exceed 50 characters.");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return new RegistrationResult(false, "Username can only contain letters, numbers, and underscores.");
        }
        
        if (email == null || email.isBlank()) {
            return new RegistrationResult(false, "Email is required.");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new RegistrationResult(false, "Please enter a valid email address.");
        }
        
        if (password == null || password.isBlank()) {
            return new RegistrationResult(false, "Password is required.");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new RegistrationResult(false, "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        
        if (!password.matches(".*[A-Za-z].*")) {
            return new RegistrationResult(false, "Password must contain at least one letter.");
        }
        
        if (!password.matches(".*\\d.*")) {
            return new RegistrationResult(false, "Password must contain at least one number.");
        }
        
        if (confirmPassword == null || confirmPassword.isBlank()) {
            return new RegistrationResult(false, "Please confirm your password.");
        }
        
        if (!password.equals(confirmPassword)) {
            return new RegistrationResult(false, "Passwords do not match.");
        }
        
        // Check if username already exists
        try {
            if (userDAO.usernameExists(username)) {
                return new RegistrationResult(false, "Username already exists. Please choose a different one.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return new RegistrationResult(false, "Database error occurred. Please try again.");
        }
        
        // Check if email already exists
        try {
            if (userDAO.emailExists(email)) {
                return new RegistrationResult(false, "Email already registered. Please use a different email or login.");
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return new RegistrationResult(false, "Database error occurred. Please try again.");
        }

        try {
            User newUser = new User(username, password, email, Role.CUSTOMER);
            int userId = userDAO.create(newUser);
            
            if (userId > 0) {
                User registeredUser = new User(userId, username, password, email, Role.CUSTOMER);
                return new RegistrationResult(true, "Registration successful! You can now log in.", registeredUser);
            } else {
                return new RegistrationResult(false, "Failed to create user. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
            return new RegistrationResult(false, "Database error occurred. Please try again.");
        }
    }
    
    public static class RegistrationResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public RegistrationResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public RegistrationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
}

