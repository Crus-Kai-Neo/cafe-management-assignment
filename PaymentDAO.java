package dao;

import model.Payment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentDAO extends BaseDAO {
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);
    private static final Map<Integer, Payment> PAYMENTS = new ConcurrentHashMap<>();
    private static final Map<Integer, Integer> ORDER_TO_PAYMENT = new ConcurrentHashMap<>();
    
    public int create(Payment payment) throws SQLException {
        if (ORDER_TO_PAYMENT.containsKey(payment.getOrderId())) {
            throw new SQLException("Payment already exists for this order.");
        }
        int id = ID_SEQ.getAndIncrement();
        Payment stored = new Payment(id, payment.getOrderId(), payment.getAmount(), payment.getPaymentMethod(), payment.getPaymentStatus(), payment.getPaymentDate());
        PAYMENTS.put(id, stored);
        ORDER_TO_PAYMENT.put(payment.getOrderId(), id);
        return id;
    }
    
    public Payment findById(int paymentId) throws SQLException {
        return PAYMENTS.get(paymentId);
    }
    
    public Payment findByOrderId(int orderId) throws SQLException {
        Integer paymentId = ORDER_TO_PAYMENT.get(orderId);
        return paymentId == null ? null : PAYMENTS.get(paymentId);
    }
    
    public List<Payment> findAll() throws SQLException {
        return new ArrayList<>(PAYMENTS.values());
    }
    
    public List<Payment> findByStatus(String paymentStatus) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        for (Payment payment : PAYMENTS.values()) {
            if (paymentStatus.equals(payment.getPaymentStatus())) {
                payments.add(payment);
            }
        }
        return payments;
    }
    
    public boolean updateStatus(int paymentId, String paymentStatus) throws SQLException {
        Payment existing = PAYMENTS.get(paymentId);
        if (existing == null) {
            return false;
        }
        Payment updated = new Payment(existing.getPaymentId(), existing.getOrderId(), existing.getAmount(), existing.getPaymentMethod(), paymentStatus, existing.getPaymentDate());
        PAYMENTS.put(paymentId, updated);
        return true;
    }
    
    public boolean delete(int paymentId) throws SQLException {
        Payment removed = PAYMENTS.remove(paymentId);
        if (removed != null) {
            ORDER_TO_PAYMENT.remove(removed.getOrderId());
            return true;
        }
        return false;
    }
}

