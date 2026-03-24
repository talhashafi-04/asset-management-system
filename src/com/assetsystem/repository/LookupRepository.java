package com.assetsystem.repository;

import com.assetsystem.model.Department;
import com.assetsystem.model.Location;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LookupRepository {

    public List<Department> listDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT DeptID, DeptName FROM Departments ORDER BY DeptName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Department(rs.getInt("DeptID"), rs.getString("DeptName")));
            }
        } catch (SQLException e) {
            System.err.println("Database error in listDepartments: " + e.getMessage());
        }
        return list;
    }

    public List<Location> listLocations() {
        List<Location> list = new ArrayList<>();
        String sql = "SELECT LocID, LocName FROM Locations ORDER BY LocName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Location(rs.getInt("LocID"), rs.getString("LocName")));
            }
        } catch (SQLException e) {
            System.err.println("Database error in listLocations: " + e.getMessage());
        }
        return list;
    }
}
