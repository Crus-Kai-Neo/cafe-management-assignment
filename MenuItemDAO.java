package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO extends BaseDAO {

    public int create(MenuItem menuItem) throws SQLException {
        String sql = "INSERT INTO menu_items (name, price) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, menuItem.getName());
            stmt.setDouble(2, menuItem.getPrice());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            throw new SQLException("Creating menu item failed, no generated ID returned.");
        }
    }

    public MenuItem findById(int itemId) throws SQLException {
        String sql = "SELECT item_id, name, price FROM menu_items WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MenuItem(rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price"));
                }
            }
        }
        return null;
    }

    public List<MenuItem> findAll() throws SQLException {
        String sql = "SELECT item_id, name, price FROM menu_items";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(new MenuItem(rs.getInt("item_id"), rs.getString("name"), rs.getDouble("price")));
            }
        }
        return items;
    }

    public ObservableList<MenuItem> findAllObservable() throws SQLException {
        return FXCollections.observableArrayList(findAll());
    }

    public boolean update(MenuItem menuItem) throws SQLException {
        String sql = "UPDATE menu_items SET name = ?, price = ? WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, menuItem.getName());
            stmt.setDouble(2, menuItem.getPrice());
            stmt.setInt(3, menuItem.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int itemId) throws SQLException {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        }
    }
}

