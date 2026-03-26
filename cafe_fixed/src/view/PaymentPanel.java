package view;

import dao.OrderDAO;
import dao.PaymentDAO;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Order;
import model.Payment;
import service.PaymentService;
import util.StyleManager;

import java.sql.SQLException;
import java.util.function.Consumer;

public class PaymentPanel {
    private final VBox root = new VBox(12);

    // Instance fields so they can be refreshed externally
    private final Label itemsLabel  = new Label();
    private final Label totalLabel  = new Label();
    private final ComboBox<String> methodCombo = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Label statusLabel = new Label();
    private final Button payBtn     = new Button("Process Payment");

    // Keep a mutable reference to the current order being paid
    private Order currentOrder;

    public PaymentPanel(Order initialOrder, PaymentDAO paymentDAO, OrderDAO orderDAO,
                        Consumer<Boolean> onPaymentComplete) {
        this.currentOrder = initialOrder;

        root.setPadding(new Insets(16));
        root.setMinWidth(230);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );

        // Title
        Label title = new Label("Payment");
        StyleManager.styleHeadingLabel(title);

        // Order summary box
        VBox summaryBox = new VBox(6);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle(
            "-fx-background-color: #F9FAFB; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 6px; -fx-background-radius: 6px;"
        );
        Label summaryTitle = new Label("Order Summary");
        StyleManager.styleSubheadingLabel(summaryTitle);
        StyleManager.styleNormalLabel(itemsLabel);
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        summaryBox.getChildren().addAll(summaryTitle, itemsLabel, totalLabel);

        // Refresh labels with initial order
        refreshSummaryLabels(initialOrder);

        // Payment method
        Label methodLabel = new Label("Payment Method");
        StyleManager.styleSubheadingLabel(methodLabel);
        methodCombo.getItems().addAll("Credit Card", "Debit Card", "Cash", "Mobile Payment", "Check");
        methodCombo.setValue("Credit Card");
        methodCombo.setMaxWidth(Double.MAX_VALUE);
        methodCombo.setStyle(
            "-fx-font-size: 13px; -fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 4px;"
        );

        // Amount
        Label amountLabel = new Label("Amount");
        StyleManager.styleSubheadingLabel(amountLabel);
        amountField.setEditable(false);
        amountField.setText(String.format("%.2f", initialOrder.getTotal()));
        StyleManager.styleTextField(amountField);

        // Status
        statusLabel.setWrapText(true);

        // Pay button
        StyleManager.stylePrimaryButton(payBtn);
        payBtn.setMaxWidth(Double.MAX_VALUE);
        payBtn.setPrefHeight(40);

        // Disable pay until order is placed (id > 0)
        payBtn.setDisable(initialOrder.getId() <= 0);

        payBtn.setOnAction(e -> {
            statusLabel.setText("");
            if (currentOrder.getId() <= 0) {
                StyleManager.styleErrorLabel(statusLabel);
                statusLabel.setText("Place your order first before paying.");
                return;
            }
            if (methodCombo.getValue() == null || methodCombo.getValue().isEmpty()) {
                StyleManager.styleErrorLabel(statusLabel);
                statusLabel.setText("Please select a payment method.");
                return;
            }
            try {
                PaymentService svc = new PaymentService(paymentDAO, orderDAO);
                PaymentService.PaymentProcessResult result = svc.processPayment(
                    currentOrder.getId(), currentOrder.getTotal(), methodCombo.getValue());
                if (result.isSuccess()) {
                    StyleManager.styleSuccessLabel(statusLabel);
                    statusLabel.setText("\u2713 Payment successful! Order completed.");
                    payBtn.setDisable(true);
                    methodCombo.setDisable(true);
                    amountField.setDisable(true);
                    onPaymentComplete.accept(true);
                } else {
                    StyleManager.styleErrorLabel(statusLabel);
                    statusLabel.setText("Error: " + result.getMessage());
                }
            } catch (Exception ex) {
                StyleManager.styleErrorLabel(statusLabel);
                statusLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Check if already paid
        try {
            Payment existing = paymentDAO.findByOrderId(initialOrder.getId());
            if (existing != null && "COMPLETED".equals(existing.getPaymentStatus())) {
                payBtn.setDisable(true);
                methodCombo.setDisable(true);
                amountField.setDisable(true);
                StyleManager.styleSuccessLabel(statusLabel);
                statusLabel.setText("\u2713 Paid via " + existing.getPaymentMethod());
            }
        } catch (SQLException ex) {
            System.err.println("Error checking payment: " + ex.getMessage());
        }

        root.getChildren().addAll(
            title, summaryBox, new Separator(),
            methodLabel, methodCombo,
            amountLabel, amountField,
            statusLabel, payBtn
        );
    }

    /**
     * Refresh the summary labels and amount field to reflect the given order.
     * Called whenever items are added/removed or after the order is placed.
     */
    public void refreshSummary(Order order) {
        this.currentOrder = order;
        refreshSummaryLabels(order);
        amountField.setText(String.format("%.2f", order.getTotal()));
        // Enable pay button only when order has been saved to DB
        payBtn.setDisable(order.getId() <= 0);
        // Reset status/controls in case user starts a new payment session
        if (order.getId() <= 0) {
            payBtn.setDisable(true);
            methodCombo.setDisable(false);
            amountField.setDisable(false);
            statusLabel.setText("");
        }
    }

    /**
     * Explicitly unlock the payment controls (called after order is successfully placed).
     */
    public void enablePayment() {
        payBtn.setDisable(false);
        methodCombo.setDisable(false);
        amountField.setDisable(false);
        statusLabel.setText("");
    }

    private void refreshSummaryLabels(Order order) {
        itemsLabel.setText("Items: " + order.getItems().size());
        totalLabel.setText("Total: $" + String.format("%.2f", order.getTotal()));
    }

    public Parent getRoot() { return root; }
}
