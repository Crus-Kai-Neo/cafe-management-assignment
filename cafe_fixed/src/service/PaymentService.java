package service;

import dao.OrderDAO;
import dao.PaymentDAO;
import model.Order;
import model.Payment;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final OrderDAO orderDAO;
    
    public PaymentService(PaymentDAO paymentDAO, OrderDAO orderDAO) {
        this.paymentDAO = paymentDAO;
        this.orderDAO = orderDAO;
    }
    
    public PaymentProcessResult processPayment(int orderId, double amount, String paymentMethod) {
        try {
            // this verfies the existence of the order and also retrieves the order total for validation
            Order order = orderDAO.findById(orderId);
            if (order == null) {
                return new PaymentProcessResult(false, "Order not found.");
            }
            
            // Validate amount
            if (amount <= 0) {
                return new PaymentProcessResult(false, "Invalid payment amount.");
            }
            
            // Check if order total matches payment amount
//            if (Math.abs(order.getTotal() - amount) > 0.01) { // Allow small floating point differences
//                return new PaymentProcessResult(false, "Payment amount does not match order total. Expected: $" +
//                    String.format("%.2f", order.getTotal()) + ", received: $" + String.format("%.2f", amount));
//            }
            
            // Create payment record
            Payment payment = new Payment(orderId, amount, paymentMethod);
            int paymentId = paymentDAO.create(payment);
            
            if (paymentId > 0) {
                paymentDAO.updateStatus(paymentId, "COMPLETED");
                orderDAO.updateStatus(orderId, "COMPLETED");
                
                Payment completedPayment = new Payment(paymentId, orderId, amount, paymentMethod, "COMPLETED", payment.getPaymentDate());
                return new PaymentProcessResult(true, "Payment processed successfully!", completedPayment);
            } else {
                return new PaymentProcessResult(false, "Failed to process payment. Please try again.");
            }
        } catch (SQLException e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return new PaymentProcessResult(false, "Database error occurred. Please try again.");
        }
    }
    
    public Payment getPaymentForOrder(int orderId) throws SQLException {
        return paymentDAO.findByOrderId(orderId);
    }
    
    public List<Payment> getAllPayments() throws SQLException {
        return paymentDAO.findAll();
    }
    
    public List<Payment> getPaymentsByStatus(String status) throws SQLException {
        return paymentDAO.findByStatus(status);
    }
    
    public double getTotalPaymentAmount() throws SQLException {
        return paymentDAO.findAll().stream()
            .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
            .mapToDouble(Payment::getAmount)
            .sum();
    }
    
    public static class PaymentProcessResult {
        private final boolean success;
        private final String message;
        private final Payment payment;
        
        public PaymentProcessResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public PaymentProcessResult(boolean success, String message, Payment payment) {
            this.success = success;
            this.message = message;
            this.payment = payment;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Payment getPayment() { return payment; }
    }
}

