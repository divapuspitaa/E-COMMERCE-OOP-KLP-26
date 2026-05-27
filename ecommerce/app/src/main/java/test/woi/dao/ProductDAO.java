package test.woi.dao;

import test.woi.model.*;
import test.woi.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ProductDAO - Data Access Object untuk entitas Product.
 * Menangani operasi CRUD dan pencarian produk.
 */
public class ProductDAO {

    private Connection getConn() {
        return DatabaseManager.getInstance().getConnection();
    }

    public List<Product> findAll() {
        return query("SELECT * FROM products WHERE is_active = 1 ORDER BY created_at DESC");
    }

    public List<Product> findByCategory(Product.Category category) {
        String sql = "SELECT * FROM products WHERE is_active=1 AND category=? ORDER BY created_at DESC";
        return queryWithParam(sql, category.name());
    }

    public List<Product> search(String keyword) {
        String sql = "SELECT * FROM products WHERE is_active=1 AND (LOWER(name) LIKE ? OR LOWER(description) LIKE ?) ORDER BY rating DESC";
        String like = "%" + keyword.toLowerCase() + "%";
        List<Product> result = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO] search error: " + e.getMessage());
        }
        return result;
    }

    public List<Product> findBySeller(String sellerId) {
        return queryWithParam("SELECT * FROM products WHERE seller_id=? ORDER BY created_at DESC", sellerId);
    }

    public Optional<Product> findById(String id) {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM products WHERE id=?")) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean save(Product product) {
        String sql = """
            INSERT INTO products (id,name,description,price,stock,category,seller_id,
                image_url,rating,total_reviews,is_active,product_type,
                weight,discount_pct,dimensions,download_url,license_type,validity_days,file_format,
                created_at,updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,datetime('now'),datetime('now'))
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            bindProductParams(ps, product);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO] save error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Product product) {
        String sql = """
            UPDATE products SET name=?,description=?,price=?,stock=?,category=?,
                image_url=?,rating=?,total_reviews=?,is_active=?,
                weight=?,discount_pct=?,dimensions=?,
                download_url=?,license_type=?,validity_days=?,file_format=?,
                updated_at=datetime('now')
            WHERE id=?
            """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getStock());
            ps.setString(5, product.getCategory().name());
            ps.setString(6, product.getImageUrl());
            ps.setDouble(7, product.getRating());
            ps.setInt(8, product.getTotalReviews());
            ps.setInt(9, product.isActive() ? 1 : 0);
            if (product instanceof PhysicalProduct p) {
                ps.setDouble(10, p.getWeight());
                ps.setDouble(11, p.getDiscountPct());
                ps.setString(12, p.getDimensions());
                ps.setNull(13, Types.VARCHAR);
                ps.setNull(14, Types.VARCHAR);
                ps.setNull(15, Types.INTEGER);
                ps.setNull(16, Types.VARCHAR);
            } else if (product instanceof DigitalProduct d) {
                ps.setNull(10, Types.REAL);
                ps.setNull(11, Types.REAL);
                ps.setNull(12, Types.VARCHAR);
                ps.setString(13, d.getDownloadUrl());
                ps.setString(14, d.getLicenseType());
                ps.setInt(15, d.getValidityDays());
                ps.setString(16, d.getFileFormat());
            }
            ps.setString(17, product.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStock(String productId, int newStock) {
        try (PreparedStatement ps = getConn().prepareStatement(
                "UPDATE products SET stock=?, updated_at=datetime('now') WHERE id=?")) {
            ps.setInt(1, newStock);
            ps.setString(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ProductDAO] updateStock error: " + e.getMessage());
            return false;
        }
    }

    public int countAll() {
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM products WHERE is_active=1")) {
            return rs.getInt(1);
        } catch (SQLException e) { return 0; }
    }

    // ─── Internal helpers ──────────────────────────────────────────

    private List<Product> query(String sql) {
        List<Product> list = new ArrayList<>();
        try (Statement s = getConn().createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO] query error: " + e.getMessage());
        }
        return list;
    }

    private List<Product> queryWithParam(String sql, String param) {
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO] queryWithParam error: " + e.getMessage());
        }
        return list;
    }

    private void bindProductParams(PreparedStatement ps, Product product) throws SQLException {
        ps.setString(1, product.getId());
        ps.setString(2, product.getName());
        ps.setString(3, product.getDescription());
        ps.setDouble(4, product.getPrice());
        ps.setInt(5, product.getStock());
        ps.setString(6, product.getCategory().name());
        ps.setString(7, product.getSellerId());
        ps.setString(8, product.getImageUrl());
        ps.setDouble(9, product.getRating());
        ps.setInt(10, product.getTotalReviews());
        ps.setInt(11, product.isActive() ? 1 : 0);

        if (product instanceof PhysicalProduct p) {
            ps.setString(12, "PHYSICAL");
            ps.setDouble(13, p.getWeight());
            ps.setDouble(14, p.getDiscountPct());
            ps.setString(15, p.getDimensions());
            ps.setNull(16, Types.VARCHAR);
            ps.setNull(17, Types.VARCHAR);
            ps.setNull(18, Types.INTEGER);
            ps.setNull(19, Types.VARCHAR);
        } else if (product instanceof DigitalProduct d) {
            ps.setString(12, "DIGITAL");
            ps.setNull(13, Types.REAL);
            ps.setNull(14, Types.REAL);
            ps.setNull(15, Types.VARCHAR);
            ps.setString(16, d.getDownloadUrl());
            ps.setString(17, d.getLicenseType());
            ps.setInt(18, d.getValidityDays());
            ps.setString(19, d.getFileFormat());
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        String type = rs.getString("product_type");

        if ("DIGITAL".equals(type)) {
            return new DigitalProduct(
                rs.getString("id"), rs.getString("name"), rs.getString("description"),
                rs.getDouble("price"), rs.getInt("stock"),
                safeCategory(rs.getString("category")),
                rs.getString("seller_id"), rs.getString("image_url"),
                rs.getDouble("rating"), rs.getInt("total_reviews"),
                rs.getInt("is_active") == 1,
                rs.getString("download_url"), rs.getString("license_type"),
                rs.getInt("validity_days"), rs.getString("file_format")
            );
        } else {
            return new PhysicalProduct(
                rs.getString("id"), rs.getString("name"), rs.getString("description"),
                rs.getDouble("price"), rs.getInt("stock"),
                safeCategory(rs.getString("category")),
                rs.getString("seller_id"), rs.getString("image_url"),
                rs.getDouble("rating"), rs.getInt("total_reviews"),
                rs.getInt("is_active") == 1,
                rs.getDouble("weight"), rs.getDouble("discount_pct"),
                rs.getString("dimensions")
            );
        }
    }

    private Product.Category safeCategory(String value) {
        try { return Product.Category.valueOf(value); }
        catch (Exception e) { return Product.Category.LAINNYA; }
    }
}
