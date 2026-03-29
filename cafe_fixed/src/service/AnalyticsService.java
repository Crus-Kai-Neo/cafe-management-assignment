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
    private final OrderDAO     orderDAO;
    private final OrderItemDAO orderItemDAO;

    public AnalyticsService(OrderDAO orderDAO, OrderItemDAO orderItemDAO) {
        this.orderDAO     = orderDAO;
        this.orderItemDAO = orderItemDAO;
    }

    public double getDailyRevenue(LocalDate date) {
        try {
            return orderDAO.findByStatus("COMPLETED").stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(date))
                    .mapToDouble(Order::getTotal).sum();
        } catch (SQLException e) { System.err.println(e.getMessage()); return 0; }
    }

    public double getWeeklyRevenue(LocalDate date) {
        try {
            WeekFields wf = WeekFields.of(Locale.getDefault());
            int week = date.get(wf.weekOfWeekBasedYear());
            int year = date.getYear();
            return orderDAO.findByStatus("COMPLETED").stream()
                    .filter(o -> o.getCreatedAt().getYear() == year &&
                            o.getCreatedAt().get(wf.weekOfWeekBasedYear()) == week)
                    .mapToDouble(Order::getTotal).sum();
        } catch (SQLException e) { System.err.println(e.getMessage()); return 0; }
    }

    public double getMonthlyRevenue(LocalDate date) {
        try {
            return orderDAO.findByStatus("COMPLETED").stream()
                    .filter(o -> o.getCreatedAt().getYear() == date.getYear() &&
                            o.getCreatedAt().getMonthValue() == date.getMonthValue())
                    .mapToDouble(Order::getTotal).sum();
        } catch (SQLException e) { System.err.println(e.getMessage()); return 0; }
    }

    public double getTotalRevenue() {
        try {
            return orderDAO.findByStatus("COMPLETED").stream()
                    .mapToDouble(Order::getTotal).sum();
        } catch (SQLException e) { System.err.println(e.getMessage()); return 0; }
    }

    public int getTotalCompletedOrders() {
        try { return orderDAO.findByStatus("COMPLETED").size(); }
        catch (SQLException e) { System.err.println(e.getMessage()); return 0; }
    }

    public String getMostPopularItem() {
        try {
            return orderItemDAO.findMostPopularCompletedItem();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "N/A";
        }
    }

    public String getMostProfitableItem() {
        try {
            return orderItemDAO.findMostProfitableCompletedItem();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "N/A";
        }
    }
}