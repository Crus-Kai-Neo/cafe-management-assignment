package view;

import dao.UserDAO;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.UserRegistrationService;
import util.StyleManager;

import java.util.function.Consumer;

public class RegistrationView {
    private final VBox root = new VBox(16);
    
    public RegistrationView(UserDAO userDAO, Runnable onBackToLogin, Consumer<Boolean> onSuccess) {
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #F9FAFB;");
        
        // Header
        Label title = new Label("Create Account");
        StyleManager.styleTitleLabel(title);
        
        Label subtitle = new Label("Join us and start managing your café orders");
        StyleManager.styleNormalLabel(subtitle);
        
        // Form container
        VBox formBox = new VBox(12);
        formBox.setPadding(new Insets(24));
        formBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        formBox.setPrefWidth(400);
        
        // Username field
        Label usernameLabel = new Label("Username");
        StyleManager.styleSubheadingLabel(usernameLabel);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username");
        StyleManager.styleTextField(usernameField);
        usernameField.setPrefHeight(40);
        
        // Email field
        Label emailLabel = new Label("Email Address");
        StyleManager.styleSubheadingLabel(emailLabel);
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        StyleManager.styleTextField(emailField);
        emailField.setPrefHeight(40);
        
        // Password field
        Label passwordLabel = new Label("Password");
        StyleManager.styleSubheadingLabel(passwordLabel);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("At least 6 characters, with letters and numbers");
        StyleManager.styleTextField(passwordField);
        passwordField.setPrefHeight(40);
        
        // Confirm Password field
        Label confirmLabel = new Label("Confirm Password");
        StyleManager.styleSubheadingLabel(confirmLabel);
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm your password");
        StyleManager.styleTextField(confirmField);
        confirmField.setPrefHeight(40);
        
        // Error message label
        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        
        // Register button
        Button registerBtn = new Button("Create Account");
        StyleManager.stylePrimaryButton(registerBtn);
        registerBtn.setPrefWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(40);
        
        registerBtn.setOnAction(e -> {
            errorLabel.setText("");
            
            UserRegistrationService registrationService = new UserRegistrationService(userDAO);
            UserRegistrationService.RegistrationResult result = registrationService.register(
                usernameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                confirmField.getText()
            );
            
            if (result.isSuccess()) {
                StyleManager.styleSuccessLabel(errorLabel);
                errorLabel.setText(result.getMessage());
                
                // Show success and schedule return to login
                PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(2));
                pause.setOnFinished(event -> {
                    usernameField.clear();
                    emailField.clear();
                    passwordField.clear();
                    confirmField.clear();
                    onSuccess.accept(true);
                });
                pause.play();
            } else {
                StyleManager.styleErrorLabel(errorLabel);
                errorLabel.setText(result.getMessage());
            }
        });
        
        // Back button
        Button backBtn = new Button("Back to Login");
        StyleManager.styleOutlineButton(backBtn);
        backBtn.setPrefWidth(Double.MAX_VALUE);
        backBtn.setPrefHeight(40);
        backBtn.setOnAction(e -> onBackToLogin.run());
        
        formBox.getChildren().addAll(
            usernameLabel, usernameField,
            emailLabel, emailField,
            passwordLabel, passwordField,
            confirmLabel, confirmField,
            errorLabel,
            registerBtn,
            backBtn
        );
        
        // Password requirements info
        VBox infoBox = new VBox(6);
        infoBox.setPadding(new Insets(16));
        infoBox.setStyle(
            "-fx-background-color: #F0F9FF; " +
            "-fx-border-color: #BFDBFE; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px;"
        );
        
        Label infoTitle = new Label("Password Requirements:");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1E40AF;");
        
        Label req1 = new Label("• At least 6 characters long");
        Label req2 = new Label("• Must contain letters (a-z, A-Z)");
        Label req3 = new Label("• Must contain numbers (0-9)");
        
        req1.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E40AF;");
        req2.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E40AF;");
        req3.setStyle("-fx-font-size: 11px; -fx-text-fill: #1E40AF;");
        
        infoBox.getChildren().addAll(infoTitle, req1, req2, req3);
        
        root.getChildren().addAll(title, subtitle, formBox, infoBox);
    }
    
    public Parent getRoot() {
        return root;
    }
}

