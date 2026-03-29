package util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class StyleManager {

    // ── Button styles ─────────────────────────────────────────────────────────
    public static void stylePrimaryButton(Button b) {
        String base   = btnBase("#2563EB", "white");
        String hover  = btnBase("#1E40AF", "white");
        applyHover(b, base, hover);
    }

    public static void styleSecondaryButton(Button b) {
        String base  = btnBase("#10B981", "white");
        String hover = btnBase("#059669", "white");
        applyHover(b, base, hover);
    }

    public static void styleDangerButton(Button b) {
        String base  = btnBase("#EF4444", "white");
        String hover = btnBase("#DC2626", "white");
        applyHover(b, base, hover);
    }

    public static void styleOutlineButton(Button b) {
        String base = "-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:10px 24px;" +
                "-fx-background-color:transparent;-fx-text-fill:#2563EB;" +
                "-fx-border-color:#2563EB;-fx-border-width:2;" +
                "-fx-border-radius:6px;-fx-background-radius:6px;-fx-cursor:hand;";
        String hover = "-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:10px 24px;" +
                "-fx-background-color:#F0F9FF;-fx-text-fill:#1E40AF;" +
                "-fx-border-color:#1E40AF;-fx-border-width:2;" +
                "-fx-border-radius:6px;-fx-background-radius:6px;-fx-cursor:hand;";
        applyHover(b, base, hover);
    }

    // ── Input / Label styles ──────────────────────────────────────────────────
    public static void styleTextField(TextField tf) {
        tf.setStyle("-fx-font-size:13px;-fx-padding:10px;-fx-background-color:white;" +
                "-fx-text-fill:#111827;-fx-border-color:#E5E7EB;-fx-border-width:1;" +
                "-fx-border-radius:6px;-fx-background-radius:6px;" +
                "-fx-focus-color:#2563EB;-fx-faint-focus-color:#DBEAFE;");
    }

    public static void styleTitleLabel(Label l) {
        l.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:#111827;");
    }
    public static void styleHeadingLabel(Label l) {
        l.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#111827;");
    }
    public static void styleSubheadingLabel(Label l) {
        l.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#374151;");
    }
    public static void styleNormalLabel(Label l) {
        l.setStyle("-fx-font-size:13px;-fx-text-fill:#6B7280;");
    }
    public static void styleSuccessLabel(Label l) {
        l.setStyle("-fx-font-size:13px;-fx-text-fill:#10B981;-fx-font-weight:bold;");
    }
    public static void styleErrorLabel(Label l) {
        l.setStyle("-fx-font-size:13px;-fx-text-fill:#EF4444;-fx-font-weight:bold;");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private static String btnBase(String bg, String fg) {
        return "-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:10px 24px;" +
                "-fx-background-color:" + bg + ";-fx-text-fill:" + fg + ";" +
                "-fx-border-radius:6px;-fx-background-radius:6px;-fx-cursor:hand;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),4,0,0,2);";
    }

    private static void applyHover(Button b, String normal, String hover) {
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(normal));
    }
}