package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.MenuItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuItemDAO extends BaseDAO {
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);
    private static final List<MenuItem> ITEMS = new ArrayList<>();
    
    public int create(MenuItem menuItem) throws SQLException {
        int id = ID_SEQ.getAndIncrement();
        ITEMS.add(new MenuItem(id, menuItem.getName(), menuItem.getPrice()));
        return id;
    }
    
    public MenuItem findById(int itemId) throws SQLException {
        for (MenuItem item : ITEMS) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }
    
    public List<MenuItem> findAll() throws SQLException {
        return new ArrayList<>(ITEMS);
    }
    
    public ObservableList<MenuItem> findAllObservable() throws SQLException {
        return FXCollections.observableArrayList(findAll());
    }
    
    public boolean update(MenuItem menuItem) throws SQLException {
        for (MenuItem item : ITEMS) {
            if (item.getId() == menuItem.getId()) {
                item.setName(menuItem.getName());
                item.setPrice(menuItem.getPrice());
                return true;
            }
        }
        return false;
    }
    
    public boolean delete(int itemId) throws SQLException {
        return ITEMS.removeIf(i -> i.getId() == itemId);
    }
}

