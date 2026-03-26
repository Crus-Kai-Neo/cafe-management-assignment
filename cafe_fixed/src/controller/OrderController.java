package controller;

import dao.OrderDAO;
import dao.OrderItemDAO;
import dao.MenuItemDAO;
import model.MenuItem;
import model.Order;
import model.OrderItem;

import java.sql.SQLException;

public class OrderController {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;
    private final MenuItemDAO menuItemDAO;

    public OrderController(OrderDAO orderDAO, OrderItemDAO orderItemDAO, MenuItemDAO menuItemDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
        this.menuItemDAO = menuItemDAO;
    }

    public void addItemToOrder(Order order, MenuItem menuItem, int qty) {
        if (qty <= 0) return;
        for (OrderItem oi : order.getItems()) {
            if (oi.getMenuItem().getId() == menuItem.getId()) {
                oi.increaseQuantity(qty);
                return;
            }
        }
        order.getItems().add(new OrderItem(menuItem, qty));
    }

    public void removeItemFromOrder(Order order, int menuItemId) {
        order.getItems().removeIf(oi -> oi.getMenuItem().getId() == menuItemId);
    }

    public boolean placeOrder(Order order) {
        if (order.getItems().isEmpty()) return false;
        try {
            int orderId = orderDAO.create(order);
            order.setId(orderId);
            order.setStatus("COMPLETED");
            for (OrderItem item : order.getItems()) {
                orderItemDAO.create(orderId,
                    item.getMenuItem().getId(),
                    item.getQuantity(),
                    item.getSubtotal());
            }
            orderDAO.updateStatus(orderId, "COMPLETED");
            return true;
        } catch (SQLException e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }

    public void cancelOrder(Order order) {
        order.setStatus("CANCELED");
        order.getItems().clear();
    }

    public Order createNewOrder(int userId, String placedBy) {
        return new Order(userId, placedBy);
    }
}
