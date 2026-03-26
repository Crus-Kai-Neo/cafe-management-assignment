package dao;

import model.Payment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO extends BaseDAO {

    public int create(Payment payment) throws SQLException {
        if (findByOrderId(payment.getOrderId()) != null) {
            throw new SQLException("Payment already exists for this order.");
        }
        String sql = "INSERT INTO payments (order_id, amount, payment_method, payment_status, payment_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getPaymentStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create payment, no generated key returned.");
    }

    public Payment findById(int paymentId) throws SQLException {
        String sql = "SELECT payment_id, order_id, amount, payment_method, payment_status, payment_date " +
                "FROM payments WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Payment findByOrderId(int orderId) throws SQLException {
        String sql = "SELECT payment_id, order_id, amount, payment_method, payment_status, payment_date " +
                "FROM payments WHERE order_id = ?";
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

    public List<Payment> findAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT payment_id, order_id, amount, payment_method, payment_status, payment_date " +
                "FROM payments";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                payments.add(mapRow(rs));
            }
        }
        return payments;
    }

    public List<Payment> findByStatus(String paymentStatus) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT payment_id, order_id, amount, payment_method, payment_status, payment_date " +
                "FROM payments WHERE payment_status = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapRow(rs));
                }
            }
        }
        return payments;
    }

    public boolean updateStatus(int paymentId, String paymentStatus) throws SQLException {
        String sql = "UPDATE payments SET payment_status = ? WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int paymentId) throws SQLException {
        String sql = "DELETE FROM payments WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("payment_date");
        LocalDateTime paymentDate = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("order_id"),
                rs.getDouble("amount"),
                rs.getString("payment_method"),
                rs.getString("payment_status"),
                paymentDate
        );
    }
}
