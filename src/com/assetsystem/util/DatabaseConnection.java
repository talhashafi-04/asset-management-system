package com.assetsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    // 127.0.0.1 is the universal address for 'this computer'
	private static final String URL = "jdbc:sqlserver://127.0.0.1:1433;databaseName=AssetSystem;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa"; 
    private static final String PASS = "Admin@12345"; // Use the password you set in Step 2

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.err.println("Connection Failed! Ensure Port 1433 is enabled in SQL Config Manager.");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (getConnection() != null) {
            System.out.println("SUCCESS: Universal SQL Authentication Established!");
        }
    }
}
