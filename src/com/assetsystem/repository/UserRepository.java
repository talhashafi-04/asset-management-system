package com.assetsystem.repository;

import com.assetsystem.model.Users;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    /**
     * Returns a user when username and password match. Password is compared as stored (plain text in DB).
     */
    public Users authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String sql = "SELECT UserID, Username, Password, Role FROM Users WHERE Username = ? AND Password = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Users(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("Role"),
                            rs.getString("Password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in authenticate: " + e.getMessage());
        }
        return null;
    }

    public Users findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT UserID, Username, Password, Role FROM Users WHERE Username = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Users(
                            rs.getInt("UserID"),
                            rs.getString("Username"),
                            rs.getString("Role"),
                            rs.getString("Password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findByUsername: " + e.getMessage());
        }
        return null;
    }
}
