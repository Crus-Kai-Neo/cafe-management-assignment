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

    private final UserDAO     userDAO;
    private final MenuItemDAO menuItemDAO;
    private final OrderDAO    orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final PaymentDAO  paymentDAO;

    private DatabaseInitializer() {
        this.userDAO      = new UserDAO();
        this.menuItemDAO  = new MenuItemDAO();
        this.orderDAO     = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.paymentDAO   = new PaymentDAO();
    }

    public static DatabaseInitializer getInstance() {
        if (instance == null) instance = new DatabaseInitializer();
        return instance;
    }

    public void seedDatabaseIfNeeded() {
        try {
            List<User> users = userDAO.findAll();
            if (!users.isEmpty()) {
                System.out.println("Database already seeded. Skipping.");
                return;
            }
            System.out.println("Seeding database with initial data...");

            userDAO.create(new User("admin",    "Admin123", "admin@cafe.local",    Role.ADMIN));
            userDAO.create(new User("cashier",  "Cash123",  "cashier@cafe.local",  Role.CASHIER));
            userDAO.create(new User("customer", "Cust123",  "customer@cafe.local", Role.CUSTOMER));

            menuItemDAO.create(new MenuItem(0, "Espresso",       3.00));
            menuItemDAO.create(new MenuItem(0, "Cappuccino",     4.50));
            menuItemDAO.create(new MenuItem(0, "Latte",          4.00));
            menuItemDAO.create(new MenuItem(0, "Mocha",          5.00));
            menuItemDAO.create(new MenuItem(0, "Croissant",      2.75));
            menuItemDAO.create(new MenuItem(0, "Blueberry Muffin", 3.25));

            System.out.println("Database seeded successfully!");
        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservableList<MenuItem> loadMenuItems() {
        ObservableList<MenuItem> items = FXCollections.observableArrayList();
        try { items.addAll(menuItemDAO.findAll()); }
        catch (SQLException e) { System.err.println("Error loading menu items: " + e.getMessage()); }
        return items;
    }

    public ObservableList<Order> loadCompletedOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        try {
            List<Order> completed = orderDAO.findByStatus("COMPLETED");
            for (Order o : completed) {
                List<model.OrderItem> items = orderItemDAO.findByOrderId(o.getId(), menuItemDAO);
                o.getItems().addAll(items);
            }
            orders.addAll(completed);
        } catch (SQLException e) {
            System.err.println("Error loading completed orders: " + e.getMessage());
        }
        return orders;
    }

    public UserDAO      getUserDAO()      { return userDAO; }
    public MenuItemDAO  getMenuItemDAO()  { return menuItemDAO; }
    public OrderDAO     getOrderDAO()     { return orderDAO; }
    public OrderItemDAO getOrderItemDAO() { return orderItemDAO; }
    public PaymentDAO   getPaymentDAO()   { return paymentDAO; }
}