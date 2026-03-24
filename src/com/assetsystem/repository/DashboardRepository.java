package com.assetsystem.repository;

import com.assetsystem.model.DashboardStats;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardRepository {

    public DashboardStats loadDashboardStats() {
        int totalAssets = countOne("SELECT COUNT(*) FROM AssetDetails");
        int totalEmployees = countOne("SELECT COUNT(*) FROM Employees");
        int activeAllocations = countOne("SELECT COUNT(*) FROM Asset_Allocation WHERE ReturnDate IS NULL");
        Map<String, Integer> byStatus = loadAssetCountsByStatus();
        return new DashboardStats(totalAssets, totalEmployees, activeAllocations, byStatus);
    }

    private Map<String, Integer> loadAssetCountsByStatus() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT s.StatusName, COUNT(*) AS Cnt "
                + "FROM AssetDetails a "
                + "INNER JOIN Allocation_Status s ON a.CurrentStatusID = s.StatusID "
                + "GROUP BY s.StatusName "
                + "ORDER BY s.StatusName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return map;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("StatusName"), rs.getInt("Cnt"));
            }
        } catch (SQLException e) {
            System.err.println("Database error in loadAssetCountsByStatus: " + e.getMessage());
        }
        return map;
    }

    private int countOne(String sql) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return 0;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Database error in countOne: " + e.getMessage());
        }
        return 0;
    }
}
