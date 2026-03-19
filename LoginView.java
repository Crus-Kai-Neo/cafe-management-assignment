package view;

import controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.User;
import util.StyleManager;

import java.util.function.Consumer;

public class LoginView {
    private final VBox root = new VBox(12);

    public LoginView(AuthController authController, Consumer<User> onLoginSuccess, Runnable onRegister) {
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F9FAFB;");
        
        // Header Section
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        
        Label title = new Label("Café Order Management");
        StyleManager.styleTitleLabel(title);
        
        Label subtitle = new Label("Sign in to manage orders and analytics");
        StyleManager.styleNormalLabel(subtitle);
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7280;");
        
        headerBox.getChildren().addAll(title, subtitle);
        
        // Form Container
        VBox formBox = new VBox(14);
        formBox.setPadding(new Insets(28));
        formBox.setPrefWidth(380);
        formBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        
        // Username field
        Label usernameLabel = new Label("Username");
        StyleManager.styleSubheadingLabel(usernameLabel);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        StyleManager.styleTextField(usernameField);
        usernameField.setPrefHeight(40);
        
        // Password field
        Label passwordLabel = new Label("Password");
        StyleManager.styleSubheadingLabel(passwordLabel);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        StyleManager.styleTextField(passwordField);
        passwordField.setPrefHeight(40);
        
        // Error/Info message label
        Label infoLabel = new Label();
        infoLabel.setWrapText(true);
        infoLabel.setPrefHeight(40);
        StyleManager.styleNormalLabel(infoLabel);
        infoLabel.setText("Demo Accounts:Admin: admin/Admin123  |  Cashier: cashier/Cash123  |  Customer: customer/Cust123");
        infoLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6B7280; -fx-padding: 10;");
        
        // Error message label
        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        
        // Login button
        Button loginBtn = new Button("Sign In");
        StyleManager.stylePrimaryButton(loginBtn);
        loginBtn.setPrefWidth(180);
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #2563EB; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        
        loginBtn.setOnAction(e -> {
            errorLabel.setText("");
            AuthController.AuthResult result = authController.login(usernameField.getText(), passwordField.getText());
            if (!result.isSuccess()) {
                StyleManager.styleErrorLabel(errorLabel);
                errorLabel.setText("Error: " + result.getMessage());
                return;
            }
            onLoginSuccess.accept(result.getUser());
        });
        
        // Register button
        Button registerBtn = new Button("Create New Account");
        StyleManager.styleOutlineButton(registerBtn);
        registerBtn.setPrefWidth(Double.MAX_VALUE);
        registerBtn.setPrefHeight(40);
        registerBtn.setOnAction(e -> onRegister.run());
        
        // Clear button
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-color: #F3F4F6; " +
            "-fx-text-fill: #6B7280; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        clearBtn.setOnAction(e -> {
            usernameField.clear();
            passwordField.clear();
            errorLabel.setText("");
        });
        clearBtn.setOnMouseEntered(e -> clearBtn.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-color: #E5E7EB; " +
            "-fx-text-fill: #374151; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        clearBtn.setOnMouseExited(e -> clearBtn.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-padding: 8px 16px; " +
            "-fx-background-color: #F3F4F6; " +
            "-fx-text-fill: #6B7280; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        
        // Button box for login and clear buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setSpacing(10);
        buttonBox.setPrefHeight(40);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(loginBtn, clearBtn);
        
        formBox.getChildren().addAll(
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            infoLabel,
            errorLabel,
            buttonBox,
            new javafx.scene.control.Separator(),
            registerBtn
        );
        
        root.getChildren().addAll(headerBox, formBox);
    }

    public Parent getRoot() {
        return root;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}