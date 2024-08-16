package org.example.smwithhikaricp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static HikariDataSource dataSource;

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                System.exit(1);
            }
            properties.load(input);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(getDbUrl());
            config.setUsername(getDbUsername());
            config.setPassword(getDbPassword());

            // Các cấu hình tùy chọn khác của HikariCP
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);

            // Tạo HikariDataSource
            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return null;
        }
    }

    public static void closeResources(Connection con, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("spring.datasource.url");
    }

    public static String getDbUsername() {
        return properties.getProperty("spring.datasource.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("spring.datasource.password");
    }
}

