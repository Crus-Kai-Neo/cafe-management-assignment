package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    private static boolean bootstrapDone;
    
    // Database configuration - can be overridden by environment variables
    private static String dbHost = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static int dbPort = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "3306"));
    private static String dbName = System.getenv().getOrDefault("DB_NAME", "cafe_order_system");
    private static String dbUser = System.getenv().getOrDefault("DB_USER", "root");
    private static String dbPassword = System.getenv().getOrDefault("DB_PASSWORD", "Dontbe2L@te4meok");
    
    private static void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                dbHost, dbPort, dbName));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(dbUser);
            config.setPassword(dbPassword);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            
            dataSource = new HikariDataSource(config);
            System.out.println("Database connection pool initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Unable to initialize database", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        try {
            if (!bootstrapDone) {
                ensureDatabaseAndTables();
                bootstrapDone = true;
            }
            if (dataSource == null || dataSource.isClosed()) {
                initializeDataSource();
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLException(buildConnectionErrorMessage(e), e);
        } catch (RuntimeException e) {
            throw new SQLException(buildConnectionErrorMessage(e), e);
        }
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }
    
    public static void setDatabaseConfig(String host, int port, String database, String user, String password) {
        dbHost = host;
        dbPort = port;
        dbName = database;
        dbUser = user;
        dbPassword = password;
        bootstrapDone = false;
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
        dataSource = null;
    }

    private static void ensureDatabaseAndTables() throws SQLException {
        if (!dbName.matches("[A-Za-z0-9_]+")) {
            throw new SQLException("Invalid database name: " + dbName);
        }

        String serverJdbcUrl = String.format(
            "jdbc:mysql://%s:%d/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            dbHost, dbPort
        );
        String databaseJdbcUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            dbHost, dbPort, dbName
        );

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found on classpath.", e);
        }

        try (Connection serverConn = DriverManager.getConnection(serverJdbcUrl, dbUser, dbPassword);
             Statement stmt = serverConn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
        }

        try (Connection dbConn = DriverManager.getConnection(databaseJdbcUrl, dbUser, dbPassword);
             Statement stmt = dbConn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(100),
                    role ENUM('ADMIN', 'CASHIER', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_username (username),
                    INDEX idx_role (role)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS menu_items (
                    item_id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    price DECIMAL(10, 2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_name (name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INT PRIMARY KEY AUTO_INCREMENT,
                    user_id INT NOT NULL,
                    status ENUM('PENDING', 'COMPLETED', 'CANCELED') NOT NULL DEFAULT 'PENDING',
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_user_id (user_id),
                    INDEX idx_status (status),
                    INDEX idx_order_date (order_date),
                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS order_items (
                    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
                    order_id INT NOT NULL,
                    item_id INT NOT NULL,
                    quantity INT NOT NULL DEFAULT 1,
                    subtotal DECIMAL(10, 2) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    INDEX idx_order_id (order_id),
                    INDEX idx_item_id (item_id),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
                    FOREIGN KEY (item_id) REFERENCES menu_items(item_id) ON DELETE RESTRICT
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS payments (
                    payment_id INT PRIMARY KEY AUTO_INCREMENT,
                    order_id INT NOT NULL,
                    amount DECIMAL(10, 2) NOT NULL,
                    payment_method VARCHAR(50) NOT NULL,
                    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
                    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_order_id (order_id),
                    INDEX idx_payment_status (payment_status),
                    UNIQUE KEY unique_payment_per_order (order_id),
                    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        }
    }

    private static String buildConnectionErrorMessage(Throwable t) {
        String msg = t.getMessage() == null ? "Unknown database error." : t.getMessage();
        String lower = msg.toLowerCase();

        if (lower.contains("access denied")) {
            return "MySQL access denied. Check DB_USER/DB_PASSWORD.";
        }
        if (lower.contains("unknown database")) {
            return "Database not found and could not be created. Check privileges for DB_NAME='" + dbName + "'.";
        }
        if (lower.contains("communications link failure") || lower.contains("connection refused") || lower.contains("connect timed out")) {
            return "Cannot reach MySQL server at " + dbHost + ":" + dbPort + ". Ensure MySQL is running.";
        }
        if (lower.contains("driver") && lower.contains("not found")) {
            return "MySQL JDBC driver missing from runtime classpath.";
        }
        return msg;
    }
}

