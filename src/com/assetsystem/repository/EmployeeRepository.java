package com.assetsystem.repository;

import com.assetsystem.model.Employees;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    /**
     * All employees. Matches {@code AS.sql}: {@code EmpName}, {@code DeptID} joined to {@code Departments}.
     */
    public List<Employees> getAllEmployees() {
        List<Employees> list = new ArrayList<>();
        String sql = "SELECT e.EmpID, e.EmpName AS FullName, ISNULL(d.DeptName, '') AS DeptName "
                + "FROM Employees e "
                + "LEFT JOIN Departments d ON e.DeptID = d.DeptID "
                + "ORDER BY e.EmpName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Employees(
                        rs.getInt("EmpID"),
                        rs.getString("FullName"),
                        rs.getString("DeptName")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllEmployees: " + e.getMessage());
        }
        return list;
    }

    public Employees findById(int empId) {
        String sql = "SELECT e.EmpID, e.EmpName AS FullName, ISNULL(d.DeptName, '') AS DeptName "
                + "FROM Employees e "
                + "LEFT JOIN Departments d ON e.DeptID = d.DeptID "
                + "WHERE e.EmpID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Employees(
                            rs.getInt("EmpID"),
                            rs.getString("FullName"),
                            rs.getString("DeptName")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findById: " + e.getMessage());
        }
        return null;
    }

    /**
     * @return {@code DeptID} for the employee, or {@code null} if unset.
     */
    public Integer findDeptIdByEmpId(int empId) {
        String sql = "SELECT DeptID FROM Employees WHERE EmpID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int d = rs.getInt("DeptID");
                    if (rs.wasNull()) {
                        return null;
                    }
                    return d;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findDeptIdByEmpId: " + e.getMessage());
        }
        return null;
    }
}
