package view;

import controller.OrderController;
import dao.MenuItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.PaymentDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import service.AnalyticsService;
import util.StyleManager;

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
        // Bug fix: create a single AnalyticsService instance, reused throughout
        AnalyticsService analyticsService = new AnalyticsService(orderDAO, orderItemDAO);

        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #2563EB, #1E40AF); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        );
        Label title = new Label("Cashier Dashboard  \u2014  " + username);
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logout = new Button("Logout");
        StyleManager.styleDangerButton(logout);
        logout.setOnAction(e -> onLogout.run());
        topBar.getChildren().addAll(title, spacer, logout);
        root.setTop(topBar);

        // Three-column layout
        HBox centerBox = new HBox(12);
        centerBox.setPadding(new Insets(15));
        centerBox.setFillHeight(true);

        final Order[] currentOrderRef = {orderController.createNewOrder(userId, username)};

        // ── LEFT: Menu panel ──────────────────────────────────────────────────
        VBox menuPanel = new VBox(10);
        menuPanel.setPadding(new Insets(14));
        menuPanel.setMinWidth(220);
        menuPanel.setPrefWidth(260);
        menuPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        Label menuTitle = new Label("Menu");
        StyleManager.styleHeadingLabel(menuTitle);
        ListView<MenuItem> menuList = new ListView<>();
        menuList.setStyle("-fx-border-color: #E5E7EB; -fx-border-radius: 4px;");
        try { menuList.setItems(menuItemDAO.findAllObservable()); }
        catch (Exception e) { System.err.println("Error loading menu: " + e.getMessage()); }
        VBox.setVgrow(menuList, Priority.ALWAYS);
        Label qtyLabel = new Label("Quantity:");
        qtyLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 100, 1);
        qtySpinner.setEditable(true);
        qtySpinner.setMaxWidth(Double.MAX_VALUE);
        Button addBtn = new Button("Add to Order");
        StyleManager.stylePrimaryButton(addBtn);
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setPrefHeight(38);
        menuPanel.getChildren().addAll(menuTitle, menuList, qtyLabel, qtySpinner, addBtn);

        // ── CENTER: Order panel ───────────────────────────────────────────────
        VBox orderPanel = new VBox(10);
        orderPanel.setPadding(new Insets(14));
        orderPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        HBox.setHgrow(orderPanel, Priority.ALWAYS);
        Label orderTitle = new Label("Current Order");
        StyleManager.styleHeadingLabel(orderTitle);
        TableView<OrderItem> orderTable = new TableView<>();
        orderTable.setPlaceholder(new Label("No items yet. Select from the menu."));
        TableColumn<OrderItem, String> itemCol = new TableColumn<>("Item");
        itemCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMenuItem().getName()));
        TableColumn<OrderItem, String> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getQuantity())));
        qtyCol.setMaxWidth(55);
        TableColumn<OrderItem, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", c.getValue().getMenuItem().getPrice())));
        TableColumn<OrderItem, String> subCol = new TableColumn<>("Subtotal");
        subCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", c.getValue().getSubtotal())));
        orderTable.getColumns().addAll(itemCol, qtyCol, priceCol, subCol);
        orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(orderTable, Priority.ALWAYS);
        Label totalLabel = new Label("Running Total: $0.00");
        totalLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        Button removeBtn = new Button("Remove Selected");
        StyleManager.styleDangerButton(removeBtn);
        removeBtn.setMaxWidth(Double.MAX_VALUE);
        removeBtn.setPrefHeight(36);
        Button placeBtn = new Button("Place Order");
        StyleManager.styleSecondaryButton(placeBtn);
        placeBtn.setMaxWidth(Double.MAX_VALUE);
        placeBtn.setPrefHeight(36);
        Button cancelBtn = new Button("Cancel Order");
        cancelBtn.setStyle(
            "-fx-font-size: 13px; -fx-padding: 8px; " +
            "-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280; " +
            "-fx-border-radius: 6px; -fx-background-radius: 6px; -fx-cursor: hand;"
        );
        cancelBtn.setMaxWidth(Double.MAX_VALUE);
        cancelBtn.setPrefHeight(36);
        VBox orderButtonBox = new VBox(8, removeBtn, placeBtn, cancelBtn);
        orderPanel.getChildren().addAll(orderTitle, orderTable, totalLabel, orderButtonBox);

        // ── RIGHT: Sales Summary panel ────────────────────────────────────────
        VBox summaryPanel = new VBox(10);
        summaryPanel.setPadding(new Insets(14));
        summaryPanel.setMinWidth(200);
        summaryPanel.setPrefWidth(250);
        summaryPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 8px; -fx-background-radius: 8px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"
        );
        Label summaryTitle = new Label("Sales Summary");
        StyleManager.styleHeadingLabel(summaryTitle);
        Label salesLabel = new Label("Total Sales: $0.00");
        StyleManager.styleSubheadingLabel(salesLabel);
        Label popularLabel = new Label("Most Popular: N/A");
        StyleManager.styleSubheadingLabel(popularLabel);
        summaryPanel.getChildren().addAll(summaryTitle, new Separator(), salesLabel, popularLabel);

        // ── Event handlers ────────────────────────────────────────────────────
        addBtn.setOnAction(e -> {
            MenuItem selected = menuList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a menu item first.");
                return;
            }
            orderController.addItemToOrder(currentOrderRef[0], selected, qtySpinner.getValue());
            orderTable.getItems().setAll(currentOrderRef[0].getItems());
            totalLabel.setText("Running Total: $" + String.format("%.2f", currentOrderRef[0].getTotal()));
        });

        removeBtn.setOnAction(e -> {
            OrderItem selected = orderTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to remove.");
                return;
            }
            orderController.removeItemFromOrder(currentOrderRef[0], selected.getMenuItem().getId());
            orderTable.getItems().setAll(currentOrderRef[0].getItems());
            totalLabel.setText("Running Total: $" + String.format("%.2f", currentOrderRef[0].getTotal()));
        });

        placeBtn.setOnAction(e -> {
            if (currentOrderRef[0].getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Order is empty. Add items first.");
                return;
            }
            boolean success = orderController.placeOrder(currentOrderRef[0]);
            if (!success) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order.");
                return;
            }
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order placed successfully.");
            // Refresh analytics using the single shared instance
            try {
                salesLabel.setText("Total Sales: $" + String.format("%.2f", analyticsService.getTotalRevenue()));
                popularLabel.setText("Most Popular: " + analyticsService.getMostPopularItem());
            } catch (Exception ex) {
                System.err.println("Error updating analytics: " + ex.getMessage());
            }
            currentOrderRef[0] = orderController.createNewOrder(userId, username);
            orderTable.getItems().clear();
            totalLabel.setText("Running Total: $0.00");
        });

        cancelBtn.setOnAction(e -> {
            orderController.cancelOrder(currentOrderRef[0]);
            currentOrderRef[0] = orderController.createNewOrder(userId, username);
            orderTable.getItems().clear();
            totalLabel.setText("Running Total: $0.00");
        });

        // Initialize analytics
        try {
            salesLabel.setText("Total Sales: $" + String.format("%.2f", analyticsService.getTotalRevenue()));
            popularLabel.setText("Most Popular: " + analyticsService.getMostPopularItem());
        } catch (Exception ex) {
            System.err.println("Error loading analytics: " + ex.getMessage());
        }

        // Layout
        centerBox.getChildren().addAll(menuPanel, orderPanel, summaryPanel);
        HBox.setHgrow(menuPanel, Priority.NEVER);
        HBox.setHgrow(orderPanel, Priority.ALWAYS);
        HBox.setHgrow(summaryPanel, Priority.NEVER);

        VBox mainContent = new VBox(centerBox);
        mainContent.setFillWidth(true);
        VBox.setVgrow(centerBox, Priority.ALWAYS);
        root.setCenter(mainContent);
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
