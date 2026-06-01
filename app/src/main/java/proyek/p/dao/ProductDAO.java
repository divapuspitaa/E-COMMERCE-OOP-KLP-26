package proyek.p.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import proyek.p.model.DataStore;
import proyek.p.model.Product;

/**
 * Data Access Object untuk operasi CRUD produk.
 */
public class ProductDAO {

    private final DataStore store = DataStore.getInstance();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua produk yang tersedia. */
    public List<Product> findAll() {
        return store.getAllProducts();
    }

    /** Mengambil semua produk milik seller tertentu. */
    public List<Product> findBySellerId(String sellerId) {
        return store.getProductsBySeller(sellerId);
    }

    /** Mencari produk berdasarkan ID. */
    public Optional<Product> findById(String id) {
        return store.findProduct(id);
    }

    /**
     * Mencari produk berdasarkan kata kunci nama atau kategori.
     * Pencarian tidak case-sensitive.
     */
    public List<Product> search(String keyword) {
        String kw = keyword.trim().toLowerCase();
        return store.getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(kw)
                          || p.getCategory().toLowerCase().contains(kw)
                          || p.getDescription().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    /**
     * Mengambil produk berdasarkan kategori.
     * Jika kategori "Semua" atau kosong, kembalikan semua produk.
     */
    public List<Product> findByCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("Semua")) {
            return store.getAllProducts();
        }
        return store.getAllProducts().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /** Mengambil produk yang masih memiliki stok (stock > 0). */
    public List<Product> findAvailable() {
        return store.getAllProducts().stream()
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /** Menyimpan produk baru ke store. */
    public void save(Product product) {
        store.addProduct(product);
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    /**
     * Memperbarui data produk yang sudah ada.
     * Field yang diperbarui: nama, deskripsi, harga, stok, kategori.
     */
    public boolean update(String productId, String name, String description,
                          double price, int stock, String category) {
        Optional<Product> opt = store.findProduct(productId);
        if (opt.isEmpty()) return false;
        Product p = opt.get();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setCategory(category);
        return true;
    }

    /**
     * Mengurangi stok produk setelah transaksi berhasil.
     * @return false jika stok tidak mencukupi.
     */
    public boolean reduceStock(String productId, int qty) {
        Optional<Product> opt = store.findProduct(productId);
        if (opt.isEmpty()) return false;
        Product p = opt.get();
        if (p.getStock() < qty) return false;
        p.setStock(p.getStock() - qty);
        return true;
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    /** Menghapus produk berdasarkan ID. */
    public void delete(String productId) {
        store.deleteProduct(productId);
    }

    // ── Utility ──────────────────────────────────────────────────────────────────

    /** Generate ID unik untuk produk baru. */
    public String generateId() {
        return "p-" + store.generateId();
    }
}
