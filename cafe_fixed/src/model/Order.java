package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private final int userId;
    private final String placedBy;
    private final LocalDateTime createdAt;
    private String status;
    private final List<OrderItem> items = new ArrayList<>();
    private double persistedTotal = 0.0;

    public Order(int id, int userId, String placedBy, LocalDateTime createdAt) {
        this.id        = id;
        this.userId    = userId;
        this.placedBy  = placedBy;
        this.createdAt = createdAt;
        this.status    = "PENDING";
    }

    public Order(int userId, String placedBy) {
        this(-1, userId, placedBy, LocalDateTime.now());
    }

    public int getId()                  { return id; }
    public int getUserId()              { return userId; }
    public String getPlacedBy()         { return placedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getStatus()           { return status; }
    public List<OrderItem> getItems()   { return items; }

    public void setId(int id)           { this.id = id; }
    public void setStatus(String s)     { this.status = s; }
    public void setPersistedTotal(double persistedTotal) { this.persistedTotal = persistedTotal; }

    public double getTotal() {
        if (!items.isEmpty()) {
            return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
        }
        return persistedTotal;
    }
}