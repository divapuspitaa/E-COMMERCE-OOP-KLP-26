package test.woi.service;

import test.woi.dao.ProductDAO;
import test.woi.model.*;
import test.woi.util.SessionManager;

import java.util.List;

/**
 * ProductService - logika bisnis untuk manajemen produk.
 *
 * Fitur 2: Setiap penjual hanya dapat melihat dan mengelola produk
 * yang mereka tambahkan sendiri (berdasarkan seller_id).
 * Pembeli/halaman publik tetap melihat semua produk aktif.
 */
public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    /** Semua produk aktif — digunakan oleh halaman publik (Home, Cart, dll.) */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Produk yang dikelola penjual saat ini.
     * Jika bukan penjual, kembalikan semua produk (fallback admin).
     */
    public List<Product> getMyProducts() {
        String sellerId = currentSellerId();
        if (sellerId == null) return productDAO.findAll();
        return productDAO.findBySeller(sellerId);
    }

    /** Cari produk — jika penjual, cari hanya dalam produk miliknya */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) return getMyProducts();
        String sellerId = currentSellerId();
        if (sellerId != null) {
            // Filter hasil pencarian global lalu saring per seller
            return productDAO.search(keyword.trim()).stream()
                    .filter(p -> sellerId.equals(p.getSellerId()))
                    .toList();
        }
        return productDAO.search(keyword.trim());
    }

    public List<Product> getByCategory(Product.Category category) {
        return productDAO.findByCategory(category);
    }

    public boolean saveProduct(Product product) {
        if (!product.isValid()) return false;
        // Paksa seller_id sesuai penjual yang sedang login
        String sellerId = currentSellerId();
        if (sellerId != null) product.setSellerId(sellerId);
        return productDAO.save(product);
    }

    public boolean updateProduct(Product product) {
        if (!product.isValid()) return false;
        // Validasi kepemilikan: penjual hanya boleh update produknya sendiri
        String sellerId = currentSellerId();
        if (sellerId != null && !sellerId.equals(product.getSellerId())) {
            System.err.println("[ProductService] Akses ditolak: penjual tidak memiliki produk " + product.getId());
            return false;
        }
        return productDAO.update(product);
    }

    public int countProducts() { return productDAO.countAll(); }

    // ─── Helper ────────────────────────────────────────────────────

    /** Kembalikan ID penjual yang sedang login, atau null jika bukan penjual */
    private String currentSellerId() {
        var session = SessionManager.getInstance();
        if (session.isLoggedIn() && session.getCurrentUser().isSeller()) {
            return session.getCurrentUser().getId();
        }
        return null;
    }
}
