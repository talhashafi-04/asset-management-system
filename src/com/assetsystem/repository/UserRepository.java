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

    /**
     * Updates stored password (plain text, same as {@link #authenticate}).
     *
     * @return {@code true} if exactly one row was updated
     */
    public boolean updatePassword(int userId, String newPassword) {
        if (newPassword == null) {
            return false;
        }
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in updatePassword: " + e.getMessage());
            return false;
        }
    }
}
