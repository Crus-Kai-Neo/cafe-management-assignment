package dao;

import model.MenuItem;
import model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO extends BaseDAO {

    public int create(int orderId, int itemId, int quantity, double subtotal) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, itemId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, subtotal);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create order item, no generated key returned.");
    }

    public OrderItem findById(int orderItemId, MenuItemDAO menuItemDAO) throws SQLException {
        String sql = "SELECT oi.item_id, oi.quantity, mi.name, mi.price " +
                "FROM order_items oi JOIN menu_items mi ON oi.item_id = mi.item_id " +
                "WHERE oi.order_item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    MenuItem menuItem = new MenuItem(rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price"));
                    return new OrderItem(menuItem, rs.getInt("quantity"));
                }
            }
        }
        return null;
    }

    public List<OrderItem> findByOrderId(int orderId, MenuItemDAO menuItemDAO) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.item_id, oi.quantity, mi.name, mi.price " +
                "FROM order_items oi JOIN menu_items mi ON oi.item_id = mi.item_id " +
                "WHERE oi.order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem menuItem = new MenuItem(rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price"));
                    items.add(new OrderItem(menuItem, rs.getInt("quantity")));
                }
            }
        }
        return items;
    }

    public boolean update(int orderItemId, int quantity) throws SQLException {
        String sql = "UPDATE order_items SET quantity = ? WHERE order_item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, orderItemId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteByOrderId(int orderId) throws SQLException {
        String sql = "DELETE FROM order_items WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }
}
