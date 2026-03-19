package dao;

import model.MenuItem;
import model.OrderItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderItemDAO extends BaseDAO {
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);
    private static final Map<Integer, List<OrderItemRow>> ORDER_ITEMS = new ConcurrentHashMap<>();
    
    private static class OrderItemRow {
        final int orderItemId;
        final int itemId;
        int quantity;
        double subtotal;

        OrderItemRow(int orderItemId, int itemId, int quantity, double subtotal) {
            this.orderItemId = orderItemId;
            this.itemId = itemId;
            this.quantity = quantity;
            this.subtotal = subtotal;
        }
    }
    
    public int create(int orderId, int itemId, int quantity, double subtotal) throws SQLException {
        int id = ID_SEQ.getAndIncrement();
        ORDER_ITEMS.computeIfAbsent(orderId, k -> new ArrayList<>())
                .add(new OrderItemRow(id, itemId, quantity, subtotal));
        return id;
    }
    
    public OrderItem findById(int orderItemId, MenuItemDAO menuItemDAO) throws SQLException {
        for (List<OrderItemRow> rows : ORDER_ITEMS.values()) {
            for (OrderItemRow row : rows) {
                if (row.orderItemId != orderItemId) {
                    continue;
                }
                MenuItem menuItem = menuItemDAO.findById(row.itemId);
                if (menuItem != null) {
                    return new OrderItem(menuItem, row.quantity);
                }
            }
        }
        return null;
    }
    
    public List<OrderItem> findByOrderId(int orderId, MenuItemDAO menuItemDAO) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        List<OrderItemRow> rows = ORDER_ITEMS.getOrDefault(orderId, List.of());
        for (OrderItemRow row : rows) {
            MenuItem menuItem = menuItemDAO.findById(row.itemId);
            if (menuItem != null) {
                items.add(new OrderItem(menuItem, row.quantity));
            }
        }
        return items;
    }
    
    public boolean update(int orderItemId, int quantity) throws SQLException {
        for (List<OrderItemRow> rows : ORDER_ITEMS.values()) {
            for (OrderItemRow row : rows) {
                if (row.orderItemId == orderItemId) {
                    row.quantity = quantity;
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean deleteByOrderId(int orderId) throws SQLException {
        return ORDER_ITEMS.remove(orderId) != null;
    }
}

