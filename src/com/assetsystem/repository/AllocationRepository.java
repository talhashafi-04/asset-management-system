package com.assetsystem.repository;

import com.assetsystem.model.AssetDetails;
import com.assetsystem.model.Asset_Allocation;
import com.assetsystem.model.Employees;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Asset_Allocation} as in {@code sql/AS.sql}: {@code AllocID}, {@code AllocationDate},
 * {@code StatusID}, {@code DeptID}, etc.
 */
public class AllocationRepository {

    private static final String ALLOCATION_SELECT =
            "SELECT al.AllocID AS AllocationID, al.AllocationDate AS AllocateDate, al.ReturnDate, al.Notes, "
                    + "a.AssetID, a.SerialNo, a.PurchaseDate, a.Price, a.Remarks, "
                    + "m.ModelID, m.ModelName, man.ManID, man.ManName, cat.CatID, cat.CatName, "
                    + "s.StatusName, "
                    + "al.EmpID, ISNULL(e.EmpName, '') AS FullName, ISNULL(d.DeptName, '') AS DeptName, "
                    + "ISNULL(l.LocName, '') AS LocName, "
                    + "ISNULL(alloc_st.StatusName, '') AS AllocStatusName "
                    + "FROM Asset_Allocation al "
                    + "INNER JOIN AssetDetails a ON al.AssetID = a.AssetID "
                    + "INNER JOIN Models m ON a.ModelID = m.ModelID "
                    + "INNER JOIN Manufacturer man ON m.ManID = man.ManID "
                    + "INNER JOIN Categories cat ON m.CatID = cat.CatID "
                    + "INNER JOIN Allocation_Status s ON a.CurrentStatusID = s.StatusID "
                    + "LEFT JOIN Employees e ON al.EmpID = e.EmpID "
                    + "LEFT JOIN Departments d ON al.DeptID = d.DeptID "
                    + "LEFT JOIN Locations l ON al.LocID = l.LocID "
                    + "LEFT JOIN Allocation_Status alloc_st ON al.StatusID = alloc_st.StatusID ";

    public List<Asset_Allocation> getAllAllocations() {
        List<Asset_Allocation> list = new ArrayList<>();
        String sql = ALLOCATION_SELECT + "ORDER BY al.AllocationDate DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllAllocations: " + e.getMessage());
        }
        return list;
    }

    /**
     * History for one asset (life cycle), newest first.
     */
    public List<Asset_Allocation> getAllocationsByAssetId(int assetId) {
        List<Asset_Allocation> list = new ArrayList<>();
        String sql = ALLOCATION_SELECT + "WHERE al.AssetID = ? ORDER BY al.AllocationDate DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllocationsByAssetId: " + e.getMessage());
        }
        return list;
    }

    /**
     * Open allocations: {@code ReturnDate} is null.
     */
    public List<Asset_Allocation> getActiveAllocations() {
        List<Asset_Allocation> list = new ArrayList<>();
        String sql = ALLOCATION_SELECT + "WHERE al.ReturnDate IS NULL ORDER BY al.AllocationDate DESC";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getActiveAllocations: " + e.getMessage());
        }
        return list;
    }

    /**
     * Creates an allocation row. Optional fields may be null.
     */
    public boolean insertAllocation(int assetId, int empId, Date allocateDate, Date returnDate, String notes,
                                    Integer locId, Integer allocationStatusId, Integer deptId) {
        String sql = "INSERT INTO Asset_Allocation (AssetID, EmpID, StatusID, AllocationDate, ReturnDate, Notes, LocID, DeptID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, assetId);
            pstmt.setInt(2, empId);
            if (allocationStatusId == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, allocationStatusId);
            }
            pstmt.setDate(4, allocateDate);
            if (returnDate == null) {
                pstmt.setNull(5, Types.DATE);
            } else {
                pstmt.setDate(5, returnDate);
            }
            if (notes == null || notes.trim().isEmpty()) {
                pstmt.setNull(6, Types.NVARCHAR);
            } else {
                pstmt.setString(6, notes.trim());
            }
            if (locId == null) {
                pstmt.setNull(7, Types.INTEGER);
            } else {
                pstmt.setInt(7, locId);
            }
            if (deptId == null) {
                pstmt.setNull(8, Types.INTEGER);
            } else {
                pstmt.setInt(8, deptId);
            }
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in insertAllocation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts {@code Asset_Allocation} and sets {@code AssetDetails.CurrentStatusID} to <em>Allocated</em> in one transaction.
     */
    public boolean allocateFromStore(int assetId, int empId, Integer locId, Integer deptId, String notes,
                                     Timestamp allocationDateTime) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        Integer allocatedStatusId = fetchStatusIdByName(conn, "Allocated");
        if (allocatedStatusId == null) {
            return false;
        }
        String insert = "INSERT INTO Asset_Allocation (AssetID, EmpID, StatusID, AllocationDate, ReturnDate, Notes, LocID, DeptID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateAsset = "UPDATE AssetDetails SET CurrentStatusID = ? WHERE AssetID = ?";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setInt(1, assetId);
                ps.setInt(2, empId);
                ps.setInt(3, allocatedStatusId);
                ps.setTimestamp(4, allocationDateTime);
                ps.setNull(5, Types.TIMESTAMP);
                if (notes == null || notes.trim().isEmpty()) {
                    ps.setNull(6, Types.NVARCHAR);
                } else {
                    ps.setString(6, notes.trim());
                }
                if (locId == null) {
                    ps.setNull(7, Types.INTEGER);
                } else {
                    ps.setInt(7, locId);
                }
                if (deptId == null) {
                    ps.setNull(8, Types.INTEGER);
                } else {
                    ps.setInt(8, deptId);
                }
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("INSERT Asset_Allocation failed");
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(updateAsset)) {
                ps.setInt(1, allocatedStatusId);
                ps.setInt(2, assetId);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("UPDATE AssetDetails failed");
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Database error in allocateFromStore: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    private static Integer fetchStatusIdByName(Connection conn, String statusName) {
        String sql = "SELECT StatusID FROM Allocation_Status WHERE StatusName = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, statusName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StatusID");
                }
            }
        } catch (SQLException e) {
            System.err.println("fetchStatusIdByName: " + e.getMessage());
        }
        return null;
    }

    public boolean updateReturnDate(int allocationId, Date returnDate) {
        String sql = "UPDATE Asset_Allocation SET ReturnDate = ? WHERE AllocID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, returnDate);
            pstmt.setInt(2, allocationId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in updateReturnDate: " + e.getMessage());
            return false;
        }
    }

    private static Asset_Allocation mapRow(ResultSet rs) throws SQLException {
        AssetDetails asset = AssetRowMapper.mapAssetDetails(rs);
        int empId = rs.getInt("EmpID");
        if (rs.wasNull()) {
            empId = -1;
        }
        Employees emp = new Employees(
                empId,
                rs.getString("FullName"),
                rs.getString("DeptName")
        );
        String allocStatus = rs.getString("AllocStatusName");
        if (allocStatus == null) {
            allocStatus = "";
        }
        java.sql.Date ret = rs.getDate("ReturnDate");
        if (rs.wasNull()) {
            ret = null;
        }
        String notes = rs.getString("Notes");
        return new Asset_Allocation(
                rs.getInt("AllocationID"),
                asset,
                emp,
                allocStatus,
                rs.getString("LocName"),
                rs.getString("DeptName"),
                rs.getDate("AllocateDate"),
                ret,
                notes
        );
    }
}
