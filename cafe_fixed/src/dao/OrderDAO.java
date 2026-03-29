package dao;

import model.Order;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends BaseDAO {

    public int create(Order order) throws SQLException {
        String sql = "INSERT INTO orders (user_id, status) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setString(2, order.getStatus());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create order, no generated key returned.");
    }

    public Order findById(int orderId) throws SQLException {
        String sql = "SELECT o.order_id, o.user_id, o.status, o.order_date, u.username, " +
                "COALESCE(SUM(oi.subtotal), 0) AS order_total " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.order_id = ? " +
                "GROUP BY o.order_id, o.user_id, o.status, o.order_date, u.username";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Order> findByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.user_id, o.status, o.order_date, u.username, " +
                "COALESCE(SUM(oi.subtotal), 0) AS order_total " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.user_id = ? " +
                "GROUP BY o.order_id, o.user_id, o.status, o.order_date, u.username " +
                "ORDER BY o.order_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        }
        return orders;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.user_id, o.status, o.order_date, u.username, " +
                "COALESCE(SUM(oi.subtotal), 0) AS order_total " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                "GROUP BY o.order_id, o.user_id, o.status, o.order_date, u.username";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                orders.add(mapRow(rs));
            }
        }
        return orders;
    }

    public List<Order> findByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.user_id, o.status, o.order_date, u.username, " +
                "COALESCE(SUM(oi.subtotal), 0) AS order_total " +
                "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                "LEFT JOIN order_items oi ON o.order_id = oi.order_id " +
                "WHERE o.status = ? " +
                "GROUP BY o.order_id, o.user_id, o.status, o.order_date, u.username";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapRow(rs));
                }
            }
        }
        return orders;
    }

    public boolean updateStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("order_id");
        int userId = rs.getInt("user_id");
        String placedBy = rs.getString("username");
        Timestamp ts = rs.getTimestamp("order_date");
        LocalDateTime orderDate = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        String status = rs.getString("status");
        double total = rs.getDouble("order_total");
        Order order = new Order(id, userId, placedBy, orderDate);
        order.setStatus(status);
        order.setPersistedTotal(total);
        return order;
    }
}
