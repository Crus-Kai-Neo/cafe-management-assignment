package dao;

import model.Role;
import model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserDAO extends BaseDAO {
    private static final AtomicInteger ID_SEQ = new AtomicInteger(1);
    private static final List<User> USERS = new ArrayList<>();
    
    public int create(User user) throws SQLException {
        if (usernameExists(user.getUsername())) {
            throw new SQLException("Username already exists.");
        }
        if (emailExists(user.getEmail())) {
            throw new SQLException("Email already exists.");
        }
        int id = ID_SEQ.getAndIncrement();
        USERS.add(new User(id, user.getUsername(), user.getPassword(), user.getEmail(), user.getRole()));
        return id;
    }
    
    public User findById(int userId) throws SQLException {
        for (User user : USERS) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }
    
    public User findByUsername(String username) throws SQLException {
        for (User user : USERS) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public User findByEmail(String email) throws SQLException {
        for (User user : USERS) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }
    
    public List<User> findAll() throws SQLException {
        return new ArrayList<>(USERS);
    }
    
    public List<User> findByRole(Role role) throws SQLException {
        List<User> users = new ArrayList<>();
        for (User user : USERS) {
            if (user.getRole() == role) {
                users.add(user);
            }
        }
        return users;
    }
    
    public boolean update(User user) throws SQLException {
        for (int i = 0; i < USERS.size(); i++) {
            if (USERS.get(i).getUserId() == user.getUserId()) {
                USERS.set(i, user);
                return true;
            }
        }
        return false;
    }
    
    public boolean delete(int userId) throws SQLException {
        return USERS.removeIf(u -> u.getUserId() == userId);
    }
    
    public boolean usernameExists(String username) throws SQLException {
        for (User user : USERS) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean emailExists(String email) throws SQLException {
        for (User user : USERS) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }
}

