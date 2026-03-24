package com.assetsystem.repository;

import com.assetsystem.model.Categories;
import com.assetsystem.model.Manufacturer;
import com.assetsystem.model.Models;
import com.assetsystem.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Lookup and CRUD for manufacturers, categories, and models.
 */
public class CatalogRepository {

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT ManID, ManName FROM Manufacturer ORDER BY ManName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Manufacturer(rs.getInt("ManID"), rs.getString("ManName")));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllManufacturers: " + e.getMessage());
        }
        return list;
    }

    /**
     * All models with manufacturer and category (same shape as {@link AssetRepository#getAllModels()}).
     */
    public List<Models> getAllModels() {
        List<Models> list = new ArrayList<>();
        String sql = "SELECT m.ModelID, m.ModelName, "
                + "man.ManID, man.ManName, "
                + "cat.CatID, cat.CatName "
                + "FROM Models m "
                + "INNER JOIN Manufacturer man ON m.ManID = man.ManID "
                + "INNER JOIN Categories cat ON m.CatID = cat.CatID "
                + "ORDER BY cat.CatName, m.ModelName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
                Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
                list.add(new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllModels: " + e.getMessage());
        }
        return list;
    }

    public List<Categories> getAllCategories() {
        List<Categories> list = new ArrayList<>();
        String sql = "SELECT CatID, CatName FROM Categories ORDER BY CatName";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Categories(rs.getInt("CatID"), rs.getString("CatName")));
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAllCategories: " + e.getMessage());
        }
        return list;
    }

    /**
     * @return new ManID, or -1 on failure
     */
    public int insertManufacturer(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }
        String sql = "INSERT INTO Manufacturer (ManName) VALUES (?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name.trim());
            int n = pstmt.executeUpdate();
            if (n != 1) {
                return -1;
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in insertManufacturer: " + e.getMessage());
        }
        return -1;
    }

    /**
     * @return new CatID, or -1 on failure
     */
    public int insertCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            return -1;
        }
        String sql = "INSERT INTO Categories (CatName) VALUES (?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name.trim());
            int n = pstmt.executeUpdate();
            if (n != 1) {
                return -1;
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in insertCategory: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateManufacturer(int manId, String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE Manufacturer SET ManName = ? WHERE ManID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            pstmt.setInt(2, manId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in updateManufacturer: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCategory(int catId, String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE Categories SET CatName = ? WHERE CatID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name.trim());
            pstmt.setInt(2, catId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in updateCategory: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteManufacturer(int manId) {
        if (countModelsByManufacturer(manId) > 0) {
            return false;
        }
        String sql = "DELETE FROM Manufacturer WHERE ManID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, manId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in deleteManufacturer: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int catId) {
        if (countModelsByCategory(catId) > 0) {
            return false;
        }
        String sql = "DELETE FROM Categories WHERE CatID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, catId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in deleteCategory: " + e.getMessage());
            return false;
        }
    }

    public int countModelsByManufacturer(int manId) {
        return countInt("SELECT COUNT(*) FROM Models WHERE ManID = ?", manId);
    }

    public int countModelsByCategory(int catId) {
        return countInt("SELECT COUNT(*) FROM Models WHERE CatID = ?", catId);
    }

    private int countInt(String sql, int param) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, param);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in countInt: " + e.getMessage());
        }
        return 0;
    }

    /**
     * @return new ModelID, or -1 on failure
     */
    public int insertModel(String modelName, int manId, int catId) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return -1;
        }
        String sql = "INSERT INTO Models (ModelName, ManID, CatID) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return -1;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, modelName.trim());
            pstmt.setInt(2, manId);
            pstmt.setInt(3, catId);
            int n = pstmt.executeUpdate();
            if (n != 1) {
                return -1;
            }
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in insertModel: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateModel(int modelId, String modelName, int manId, int catId) {
        if (modelName == null || modelName.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE Models SET ModelName = ?, ManID = ?, CatID = ? WHERE ModelID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, modelName.trim());
            pstmt.setInt(2, manId);
            pstmt.setInt(3, catId);
            pstmt.setInt(4, modelId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in updateModel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a model only when no {@code AssetDetails} row references it.
     */
    public boolean deleteModel(int modelId) {
        if (countAssetsByModel(modelId) > 0) {
            return false;
        }
        String sql = "DELETE FROM Models WHERE ModelID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return false;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, modelId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Database error in deleteModel: " + e.getMessage());
            return false;
        }
    }

    public int countAssetsByModel(int modelId) {
        return countInt("SELECT COUNT(*) FROM AssetDetails WHERE ModelID = ?", modelId);
    }

    public Models findModelById(int modelId) {
        String sql = "SELECT m.ModelID, m.ModelName, man.ManID, man.ManName, cat.CatID, cat.CatName "
                + "FROM Models m "
                + "INNER JOIN Manufacturer man ON m.ManID = man.ManID "
                + "INNER JOIN Categories cat ON m.CatID = cat.CatID "
                + "WHERE m.ModelID = ?";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            return null;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, modelId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Manufacturer man = new Manufacturer(rs.getInt("ManID"), rs.getString("ManName"));
                    Categories cat = new Categories(rs.getInt("CatID"), rs.getString("CatName"));
                    return new Models(rs.getInt("ModelID"), rs.getString("ModelName"), man, cat);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error in findModelById: " + e.getMessage());
        }
        return null;
    }
}
