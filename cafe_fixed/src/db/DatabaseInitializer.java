package db;

import dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.MenuItem;
import model.Order;
import model.Role;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class DatabaseInitializer {
    private static DatabaseInitializer instance;
    
    private final UserDAO userDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PaymentDAO paymentDAO;
    
    private DatabaseInitializer() {
        this.userDAO = new UserDAO();
        this.menuItemDAO = new MenuItemDAO();
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.paymentDAO = new PaymentDAO();
    }
    
    public static DatabaseInitializer getInstance() {
        if (instance == null) {
            instance = new DatabaseInitializer();
        }
        return instance;
    }
    
    public void seedDatabaseIfNeeded() {
        try {
            List<User> users = userDAO.findAll();
            if (!users.isEmpty()) {
                System.out.println("Database already seeded. Skipping seed data insertion.");
                return;
            }
            
            System.out.println("Seeding database with initial data...");

            User admin = new User("admin", "Admin123", "admin@cafe.local", Role.ADMIN);
            User cashier = new User("cashier", "Cash123", "cashier@cafe.local", Role.CASHIER);
            User customer = new User("customer", "Cust123", "customer@cafe.local", Role.CUSTOMER);
            
            userDAO.create(admin);
            userDAO.create(cashier);
            userDAO.create(customer);

            MenuItem espresso = new MenuItem(1, "Espresso", 3.00);
            MenuItem cappuccino = new MenuItem(2, "Cappuccino", 4.50);
            MenuItem latte = new MenuItem(3, "Latte", 4.00);
            MenuItem mocha = new MenuItem(4, "Mocha", 5.00);
            MenuItem croissant = new MenuItem(5, "Croissant", 2.75);
            MenuItem muffin = new MenuItem(6, "Blueberry Muffin", 3.25);
            
            menuItemDAO.create(espresso);
            menuItemDAO.create(cappuccino);
            menuItemDAO.create(latte);
            menuItemDAO.create(mocha);
            menuItemDAO.create(croissant);
            menuItemDAO.create(muffin);
            
            System.out.println("Database seeded successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public ObservableList<User> loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        try {
            users.addAll(userDAO.findAll());
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    public ObservableList<MenuItem> loadMenuItems() {
        ObservableList<MenuItem> items = FXCollections.observableArrayList();
        try {
            items.addAll(menuItemDAO.findAll());
        } catch (SQLException e) {
            System.err.println("Error loading menu items: " + e.getMessage());
        }
        return items;
    }
    
    public ObservableList<Order> loadCompletedOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");

            for (Order order : completedOrders) {
                List<model.OrderItem> items = orderItemDAO.findByOrderId(order.getId(), menuItemDAO);
                order.getItems().addAll(items);
            }
            
            orders.addAll(completedOrders);
        } catch (SQLException e) {
            System.err.println("Error loading completed orders: " + e.getMessage());
        }
        return orders;
    }
    
    public UserDAO getUserDAO() { return userDAO; }
    public MenuItemDAO getMenuItemDAO() { return menuItemDAO; }
    public OrderDAO getOrderDAO() { return orderDAO; }
    public OrderItemDAO getOrderItemDAO() { return orderItemDAO; }
    public PaymentDAO getPaymentDAO() { return paymentDAO; }
}

