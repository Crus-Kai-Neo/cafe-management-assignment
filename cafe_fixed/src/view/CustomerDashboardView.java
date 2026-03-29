package view;

import controller.OrderController;
import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.MenuItem;
import model.Order;
import model.OrderItem;
import service.PaymentService;
import util.StyleManager;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        // Tab pane for Current Order and Order History
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // =================== TAB 1: CURRENT ORDER ===================
        Tab currentOrderTab = new Tab("Current Order", buildCurrentOrderTab(
            menuItemDAO, orderController, orderDAO, orderItemDAO, paymentDAO, userId, username
        ));
        
        // =================== TAB 2: ORDER HISTORY ===================
        Tab orderHistoryTab = new Tab("Order History", buildOrderHistoryTab(
            orderDAO, orderItemDAO, menuItemDAO, paymentDAO, userId
        ));
        
        tabPane.getTabs().addAll(currentOrderTab, orderHistoryTab);

        root.setTop(topBar);
        root.setCenter(tabPane);
    }

    private Parent buildCurrentOrderTab(MenuItemDAO menuItemDAO,
                                        OrderController orderController,
                                        OrderDAO orderDAO,
                                        OrderItemDAO orderItemDAO,
                                        PaymentDAO paymentDAO,
                                        int userId,
                                        String username) {
        HBox centerBox = new HBox(12);
        centerBox.setPadding(new Insets(15));
        centerBox.setFillHeight(true);

        Order openOrder = findLatestOpenOrder(orderDAO, orderItemDAO, menuItemDAO, userId);
        final Order[] currentOrderRef = {openOrder != null ? openOrder : orderController.createNewOrder(userId, username)};
        final boolean[] existingOpenOrder = {openOrder != null};

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

        if (existingOpenOrder[0]) {
            orderTable.getItems().setAll(currentOrderRef[0].getItems());
            totalLabel.setText("Order Total: $" + String.format("%.2f", currentOrderRef[0].getTotal()));
            addBtn.setDisable(true);
            removeBtn.setDisable(true);
            placeBtn.setDisable(true);
            cancelBtn.setDisable(true);
        }

        // ── RIGHT: Payment panel ──────────────────────────────────────────────
        PaymentPanel paymentPanel = new PaymentPanel(
            currentOrderRef[0], paymentDAO, orderDAO,
            success -> {
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Payment Submitted",
                        "The cashier will confirm your payment.");
                    existingOpenOrder[0] = false;
                    addBtn.setDisable(false);
                    removeBtn.setDisable(false);
                    placeBtn.setDisable(false);
                    cancelBtn.setDisable(false);
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
            if (existingOpenOrder[0]) {
                showAlert(Alert.AlertType.INFORMATION, "Open Order Exists", "Complete the existing order flow before creating a new one.");
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
            if (existingOpenOrder[0]) {
                showAlert(Alert.AlertType.INFORMATION, "Open Order Locked", "This existing order cannot be edited here.");
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
            if (existingOpenOrder[0]) {
                showAlert(Alert.AlertType.INFORMATION, "Open Order Exists", "Complete the existing order flow before placing a new order.");
                return;
            }
            boolean success = orderController.placeOrder(currentOrderRef[0]);
            if (!success) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to place order. Please try again.");
                return;
            }
            paymentPanel.refreshSummary(currentOrderRef[0]);
            paymentPanel.enablePayment();
            existingOpenOrder[0] = true;
            addBtn.setDisable(true);
            removeBtn.setDisable(true);
            placeBtn.setDisable(true);
            cancelBtn.setDisable(true);
            showAlert(Alert.AlertType.INFORMATION, "Order Placed",
                "Order #" + currentOrderRef[0].getId() + " placed!\nThe cashier will finalize your order before payment is accepted.");
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

        return centerBox;
    }

    private Order findLatestOpenOrder(OrderDAO orderDAO, OrderItemDAO orderItemDAO, MenuItemDAO menuItemDAO, int userId) {
        try {
            List<Order> orders = orderDAO.findByUserId(userId);
            Order latestOpen = null;
            for (Order order : orders) {
                String status = order.getStatus();
                if ("COMPLETED".equals(status) || "CANCELED".equals(status)) {
                    continue;
                }
                if (latestOpen == null || order.getCreatedAt().isAfter(latestOpen.getCreatedAt())) {
                    latestOpen = order;
                }
            }
            if (latestOpen != null) {
                latestOpen.getItems().clear();
                latestOpen.getItems().addAll(orderItemDAO.findByOrderId(latestOpen.getId(), menuItemDAO));
            }
            return latestOpen;
        } catch (SQLException e) {
            System.err.println("Error finding open order: " + e.getMessage());
            return null;
        }
    }

    private Parent buildOrderHistoryTab(OrderDAO orderDAO, OrderItemDAO orderItemDAO,
                                       MenuItemDAO menuItemDAO, PaymentDAO paymentDAO, int userId) {
        VBox historyPanel = new VBox(10);
        historyPanel.setPadding(new Insets(15));
        historyPanel.setStyle("-fx-background-color: #F9FAFB;");
        
        Label title = new Label("Order History");
        StyleManager.styleHeadingLabel(title);
        
        ObservableList<Order> customerOrders = FXCollections.observableArrayList();

        Runnable reloadHistory = () -> {
            customerOrders.clear();
            try {
                List<Order> orders = orderDAO.findByUserId(userId);
                for (Order o : orders) {
                    if ("CANCELED".equals(o.getStatus())) {
                        continue;
                    }
                    o.getItems().clear();
                    o.getItems().addAll(orderItemDAO.findByOrderId(o.getId(), menuItemDAO));
                    customerOrders.add(o);
                }
            } catch (SQLException e) {
                System.err.println("Error loading order history: " + e.getMessage());
            }
        };
        reloadHistory.run();
        
        // Table for order history
        TableView<Order> historyTable = new TableView<>(customerOrders);
        
        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getId())));
        
        TableColumn<Order, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ));
        
        TableColumn<Order, String> itemsCol = new TableColumn<>("Items");
        itemsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf(c.getValue().getItems().size())
        ));
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            "$" + String.format("%.2f", c.getValue().getTotal())
        ));
        
        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getStatus()
        ));
        
        historyTable.getColumns().addAll(idCol, dateCol, itemsCol, totalCol, statusCol);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setPrefHeight(300);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        Button refreshBtn = new Button("Refresh History");
        StyleManager.styleSecondaryButton(refreshBtn);
        refreshBtn.setOnAction(e -> reloadHistory.run());
        
        // Details panel
        VBox detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(14));
        detailsBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; -fx-border-width: 1; " +
            "-fx-border-radius: 6px;"
        );
        
        Label detailsTitle = new Label("Order Details");
        StyleManager.styleHeadingLabel(detailsTitle);
        
        TableView<OrderItem> detailsTable = new TableView<>();
        TableColumn<OrderItem, String> itemNameCol = new TableColumn<>("Item");
        itemNameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getMenuItem().getName()
        ));
        TableColumn<OrderItem, String> itemQtyCol = new TableColumn<>("Qty");
        itemQtyCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            String.valueOf(c.getValue().getQuantity())
        ));
        TableColumn<OrderItem, String> itemPriceCol = new TableColumn<>("Unit Price");
        itemPriceCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            "$" + String.format("%.2f", c.getValue().getMenuItem().getPrice())
        ));
        
        detailsTable.getColumns().addAll(itemNameCol, itemQtyCol, itemPriceCol);
        detailsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        detailsTable.setPrefHeight(150);
        VBox.setVgrow(detailsTable, Priority.ALWAYS);

        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("Credit Card", "Debit Card", "Cash", "Mobile Payment", "Check");
        methodCombo.setValue("Credit Card");
        methodCombo.setMaxWidth(Double.MAX_VALUE);

        Button paySelectedBtn = new Button("Pay Selected Order");
        StyleManager.stylePrimaryButton(paySelectedBtn);
        paySelectedBtn.setMaxWidth(Double.MAX_VALUE);

        Label actionHint = new Label("CONFIRMED orders can be paid from here.");
        StyleManager.styleNormalLabel(actionHint);
        
        detailsBox.getChildren().addAll(detailsTitle, detailsTable, actionHint, methodCombo, paySelectedBtn);
        
        // Selection listener
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected != null) {
                detailsTable.setItems(FXCollections.observableArrayList(selected.getItems()));
                actionHint.setText("Selected status: " + selected.getStatus());
            } else {
                detailsTable.getItems().clear();
                actionHint.setText("CONFIRMED orders can be paid from here.");
            }
        });

        paySelectedBtn.setOnAction(e -> {
            Order selected = historyTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an order first.");
                return;
            }

            if ("PAYMENT_PENDING".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Payment Pending", "Payment is already submitted. Cashier confirmation is pending.");
                return;
            }
            if (!"CONFIRMED".equals(selected.getStatus())) {
                showAlert(Alert.AlertType.INFORMATION, "Not Ready", "This order cannot be paid yet. Wait for cashier confirmation.");
                return;
            }

            PaymentService paymentService = new PaymentService(paymentDAO, orderDAO);
            PaymentService.PaymentProcessResult result = paymentService.requestPayment(
                    selected.getId(),
                    selected.getTotal(),
                    methodCombo.getValue()
            );
            if (result.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Payment Submitted", "The cashier will confirm your payment.");
                reloadHistory.run();
            } else {
                showAlert(Alert.AlertType.ERROR, "Payment Error", result.getMessage());
            }
        });
        
        historyPanel.getChildren().addAll(title, refreshBtn, historyTable, detailsBox);
        return historyPanel;
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
