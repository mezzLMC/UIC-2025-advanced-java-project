package com.projet1.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // Database configuration - modify these values for your setup
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "projet1_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static final String URL = String.format(
        "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
        HOST, PORT, DATABASE
    );
    
    private static Connection connection = null;
    
    /**
     * Get a connection to the database.
     * Creates a new connection if one doesn't exist or is closed.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the MySQL driver (optional for newer JDBC versions)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }
    
    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DATABASE CONNECTION] Database connection closed.");
            } catch (SQLException e) {
                System.err.println("[DATABASE CONNECTION] Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
