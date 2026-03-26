package view;

import dao.MenuItemDAO;
import dao.OrderDAO;
import dao.OrderItemDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.MenuItem;
import model.Order;
import service.AnalyticsService;
import util.StyleManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminDashboardView {
    private final BorderPane root = new BorderPane();
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final AnalyticsService analyticsService;

    private final Label totalOrders = new Label();
    private final Label dailyRevenue = new Label();
    private final Label weeklyRevenue = new Label();
    private final Label monthlyRevenue = new Label();
    private final Label popular = new Label();
    private final Label profitable = new Label();

    public AdminDashboardView(MenuItemDAO menuItemDAO, OrderDAO orderDAO, OrderItemDAO orderItemDAO,
                              AnalyticsService analyticsService, Runnable onLogout) {
        this.menuItemDAO = menuItemDAO;
        this.orderDAO = orderDAO;
        this.analyticsService = analyticsService;

        // Top bar
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(15));
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, #2563EB, #1E40AF); " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        
        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Button refresh = new Button("Refresh Analytics");
        StyleManager.styleSecondaryButton(refresh);
        refresh.setOnAction(e -> refreshStats());
        
        Button logout = new Button("Logout");
        StyleManager.styleDangerButton(logout);
        logout.setOnAction(e -> onLogout.run());
        
        topBar.getChildren().addAll(title, new Pane(), refresh, logout);
        HBox.setHgrow(topBar.getChildren().get(1), Priority.ALWAYS);

        // Main content
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(15));
        mainContent.setStyle("-fx-background-color: #F9FAFB;");

        // Analytics section
        VBox analyticsBox = new VBox(8);
        analyticsBox.setPadding(new Insets(16));
        analyticsBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        analyticsBox.setMaxWidth(Double.MAX_VALUE); // Expand
        
        Label analyticsTitle = new Label("System Analytics Overview");
        StyleManager.styleHeadingLabel(analyticsTitle);
        
        StyleManager.styleSubheadingLabel(totalOrders);
        StyleManager.styleSubheadingLabel(dailyRevenue);
        StyleManager.styleSubheadingLabel(weeklyRevenue);
        StyleManager.styleSubheadingLabel(monthlyRevenue);
        StyleManager.styleSubheadingLabel(popular);
        StyleManager.styleSubheadingLabel(profitable);
        
        analyticsBox.getChildren().addAll(analyticsTitle, totalOrders, dailyRevenue, weeklyRevenue, monthlyRevenue, popular, profitable);

        // Menu management section
        VBox menuMgmt = buildMenuManagement();
        menuMgmt.setMaxWidth(Double.MAX_VALUE); // Expand

        // Left column
        VBox leftColumn = new VBox(12, analyticsBox, menuMgmt);
        leftColumn.setMaxWidth(Double.MAX_VALUE); // Expand

        // Orders section
        VBox ordersBox = new VBox(10);
        ordersBox.setPadding(new Insets(16));
        ordersBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        ordersBox.setMaxWidth(Double.MAX_VALUE); // Expand
        
        Label ordersTitle = new Label("Completed Orders");
        StyleManager.styleHeadingLabel(ordersTitle);
        
        TableView<Order> orderTable = buildOrderTable();
        
        ordersBox.getChildren().addAll(ordersTitle, orderTable);
        VBox.setVgrow(orderTable, Priority.ALWAYS);

        // Main layout
        HBox center = new HBox(12, leftColumn, ordersBox);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);
        HBox.setHgrow(ordersBox, Priority.ALWAYS);

        mainContent.getChildren().add(center);

        root.setTop(topBar);
        root.setCenter(mainContent);

        refreshStats();
    }

    private TableView<Order> buildOrderTable() {
        TableView<Order> table = new TableView<>();

        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getId())));

        TableColumn<Order, String> byCol = new TableColumn<>("User ID");
        byCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getUserId())));

        TableColumn<Order, String> atCol = new TableColumn<>("Created At");
        atCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        ));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                "$" + String.format("%.2f", c.getValue().getTotal())
        ));

        table.getColumns().addAll(idCol, byCol, atCol, statusCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Load completed orders
        try {
            table.setItems(javafx.collections.FXCollections.observableArrayList(orderDAO.findByStatus("COMPLETED")));
        } catch (Exception e) {
            System.err.println("Error loading completed orders: " + e.getMessage());
        }
        
        return table;
    }

    private VBox buildMenuManagement() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(16));
        box.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #E5E7EB; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 6px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 2);"
        );
        box.setMaxWidth(Double.MAX_VALUE); // Expand
        
        Label label = new Label("Manage Menu Items");
        StyleManager.styleHeadingLabel(label);

        TextField nameField = new TextField();
        nameField.setPromptText("Item name");
        nameField.setMaxWidth(Double.MAX_VALUE); // Expand
        StyleManager.styleTextField(nameField);

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        priceField.setMaxWidth(Double.MAX_VALUE); // Expand
        StyleManager.styleTextField(priceField);
        
        VBox fieldsBox = new VBox(8, nameField, priceField);
        fieldsBox.setMaxWidth(Double.MAX_VALUE);

        TableView<MenuItem> menuTable = new TableView<>();
        TableColumn<MenuItem, String> id = new TableColumn<>("ID");
        id.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getId())));
        TableColumn<MenuItem, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        TableColumn<MenuItem, String> price = new TableColumn<>("Price");
        price.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + String.format("%.2f", c.getValue().getPrice())));
        menuTable.getColumns().addAll(id, name, price);
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try {
            menuTable.setItems(menuItemDAO.findAllObservable());
        } catch (Exception e) {
            System.err.println("Error loading menu items: " + e.getMessage());
        }

        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getName());
                priceField.setText(String.valueOf(selected.getPrice()));
            }
        });

        Button addBtn = new Button("Add Item");
        StyleManager.stylePrimaryButton(addBtn);
        addBtn.setMaxWidth(Double.MAX_VALUE); // Expand
        
        Button updateBtn = new Button("Update Price");
        StyleManager.styleSecondaryButton(updateBtn);
        updateBtn.setMaxWidth(Double.MAX_VALUE); // Expand
        
        Button deleteBtn = new Button("Delete Item");
        StyleManager.styleDangerButton(deleteBtn);
        deleteBtn.setMaxWidth(Double.MAX_VALUE); // Expand
        
        HBox btnBox = new HBox(8, addBtn, updateBtn, deleteBtn);
        btnBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addBtn, Priority.ALWAYS);
        HBox.setHgrow(updateBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
        
        // Event handlers
        addBtn.setOnAction(e -> {
            try {
                String n = nameField.getText().trim();
                double p = parsePrice(priceField.getText());
                if (n.isEmpty()) throw new IllegalArgumentException();
                MenuItem newItem = new MenuItem(-1, n, p);
                menuItemDAO.create(newItem);
                menuTable.setItems(menuItemDAO.findAllObservable());
                nameField.clear();
                priceField.clear();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid name or price.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save menu item to MySQL.\n" + extractRootMessage(ex));
                System.err.println("Error saving menu item: " + ex.getMessage());
            }

        });

        updateBtn.setOnAction(e -> {
            MenuItem m = menuTable.getSelectionModel().getSelectedItem();
            if (m == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an item to edit.");
                return;
            }
            try {
                String n = nameField.getText().trim();
                double p = parsePrice(priceField.getText());
                if (n.isEmpty()) throw new IllegalArgumentException();
                m.setName(n);
                m.setPrice(p);
                menuItemDAO.update(m);
                menuTable.setItems(menuItemDAO.findAllObservable());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid name or price.");
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update menu item in MySQL.\n" + extractRootMessage(ex));
                System.err.println("Error updating menu item: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            MenuItem m = menuTable.getSelectionModel().getSelectedItem();
            if (m == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Select an item to delete.");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected menu item?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                try {
                    menuItemDAO.delete(m.getId());
                    menuTable.setItems(menuItemDAO.findAllObservable());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete item.");
                }
            }
        });

        box.getChildren().addAll(label, fieldsBox, btnBox, menuTable);
        VBox.setVgrow(menuTable, Priority.ALWAYS);
        return box;
    }

    private void refreshStats() {
        LocalDate now = LocalDate.now();
        totalOrders.setText("Total Completed Orders: " + analyticsService.getTotalCompletedOrders());
        dailyRevenue.setText("Daily Revenue: $" + String.format("%.2f", analyticsService.getDailyRevenue(now)));
        weeklyRevenue.setText("Weekly Revenue: $" + String.format("%.2f", analyticsService.getWeeklyRevenue(now)));
        monthlyRevenue.setText("Monthly Revenue: $" + String.format("%.2f", analyticsService.getMonthlyRevenue(now)));
        popular.setText("Most Popular Item: " + analyticsService.getMostPopularItem());
        profitable.setText("Most Profitable Item: " + analyticsService.getMostProfitableItem());
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public Parent getRoot() {
        return root;
    }

    private double parsePrice(String rawPrice) {
        double price = Double.parseDouble(rawPrice.trim());
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        return price;
    }

    private String extractRootMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage() == null ? "Unknown database error." : root.getMessage();
    }
}
