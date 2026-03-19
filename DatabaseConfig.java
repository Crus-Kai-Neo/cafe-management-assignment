package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static HikariDataSource dataSource;
    
    // Database configuration - can be overridden by environment variables
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final int DB_PORT = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "3306"));
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "cafe_order_system");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "Dontbe2L@te4meok");
    
    private static void initializeDataSource() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                DB_HOST, DB_PORT, DB_NAME));
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
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
        if (dataSource == null) {
            try {
                initializeDataSource();
            } catch (RuntimeException e) {
                throw new SQLException("Database driver or connection unavailable.", e);
            }
        }
        return dataSource.getConnection();
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed.");
        }
    }
    
    public static void setDatabaseConfig(String host, int port, String database, String user, String password) {
        // Allow for configuration override before pool initialization
        // This is useful for testing or different environments
    }
}

