package util;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class StyleManager {
    // Modern color palette
    public static final Color PRIMARY_COLOR = Color.web("#2563EB");      // Vibrant Blue
    public static final Color PRIMARY_DARK = Color.web("#1E40AF");       // Darker Blue
    public static final Color SECONDARY_COLOR = Color.web("#10B981");    // Emerald Green
    public static final Color ACCENT_COLOR = Color.web("#F59E0B");       // Amber
    public static final Color SUCCESS_COLOR = Color.web("#10B981");      // Green
    public static final Color ERROR_COLOR = Color.web("#EF4444");        // Red
    public static final Color WARNING_COLOR = Color.web("#F59E0B");      // Amber
    public static final Color BG_LIGHT = Color.web("#F9FAFB");           // Light Gray
    public static final Color BG_DARK = Color.web("#111827");            // Very Dark Gray
    public static final Color TEXT_PRIMARY = Color.web("#111827");       // Dark Text
    public static final Color TEXT_SECONDARY = Color.web("#6B7280");     // Gray Text
    public static final Color BORDER_COLOR = Color.web("#E5E7EB");       // Light Border
    
    // Button styling
    public static void stylePrimaryButton(Button button) {
        button.setStyle(
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
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #1E40AF; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #2563EB; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        ));
    }
    
    public static void styleSecondaryButton(Button button) {
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #10B981; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #059669; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #10B981; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        ));
    }
    
    public static void styleDangerButton(Button button) {
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #EF4444; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #DC2626; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #EF4444; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        ));
    }
    
    public static void styleOutlineButton(Button button) {
        button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #2563EB; " +
            "-fx-border-color: #2563EB; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: #F0F9FF; " +
            "-fx-text-fill: #1E40AF; " +
            "-fx-border-color: #1E40AF; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10px 24px; " +
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #2563EB; " +
            "-fx-border-color: #2563EB; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-cursor: hand;"
        ));
    }
    
    // Input field styling
    public static void styleTextField(TextField textField) {
        textField.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-padding: 10px; " +
            "-fx-background-color: white; " +
            "-fx-text-fill: #111827; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-focus-color: #2563EB; " +
            "-fx-faint-focus-color: #DBEAFE;"
        );
    }
    
    // Label styling
    public static void styleTitleLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #111827;"
        );
    }
    
    public static void styleHeadingLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #111827;"
        );
    }
    
    public static void styleSubheadingLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #374151;"
        );
    }
    
    public static void styleNormalLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #6B7280;"
        );
    }
    
    public static void styleSuccessLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #10B981; " +
            "-fx-font-weight: bold;"
        );
    }
    
    public static void styleErrorLabel(Label label) {
        label.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #EF4444; " +
            "-fx-font-weight: bold;"
        );
    }
}

