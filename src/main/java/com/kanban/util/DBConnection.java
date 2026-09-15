package com.kanban.util;

import java.sql.*;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kanban_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "zaid$232006"; // ⚠️ CHANGE THIS!
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✅ Database connected!");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
        }
        return conn;
    }
}