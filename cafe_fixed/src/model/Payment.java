package model;

import java.time.LocalDateTime;

public class Payment {
    private final int paymentId;
    private final int orderId;
    private final double amount;
    private final String paymentMethod;
    private String paymentStatus;
    private final LocalDateTime paymentDate;

    public Payment(int paymentId, int orderId, double amount,
                   String paymentMethod, String paymentStatus, LocalDateTime paymentDate) {
        this.paymentId     = paymentId;
        this.orderId       = orderId;
        this.amount        = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate   = paymentDate;
    }

    /** Constructor for new payments (DB auto-generates the ID). */
    public Payment(int orderId, double amount, String paymentMethod) {
        this(-1, orderId, amount, paymentMethod, "PENDING", LocalDateTime.now());
    }

    public int getPaymentId()               { return paymentId; }
    public int getOrderId()                 { return orderId; }
    public double getAmount()               { return amount; }
    public String getPaymentMethod()        { return paymentMethod; }
    public String getPaymentStatus()        { return paymentStatus; }
    public LocalDateTime getPaymentDate()   { return paymentDate; }

    public void setPaymentStatus(String s)  { this.paymentStatus = s; }

    @Override
    public String toString() {
        return "Payment{id=" + paymentId + ", orderId=" + orderId +
                ", amount=" + amount + ", method='" + paymentMethod +
                "', status='" + paymentStatus + "'}";
    }
}