package dao;

import model.Order;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderDAO extends BaseDAO {
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);
    private static final Map<Integer, Order> ORDERS = new ConcurrentHashMap<>();
    
    public int create(Order order) throws SQLException {
        int id = ID_SEQ.getAndIncrement();
        Order stored = new Order(id, order.getUserId(), order.getPlacedBy(), LocalDateTime.now());
        stored.setStatus(order.getStatus());
        stored.getItems().addAll(order.getItems());
        ORDERS.put(id, stored);
        return id;
    }
    
    public Order findById(int orderId) throws SQLException {
        return ORDERS.get(orderId);
    }
    
    public List<Order> findByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : ORDERS.values()) {
            if (order.getUserId() == userId) {
                orders.add(order);
            }
        }
        return orders;
    }
    
    public List<Order> findAll() throws SQLException {
        return new ArrayList<>(ORDERS.values());
    }
    
    public List<Order> findByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        for (Order order : ORDERS.values()) {
            if (status.equals(order.getStatus())) {
                orders.add(order);
            }
        }
        return orders;
    }
    
    public boolean updateStatus(int orderId, String status) throws SQLException {
        Order order = ORDERS.get(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(status);
        return true;
    }
    
    public boolean delete(int orderId) throws SQLException {
        return ORDERS.remove(orderId) != null;
    }
}

