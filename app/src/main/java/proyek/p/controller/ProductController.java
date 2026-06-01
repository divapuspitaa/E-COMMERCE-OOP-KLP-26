package proyek.p.controller;

import proyek.p.dao.ProductDAO;
import proyek.p.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Controller yang menangani logika bisnis produk:
 * menambah, memperbarui, menghapus, dan mencari produk.
 */
public class ProductController {

    private final ProductDAO productDAO = new ProductDAO();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua produk yang tersedia. */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    /** Mengambil semua produk yang masih memiliki stok. */
    public List<Product> getAvailableProducts() {
        return productDAO.findAvailable();
    }

    /** Mengambil semua produk milik seller tertentu. */
    public List<Product> getProductsBySeller(String sellerId) {
        return productDAO.findBySellerId(sellerId);
    }

    /** Mencari produk berdasarkan kata kunci. */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) return productDAO.findAvailable();
        return productDAO.search(keyword);
    }

    /** Mengambil produk berdasarkan kategori. */
    public List<Product> getByCategory(String category) {
        return productDAO.findByCategory(category);
    }

    /** Mencari produk berdasarkan ID. */
    public Optional<Product> findById(String productId) {
        return productDAO.findById(productId);
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /**
     * Menambahkan produk baru oleh seller.
     * @return pesan error, atau null jika berhasil.
     */
    public String addProduct(String name, String description, String priceStr,
                             String stockStr, String category,
                             String sellerId, String sellerName) {
        String err = validateProductInput(name, description, priceStr, stockStr, category);
        if (err != null) return err;

        double price = Double.parseDouble(priceStr.trim());
        int    stock = Integer.parseInt(stockStr.trim());
        String id    = productDAO.generateId();

        productDAO.save(new Product(id, name.trim(), description.trim(),
                                    price, stock, category, sellerId, sellerName));
        return null;
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    /**
     * Memperbarui data produk yang sudah ada.
     * @return pesan error, atau null jika berhasil.
     */
    public String updateProduct(String productId, String name, String description,
                                String priceStr, String stockStr, String category) {
        String err = validateProductInput(name, description, priceStr, stockStr, category);
        if (err != null) return err;

        double price = Double.parseDouble(priceStr.trim());
        int    stock = Integer.parseInt(stockStr.trim());

        boolean ok = productDAO.update(productId, name.trim(), description.trim(),
                                       price, stock, category);
        if (!ok) return "Produk tidak ditemukan.";
        return null;
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    /**
     * Menghapus produk. Hanya seller pemilik atau admin yang boleh.
     * @return pesan error, atau null jika berhasil.
     */
    public String deleteProduct(String productId, String requesterId, boolean isAdmin) {
        Optional<Product> opt = productDAO.findById(productId);
        if (opt.isEmpty()) return "Produk tidak ditemukan.";

        if (!isAdmin && !opt.get().getSellerId().equals(requesterId)) {
            return "Anda tidak memiliki izin untuk menghapus produk ini.";
        }

        productDAO.delete(productId);
        return null;
    }

    // ── Stock ────────────────────────────────────────────────────────────────────

    /**
     * Mengurangi stok produk setelah pembelian.
     * @return false jika stok tidak mencukupi atau produk tidak ada.
     */
    public boolean reduceStock(String productId, int quantity) {
        return productDAO.reduceStock(productId, quantity);
    }

    // ── Validasi ─────────────────────────────────────────────────────────────────

    private String validateProductInput(String name, String description,
                                        String priceStr, String stockStr, String category) {
        if (name == null || name.isBlank())
            return "Nama produk tidak boleh kosong.";
        if (name.trim().length() < 3)
            return "Nama produk minimal 3 karakter.";
        if (description == null || description.isBlank())
            return "Deskripsi produk tidak boleh kosong.";
        if (priceStr == null || priceStr.isBlank())
            return "Harga tidak boleh kosong.";
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price <= 0) return "Harga harus lebih dari 0.";
        } catch (NumberFormatException e) {
            return "Format harga tidak valid (gunakan angka).";
        }
        if (stockStr == null || stockStr.isBlank())
            return "Stok tidak boleh kosong.";
        try {
            int stock = Integer.parseInt(stockStr.trim());
            if (stock < 0) return "Stok tidak boleh negatif.";
        } catch (NumberFormatException e) {
            return "Format stok tidak valid (gunakan bilangan bulat).";
        }
        if (category == null || category.isBlank())
            return "Pilih kategori produk.";
        return null;
    }
}
