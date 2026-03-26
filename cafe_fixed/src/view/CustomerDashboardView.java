package view;

import controller.OrderController;
import dao.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import util.StyleManager;

public class CustomerDashboardView {
    private final BorderPane root = new BorderPane();

    public CustomerDashboardView(MenuItemDAO menuItemDAO,
                                 OrderController orderController,
                                 OrderDAO orderDAO,
                                 OrderItemDAO orderItemDAO,
                                 PaymentDAO paymentDAO,
                                 String username,
                                 int userId,
                                 Runnable onLogout) {
        // Top bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #2563EB, #1E40AF); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
        );
        Label titleLabel = new Label("Customer Dashboard  \u2014  " + username);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logout = new Button("Logout");
        StyleManager.styleDangerButton(logout);
        logout.setOnAction(e -> onLogout.run());
        topBar.getChildren().addAll(titleLabel, spacer, logout);

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
        Button addBtn = new Button("Add to Cart");
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
        Label totalLabel = new Label("Order Total: $0.00");
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

        // ── RIGHT: Payment panel ──────────────────────────────────────────────
        PaymentPanel paymentPanel = new PaymentPanel(
            currentOrderRef[0], paymentDAO, orderDAO,
            success -> {
                if (success) {
                    currentOrderRef[0] = orderController.createNewOrder(userId, username);
                    orderTable.getItems().clear();
                    totalLabel.setText("Order Total: $0.00");
                }
            }
        );
        VBox paymentRoot = (VBox) paymentPanel.getRoot();
        paymentRoot.setMinWidth(230);
        paymentRoot.setPrefWidth(270);

        // ── Event handlers ────────────────────────────────────────────────────
        addBtn.setOnAction(e -> {
            MenuItem selected = menuList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a menu item first.");
                return;
            }
            orderController.addItemToOrder(currentOrderRef[0], selected, qtySpinner.getValue());
            orderTable.getItems().setAll(currentOrderRef[0].getItems());
            totalLabel.setText("Order Total: $" + String.format("%.2f", currentOrderRef[0].getTotal()));
            paymentPanel.refreshSummary(currentOrderRef[0]);
        });

        removeBtn.setOnAction(e -> {
            OrderItem selected = orderTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select an item to remove.");
                return;
            }
            orderController.removeItemFromOrder(currentOrderRef[0], selected.getMenuItem().getId());
            orderTable.getItems().setAll(currentOrderRef[0].getItems());
            totalLabel.setText("Order Total: $" + String.format("%.2f", currentOrderRef[0].getTotal()));
            paymentPanel.refreshSummary(currentOrderRef[0]);
        });

        placeBtn.setOnAction(e -> {
            if (currentOrderRef[0].getItems().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Your cart is empty. Add items first.");
                return;
            }
            boolean success = orderController.placeOrder(currentOrderRef[0]);
            if (!success) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order. Please try again.");
                return;
            }
            // Lock in the placed order's summary on the payment panel BEFORE creating a new order
            paymentPanel.refreshSummary(currentOrderRef[0]);
            paymentPanel.enablePayment();
            showAlert(Alert.AlertType.INFORMATION, "Order Placed",
                "Order #" + currentOrderRef[0].getId() + " placed!\nProceed to payment on the right.");
            // Start fresh for next order
            currentOrderRef[0] = orderController.createNewOrder(userId, username);
            orderTable.getItems().clear();
            totalLabel.setText("Order Total: $0.00");
        });

        cancelBtn.setOnAction(e -> {
            orderController.cancelOrder(currentOrderRef[0]);
            currentOrderRef[0] = orderController.createNewOrder(userId, username);
            orderTable.getItems().clear();
            totalLabel.setText("Order Total: $0.00");
            paymentPanel.refreshSummary(currentOrderRef[0]);
        });

        // ── Layout assembly ───────────────────────────────────────────────────
        centerBox.getChildren().addAll(menuPanel, orderPanel, paymentRoot);
        // menuPanel and paymentRoot use their preferred width; orderPanel expands
        HBox.setHgrow(menuPanel, Priority.NEVER);
        HBox.setHgrow(orderPanel, Priority.ALWAYS);
        HBox.setHgrow(paymentRoot, Priority.NEVER);

        root.setTop(topBar);
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
