package service;

import dao.OrderDAO;
import dao.OrderItemDAO;
import model.Order;
import model.OrderItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnalyticsService {
    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;

    public AnalyticsService(OrderDAO orderDAO, OrderItemDAO orderItemDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO = orderItemDAO;
    }

    public double getDailyRevenue(LocalDate date) {
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            return completedOrders.stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                    .mapToDouble(Order::getTotal)
                    .sum();
        } catch (SQLException e) {
            System.err.println("Error calculating daily revenue: " + e.getMessage());
            return 0.0;
        }
    }

    public double getWeeklyRevenue(LocalDate date) {
        try {
            WeekFields wf = WeekFields.of(Locale.getDefault());
            int targetWeek = date.get(wf.weekOfWeekBasedYear());
            int targetYear = date.getYear();

            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            return completedOrders.stream()
                    .filter(o -> o.getCreatedAt().getYear() == targetYear &&
                            o.getCreatedAt().get(wf.weekOfWeekBasedYear()) == targetWeek)
                    .mapToDouble(Order::getTotal)
                    .sum();
        } catch (SQLException e) {
            System.err.println("Error calculating weekly revenue: " + e.getMessage());
            return 0.0;
        }
    }

    public double getMonthlyRevenue(LocalDate date) {
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            return completedOrders.stream()
                    .filter(o -> o.getCreatedAt().getYear() == date.getYear() &&
                            o.getCreatedAt().getMonthValue() == date.getMonthValue())
                    .mapToDouble(Order::getTotal)
                    .sum();
        } catch (SQLException e) {
            System.err.println("Error calculating monthly revenue: " + e.getMessage());
            return 0.0;
        }
    }

    public String getMostPopularItem() {
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            Map<String, Integer> qtyMap = new HashMap<>();
            for (Order o : completedOrders) {
                for (OrderItem oi : o.getItems()) {
                    qtyMap.merge(oi.getMenuItem().getName(), oi.getQuantity(), Integer::sum);
                }
            }
            return qtyMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " (" + e.getValue() + " sold)")
                    .orElse("N/A");
        } catch (SQLException e) {
            System.err.println("Error calculating most popular item: " + e.getMessage());
            return "N/A";
        }
    }

    public String getMostProfitableItem() {
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            Map<String, Double> revMap = new HashMap<>();
            for (Order o : completedOrders) {
                for (OrderItem oi : o.getItems()) {
                    revMap.merge(oi.getMenuItem().getName(), oi.getSubtotal(), Double::sum);
                }
            }
            return revMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> e.getKey() + " ($" + String.format("%.2f", e.getValue()) + ")")
                    .orElse("N/A");
        } catch (SQLException e) {
            System.err.println("Error calculating most profitable item: " + e.getMessage());
            return "N/A";
        }
    }

    public double getTotalRevenue() {
        try {
            List<Order> completedOrders = orderDAO.findByStatus("COMPLETED");
            return completedOrders.stream().mapToDouble(Order::getTotal).sum();
        } catch (SQLException e) {
            System.err.println("Error calculating total revenue: " + e.getMessage());
            return 0.0;
        }
    }

    public int getTotalCompletedOrders() {
        try {
            return orderDAO.findByStatus("COMPLETED").size();
        } catch (SQLException e) {
            System.err.println("Error counting completed orders: " + e.getMessage());
            return 0;
        }
    }
}