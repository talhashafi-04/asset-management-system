package com.assetsystem.repository;

import com.assetsystem.util.DatabaseConnection;
import com.assetsystem.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetRepository {

    public List<AssetDetails> getAllAssets() {
        List<AssetDetails> assetList = new ArrayList<>();
        
        // REMOVED: a.CurrentLocID and JOIN Locations l
        String sql = "SELECT a.AssetID, a.SerialNo, a.PurchaseDate, a.Price, a.Remarks, " +
                     "m.ModelID, m.ModelName, " +
                     "man.ManID, man.ManName, " +
                     "cat.CatID, cat.CatName, " +
                     "s.StatusName " + 
                     "FROM AssetDetails a " +
                     "INNER JOIN Models m ON a.ModelID = m.ModelID " +
                     "INNER JOIN Manufacturer man ON m.ManID = man.ManID " +
                     "INNER JOIN Categories cat ON m.CatID = cat.CatID " +
                     "INNER JOIN Allocation_Status s ON a.CurrentStatusID = s.StatusID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 1. Map Parent Models
                Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
                Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
                
                // 2. Map the Device Model
                Models model = new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat);
                
                // 3. Map the Final Asset (Location is now handled in Allocation, so it's gone from here)
                AssetDetails asset = new AssetDetails(
                    rs.getInt("AssetID"),
                    rs.getString("SerialNo"),
                    model,
                    rs.getDate("PurchaseDate"),
                    rs.getDouble("Price"),
                    rs.getString("StatusName"),
                    rs.getString("Remarks")
                );

                assetList.add(asset);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllAssets: " + e.getMessage());
            // It's good practice to re-throw or handle properly
        }
        return assetList;
    }
}