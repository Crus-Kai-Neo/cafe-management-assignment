package controller;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

public class AuthController {
    private final UserDAO userDAO;

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResult login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new AuthResult(false, "Username and password are required.", null);
        }

        // Optional password rules check on input format
        if (password.length() < 6 || !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            return new AuthResult(false, "Password must be at least 6 chars and alphanumeric.", null);
        }

        try {
            User user = userDAO.findByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                return new AuthResult(true, "Login successful.", user);
            }
            return new AuthResult(false, "Invalid username or password.", null);
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            return new AuthResult(false, "Database error. Please try again.", null);
        }
    }

    public static class AuthResult {
        private final boolean success;
        private final String message;
        private final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }

    public static void main(String[] args) {
        launchMainApp(args);
    }

    private static void launchMainApp(String[] args) {
        try {
            Class<?> mainApp = Class.forName("MainApp");
            mainApp.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to launch MainApp from AuthController", e);
        }
    }   
}