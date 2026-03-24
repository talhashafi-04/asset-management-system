package com.assetsystem.repository;

import com.assetsystem.model.AssetDetails;
import com.assetsystem.model.Categories;
import com.assetsystem.model.Manufacturer;
import com.assetsystem.model.Models;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps joined {@code AssetDetails} + {@code Models} + manufacturer/category + status rows.
 * Expects columns: AssetID, SerialNo, PurchaseDate, Price, Remarks, ModelID, ModelName,
 * ManID, ManName, CatID, CatName, StatusName.
 */
public final class AssetRowMapper {

    private AssetRowMapper() {
    }

    public static AssetDetails mapAssetDetails(ResultSet rs) throws SQLException {
        Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
        Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
        Models model = new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat);
        return new AssetDetails(
                rs.getInt("AssetID"),
                rs.getString("SerialNo"),
                model,
                rs.getDate("PurchaseDate"),
                rs.getDouble("Price"),
                rs.getString("StatusName"),
                rs.getString("Remarks")
        );
    }

    /**
     * Same base columns as {@link #mapAssetDetails} plus {@code AllocEmpName}, {@code AllocDeptName}, {@code ActiveAllocDate}.
     */
    public static AssetDetails mapAllocatedAssetRow(ResultSet rs) throws SQLException {
        Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
        Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
        Models model = new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat);
        String emp = rs.getString("AllocEmpName");
        String dept = rs.getString("AllocDeptName");
        Date allocDate = rs.getDate("ActiveAllocDate");
        if (rs.wasNull()) {
            allocDate = null;
        }
        return new AssetDetails(
                rs.getInt("AssetID"),
                rs.getString("SerialNo"),
                model,
                rs.getDate("PurchaseDate"),
                rs.getDouble("Price"),
                rs.getString("StatusName"),
                rs.getString("Remarks"),
                emp,
                dept,
                allocDate
        );
    }
}
