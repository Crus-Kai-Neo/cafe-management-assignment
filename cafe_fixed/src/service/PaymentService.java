package service;

import dao.OrderDAO;
import dao.PaymentDAO;
import model.Order;
import model.Payment;

import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO;
    private final OrderDAO   orderDAO;

    public PaymentService(PaymentDAO paymentDAO, OrderDAO orderDAO) {
        this.paymentDAO = paymentDAO;
        this.orderDAO   = orderDAO;
    }

    public PaymentProcessResult requestPayment(int orderId, double amount, String paymentMethod) {
        try {
            Order order = orderDAO.findById(orderId);
            if (order == null)  return new PaymentProcessResult(false, "Order not found.");
            if (!"CONFIRMED".equals(order.getStatus())) {
                return new PaymentProcessResult(false, "Cashier confirmation is required before payment.");
            }
            if (amount <= 0)    return new PaymentProcessResult(false, "Invalid payment amount.");

            Payment existing = paymentDAO.findByOrderId(orderId);
            if (existing != null) {
                if ("COMPLETED".equals(existing.getPaymentStatus())) {
                    return new PaymentProcessResult(false, "Payment already completed.");
                }
                return new PaymentProcessResult(false, "Payment is already pending cashier confirmation.");
            }

            Payment payment = new Payment(orderId, amount, paymentMethod);
            int paymentId = paymentDAO.create(payment);
            if (paymentId > 0) {
                orderDAO.updateStatus(orderId, "PAYMENT_PENDING");
                Payment pending = new Payment(paymentId, orderId, amount, paymentMethod,
                        "PENDING", payment.getPaymentDate());
                return new PaymentProcessResult(true, "Payment submitted. Cashier will confirm it.", pending);
            }
            return new PaymentProcessResult(false, "Failed to submit payment.");
        } catch (SQLException e) {
            System.err.println("Error requesting payment: " + e.getMessage());
            return new PaymentProcessResult(false, "Database error: " + e.getMessage());
        }
    }

    public PaymentProcessResult confirmPaymentByCashier(int orderId) {
        try {
            Order order = orderDAO.findById(orderId);
            if (order == null) {
                return new PaymentProcessResult(false, "Order not found.");
            }
            Payment payment = paymentDAO.findByOrderId(orderId);
            if (payment == null) {
                return new PaymentProcessResult(false, "Customer has not submitted payment yet.");
            }
            if ("COMPLETED".equals(payment.getPaymentStatus())) {
                return new PaymentProcessResult(false, "Payment already confirmed.");
            }

            boolean paymentUpdated = paymentDAO.updateStatus(payment.getPaymentId(), "COMPLETED");
            boolean orderUpdated = orderDAO.updateStatus(orderId, "COMPLETED");

            if (paymentUpdated && orderUpdated) {
                payment.setPaymentStatus("COMPLETED");
                return new PaymentProcessResult(true, "Payment confirmed and order completed.", payment);
            }
            return new PaymentProcessResult(false, "Failed to finalize payment confirmation.");
        } catch (SQLException e) {
            System.err.println("Error confirming payment: " + e.getMessage());
            return new PaymentProcessResult(false, "Database error: " + e.getMessage());
        }
    }

    /**
     * Backward-compatible alias used by existing callers.
     */
    public PaymentProcessResult processPayment(int orderId, double amount, String paymentMethod) {
        return requestPayment(orderId, amount, paymentMethod);
    }

    public Payment getPaymentForOrder(int orderId) throws SQLException {
        return paymentDAO.findByOrderId(orderId);
    }

    public List<Payment> getAllPayments() throws SQLException        { return paymentDAO.findAll(); }
    public List<Payment> getPaymentsByStatus(String s) throws SQLException { return paymentDAO.findByStatus(s); }

    public double getTotalPaymentAmount() throws SQLException {
        return paymentDAO.findAll().stream()
                .filter(p -> "COMPLETED".equals(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount).sum();
    }

    public static class PaymentProcessResult {
        private final boolean success;
        private final String  message;
        private final Payment payment;

        public PaymentProcessResult(boolean success, String message) { this(success, message, null); }
        public PaymentProcessResult(boolean success, String message, Payment payment) {
            this.success = success; this.message = message; this.payment = payment;
        }

        public boolean isSuccess()  { return success; }
        public String getMessage()  { return message; }
        public Payment getPayment() { return payment; }
    }
}