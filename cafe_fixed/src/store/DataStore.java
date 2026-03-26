package store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.MenuItem;
import model.Order;
import model.Role;
import model.User;

public class DataStore {
    public final ObservableList<User> users = FXCollections.observableArrayList();
    public final ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    public final ObservableList<Order> completedOrders = FXCollections.observableArrayList();

    public int nextMenuItemId = 1;
    public int nextOrderId = 1;

    public void seedInitialData() {
        users.add(new User("admin", "Admin123", "admin@cafe.local", Role.ADMIN));
        users.add(new User("cashier", "Cash123", "cashier@cafe.local", Role.CASHIER));
        users.add(new User("customer", "Cust123", "customer@cafe.local", Role.CUSTOMER));

        menuItems.add(new MenuItem(nextMenuItemId++, "Espresso", 3.00));
        menuItems.add(new MenuItem(nextMenuItemId++, "Cappuccino", 4.50));
        menuItems.add(new MenuItem(nextMenuItemId++, "Latte", 4.00));
        menuItems.add(new MenuItem(nextMenuItemId++, "Mocha", 5.00));
        menuItems.add(new MenuItem(nextMenuItemId++, "Croissant", 2.75));
        menuItems.add(new MenuItem(nextMenuItemId++, "Blueberry Muffin", 3.25));
    }
}