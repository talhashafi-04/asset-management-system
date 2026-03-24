package com.assetsystem.repository;

import com.assetsystem.util.DatabaseConnection;
import com.assetsystem.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetRepository {

    private static final String ASSET_LIST_SQL =
            "SELECT a.AssetID, a.SerialNo, a.PurchaseDate, a.Price, a.Remarks, "
                    + "m.ModelID, m.ModelName, "
                    + "man.ManID, man.ManName, "
                    + "cat.CatID, cat.CatName, "
                    + "s.StatusName "
                    + "FROM AssetDetails a "
                    + "INNER JOIN Models m ON a.ModelID = m.ModelID "
                    + "INNER JOIN Manufacturer man ON m.ManID = man.ManID "
                    + "INNER JOIN Categories cat ON m.CatID = cat.CatID "
                    + "INNER JOIN Allocation_Status s ON a.CurrentStatusID = s.StatusID";

    /** Allocated assets with active (open) allocation and employee / department. */
    private static final String ALLOCATED_WITH_EMP_SQL =
            "SELECT a.AssetID, a.SerialNo, a.PurchaseDate, a.Price, a.Remarks, "
                    + "m.ModelID, m.ModelName, "
                    + "man.ManID, man.ManName, "
                    + "cat.CatID, cat.CatName, "
                    + "s.StatusName, "
                    + "ISNULL(e.EmpName, '') AS AllocEmpName, "
                    + "ISNULL(d.DeptName, '') AS AllocDeptName, "
                    + "al.AllocationDate AS ActiveAllocDate "
                    + "FROM AssetDetails a "
                    + "INNER JOIN Models m ON a.ModelID = m.ModelID "
                    + "INNER JOIN Manufacturer man ON m.ManID = man.ManID "
                    + "INNER JOIN Categories cat ON m.CatID = cat.CatID "
                    + "INNER JOIN Allocation_Status s ON a.CurrentStatusID = s.StatusID "
                    + "OUTER APPLY ( "
                    + "  SELECT TOP 1 al.* FROM Asset_Allocation al "
                    + "  WHERE al.AssetID = a.AssetID AND al.ReturnDate IS NULL "
                    + "  ORDER BY al.AllocationDate DESC "
                    + ") al "
                    + "LEFT JOIN Employees e ON al.EmpID = e.EmpID "
                    + "LEFT JOIN Departments d ON al.DeptID = d.DeptID "
                    + "WHERE s.StatusName = ?";

    public List<AllocationStatus> getAllAllocationStatuses() {
        List<AllocationStatus> list = new ArrayList<>();
        String sql = "SELECT StatusID, StatusName FROM Allocation_Status ORDER BY StatusID";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new AllocationStatus(rs.getInt("StatusID"), rs.getString("StatusName")));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllAllocationStatuses: " + e.getMessage());
        }
        return list;
    }

    public List<Models> getAllModels() {
        List<Models> list = new ArrayList<>();
        String sql = "SELECT m.ModelID, m.ModelName, " +
                     "man.ManID, man.ManName, " +
                     "cat.CatID, cat.CatName " +
                     "FROM Models m " +
                     "INNER JOIN Manufacturer man ON m.ManID = man.ManID " +
                     "INNER JOIN Categories cat ON m.CatID = cat.CatID " +
                     "ORDER BY cat.CatName, m.ModelName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
                Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
                Models model = new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat);
                list.add(model);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllModels: " + e.getMessage());
        }
        return list;
    }

    public boolean serialNumberExists(String serialNo) {
        if (serialNo == null || serialNo.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM AssetDetails WHERE SerialNo = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serialNo.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Database error in serialNumberExists: " + e.getMessage());
            return false;
        }
    }

    public boolean insertAsset(String serialNo, Date purchaseDate, double price, String remarks,
                               int modelId, int statusId) {
        String sql = "INSERT INTO AssetDetails (SerialNo, PurchaseDate, Price, Remarks, ModelID, CurrentStatusID) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, serialNo.trim());
            pstmt.setDate(2, purchaseDate);
            pstmt.setDouble(3, price);
            if (remarks == null || remarks.trim().isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, remarks.trim());
            }
            pstmt.setInt(5, modelId);
            pstmt.setInt(6, statusId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in insertAsset: " + e.getMessage());
            return false;
        }
    }

    /**
     * All assets with model, manufacturer, category, and current allocation status.
     */
    public List<AssetDetails> getAllAssets() {
        return queryAssets(null);
    }

    /**
     * Assets whose current {@code Allocation_Status.StatusName} equals the given value (e.g. "In Store").
     */
    public List<AssetDetails> getAssetsByStatusName(String statusName) {
        if (statusName == null || statusName.trim().isEmpty()) {
            return getAllAssets();
        }
        return queryAssets(statusName.trim());
    }

    /**
     * Assets currently in store; {@code Allocation_Status.StatusName} must be exactly {@code In Store} in the database.
     */
    public List<AssetDetails> getAssetsInStore() {
        return getAssetsByStatusName("In Store");
    }

    /**
     * Assets with status Allocated, including active open allocation: employee name, department, allocation date.
     */
    public List<AssetDetails> getAllocatedAssetsWithAssignee() {
        List<AssetDetails> assetList = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return assetList;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(ALLOCATED_WITH_EMP_SQL)) {
            pstmt.setString(1, "Allocated");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assetList.add(AssetRowMapper.mapAllocatedAssetRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllocatedAssetsWithAssignee: " + e.getMessage());
        }
        return assetList;
    }

    private List<AssetDetails> queryAssets(String statusNameOrNull) {
        List<AssetDetails> assetList = new ArrayList<>();
        String sql = ASSET_LIST_SQL;
        if (statusNameOrNull != null) {
            sql += " WHERE s.StatusName = ?";
        }
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return assetList;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (statusNameOrNull != null) {
                pstmt.setString(1, statusNameOrNull);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    assetList.add(AssetRowMapper.mapAssetDetails(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in queryAssets: " + e.getMessage());
        }
        return assetList;
    }
}