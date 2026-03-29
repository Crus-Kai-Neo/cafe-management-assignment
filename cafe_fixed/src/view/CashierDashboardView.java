package view;

import controller.OrderController;
import dao.MenuItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.PaymentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Order;
import model.OrderItem;
import service.AnalyticsService;
import service.PaymentService;
import util.StyleManager;

import java.util.List;

public class CashierDashboardView {
    private final BorderPane root = new BorderPane();

    public CashierDashboardView(MenuItemDAO menuItemDAO,
                                OrderController orderController,
                                OrderDAO orderDAO,
                                OrderItemDAO orderItemDAO,
                                PaymentDAO paymentDAO,
                                String username,
                                int userId,
                                Runnable onLogout) {
        AnalyticsService analyticsService = new AnalyticsService(orderDAO, orderItemDAO);
        PaymentService paymentService = new PaymentService(paymentDAO, orderDAO);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #2563EB, #1E40AF); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        );
        Label title = new Label("Cashier Dashboard  -  " + username);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logout = new Button("Logout");
        StyleManager.styleDangerButton(logout);
        logout.setOnAction(e -> onLogout.run());
        topBar.getChildren().addAll(title, spacer, logout);
        root.setTop(topBar);

        HBox centerBox = new HBox(12);
        centerBox.setPadding(new Insets(15));
        centerBox.setFillHeight(true);

        ObservableList<Order> activeOrders = FXCollections.observableArrayList();

        Runnable reloadOrders = () -> {
            activeOrders.clear();
            try {
                List<Order> pending = orderDAO.findByStatus("PENDING");
                List<Order> confirmed = orderDAO.findByStatus("CONFIRMED");
                List<Order> paymentPending = orderDAO.findByStatus("PAYMENT_PENDING");

                for (Order o : pending) {
                    o.getItems().clear();
                    o.getItems().addAll(orderItemDAO.findByOrderId(o.getId(), menuItemDAO));
                    activeOrders.add(o);
                }
                for (Order o : confirmed) {
                    o.getItems().clear();
                    o.getItems().addAll(orderItemDAO.findByOrderId(o.getId(), menuItemDAO));
                    activeOrders.add(o);
                }
                for (Order o : paymentPending) {
                    o.getItems().clear();
                    o.getItems().addAll(orderItemDAO.findByOrderId(o.getId(), menuItemDAO));
                    activeOrders.add(o);
                }
            } catch (Exception ex) {
                System.err.println("Error loading orders for cashier: " + ex.getMessage());
            }
        };
        reloadOrders.run();

        VBox ordersListPanel = new VBox(10);
        ordersListPanel.setPadding(new Insets(14));
        ordersListPanel.setMinWidth(260);
        ordersListPanel.setPrefWidth(300);
        ordersListPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );

        Label ordersListTitle = new Label("Action Required Orders");
        StyleManager.styleHeadingLabel(ordersListTitle);

        ListView<Order> ordersList = new ListView<>(activeOrders);
        ordersList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Order item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("#" + item.getId() + " | " + item.getStatus() + " | $" + String.format("%.2f", item.getTotal()));
                }
            }
        });
        VBox.setVgrow(ordersList, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh Orders");
        StyleManager.styleSecondaryButton(refreshBtn);
        refreshBtn.setMaxWidth(Double.MAX_VALUE);
        refreshBtn.setOnAction(e -> reloadOrders.run());

        ordersListPanel.getChildren().addAll(ordersListTitle, ordersList, refreshBtn);

        VBox detailsPanel = new VBox(10);
        detailsPanel.setPadding(new Insets(14));
        detailsPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );
        HBox.setHgrow(detailsPanel, Priority.ALWAYS);

        Label detailsTitle = new Label("Order Workflow");
        StyleManager.styleHeadingLabel(detailsTitle);

        TableView<OrderItem> detailsTable = new TableView<>();
        detailsTable.setPlaceholder(new Label("Select an order to view details."));
        TableColumn<OrderItem, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMenuItem().getName()));
        TableColumn<OrderItem, String> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        TableColumn<OrderItem, String> subCol = new TableColumn<>("Subtotal");
        subCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", c.getValue().getSubtotal())));
        detailsTable.getColumns().addAll(itemCol, qtyCol, subCol);
        detailsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(detailsTable, Priority.ALWAYS);

        Label statusLabel = new Label("Select an order.");
        StyleManager.styleNormalLabel(statusLabel);
        Label totalLabel = new Label("Order Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #10B981;");

        Button confirmOrderBtn = new Button("Confirm Order");
        StyleManager.styleSecondaryButton(confirmOrderBtn);
        confirmOrderBtn.setMaxWidth(Double.MAX_VALUE);

        Button confirmPaymentBtn = new Button("Confirm Payment");
        StyleManager.stylePrimaryButton(confirmPaymentBtn);
        confirmPaymentBtn.setMaxWidth(Double.MAX_VALUE);

        Button generateBillBtn = new Button("Generate Bill");
        StyleManager.styleSecondaryButton(generateBillBtn);
        generateBillBtn.setMaxWidth(Double.MAX_VALUE);

        HBox buttons = new HBox(8, confirmOrderBtn, confirmPaymentBtn, generateBillBtn);
        HBox.setHgrow(confirmOrderBtn, Priority.ALWAYS);
        HBox.setHgrow(confirmPaymentBtn, Priority.ALWAYS);
        HBox.setHgrow(generateBillBtn, Priority.ALWAYS);

        detailsPanel.getChildren().addAll(detailsTitle, detailsTable, totalLabel, statusLabel, buttons);

        VBox summaryPanel = new VBox(10);
        summaryPanel.setPadding(new Insets(14));
        summaryPanel.setMinWidth(220);
        summaryPanel.setPrefWidth(260);
        summaryPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px;"
        );

        Label summaryTitle = new Label("Sales Summary");
        StyleManager.styleHeadingLabel(summaryTitle);
        Label salesLabel = new Label();
        Label ordersCountLabel = new Label();
        Label popularLabel = new Label();
        StyleManager.styleSubheadingLabel(salesLabel);
        StyleManager.styleSubheadingLabel(ordersCountLabel);
        StyleManager.styleSubheadingLabel(popularLabel);

        Runnable refreshAnalytics = () -> {
            salesLabel.setText("Total Sales: $" + String.format("%.2f", analyticsService.getTotalRevenue()));
            ordersCountLabel.setText("Completed Orders: " + analyticsService.getTotalCompletedOrders());
            popularLabel.setText("Most Popular: " + analyticsService.getMostPopularItem());
        };
        refreshAnalytics.run();

        summaryPanel.getChildren().addAll(summaryTitle, new Separator(), salesLabel, ordersCountLabel, popularLabel);

        ordersList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            detailsTable.getItems().clear();
            if (selected == null) {
                totalLabel.setText("Order Total: $0.00");
                statusLabel.setText("Select an order.");
                return;
            }
            detailsTable.getItems().addAll(selected.getItems());
            totalLabel.setText("Order Total: $" + String.format("%.2f", selected.getTotal()));
            statusLabel.setText("Current Status: " + selected.getStatus());
        });

        confirmOrderBtn.setOnAction(e -> {
            Order selected = ordersList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an order first.");
                return;
            }
            if (!"PENDING".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Only PENDING orders can be confirmed.");
                return;
            }
            try {
                orderDAO.updateStatus(selected.getId(), "CONFIRMED");
                showAlert(Alert.AlertType.INFORMATION, "Order Confirmed", "Order confirmed. Customer can now submit payment.");
                reloadOrders.run();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to confirm order: " + ex.getMessage());
            }
        });

        confirmPaymentBtn.setOnAction(e -> {
            Order selected = ordersList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an order first.");
                return;
            }
            if (!"PAYMENT_PENDING".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Customer must submit payment first.");
                return;
            }

            PaymentService.PaymentProcessResult result = paymentService.confirmPaymentByCashier(selected.getId());
            if (result.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "The order has been completed.");
                reloadOrders.run();
                refreshAnalytics.run();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
            }
        });

        generateBillBtn.setOnAction(e -> {
            Order selected = ordersList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an order first.");
                return;
            }
            StringBuilder bill = new StringBuilder();
            bill.append("Order #").append(selected.getId()).append("\n");
            bill.append("Status: ").append(selected.getStatus()).append("\n");
            bill.append("Day: ").append(selected.getCreatedAt().toLocalDate()).append("\n");
            bill.append("------------------------------\n");
            for (OrderItem item : selected.getItems()) {
                bill.append(item.getMenuItem().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" = $")
                    .append(String.format("%.2f", item.getSubtotal()))
                    .append("\n");
            }
            bill.append("------------------------------\n");
            bill.append("Total: $").append(String.format("%.2f", selected.getTotal()));
            showAlert(Alert.AlertType.INFORMATION, "Generated Bill", bill.toString());
        });

        centerBox.getChildren().addAll(ordersListPanel, detailsPanel, summaryPanel);
        HBox.setHgrow(detailsPanel, Priority.ALWAYS);

        root.setCenter(centerBox);
    }

    public Parent getRoot() { return root; }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
