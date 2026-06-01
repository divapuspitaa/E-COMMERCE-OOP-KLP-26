package proyek.p.controller;

import proyek.p.dao.ProductDAO;
import proyek.p.dao.TransactionDAO;
import proyek.p.model.Product;
import proyek.p.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Controller yang menangani logika transaksi pembelian:
 * membuat transaksi baru, mengambil riwayat, dan menghitung statistik.
 */
public class TransactionController {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final ProductDAO     productDAO     = new ProductDAO();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua transaksi milik customer, terbaru di atas. */
    public List<Transaction> getTransactionsByCustomer(String customerId) {
        return transactionDAO.findByCustomerId(customerId);
    }

    /** Mengambil semua transaksi yang masuk ke seller, terbaru di atas. */
    public List<Transaction> getTransactionsBySeller(String sellerId) {
        return transactionDAO.findBySellerId(sellerId);
    }

    /** Menghitung total pendapatan seller dari semua transaksinya. */
    public double getTotalRevenueBySeller(String sellerId) {
        return transactionDAO.getTotalRevenueBySeller(sellerId);
    }

    /** Menghitung total belanja customer. */
    public double getTotalSpendingByCustomer(String customerId) {
        return transactionDAO.getTotalSpendingByCustomer(customerId);
    }

    /** Menghitung total item yang terjual oleh seller. */
    public int getTotalItemsSoldBySeller(String sellerId) {
        return transactionDAO.getTotalItemsSoldBySeller(sellerId);
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /**
     * Membuat transaksi pembelian baru.
     * Mengurangi stok produk secara otomatis.
     *
     * @param customerId   ID customer yang membeli
     * @param customerName Nama/username customer
     * @param productId    ID produk yang dibeli
     * @param quantity     Jumlah item yang dibeli
     * @return Transaction jika berhasil, atau null jika gagal
     * @throws IllegalArgumentException jika produk tidak ditemukan, stok kurang, atau qty invalid
     */
    public Transaction createTransaction(String customerId, String customerName,
                                         String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah pembelian harus minimal 1.");
        }

        Optional<Product> opt = productDAO.findById(productId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Produk tidak ditemukan.");
        }

        Product product = opt.get();

        if (product.getStock() < quantity) {
            throw new IllegalArgumentException(
                "Stok tidak mencukupi. Stok tersedia: " + product.getStock() + " item."
            );
        }

        // Kurangi stok
        boolean reduced = productDAO.reduceStock(productId, quantity);
        if (!reduced) {
            throw new IllegalArgumentException("Gagal mengurangi stok produk.");
        }

        double total = product.getPrice() * quantity;

        // Buat ID transaksi unik
        String txId = "tx-" + java.util.UUID.randomUUID().toString().substring(0, 6);

        Transaction tx = new Transaction(
            txId,
            customerId,
            customerName,
            product.getSellerId(),
            productId,
            product.getName(),
            quantity,
            total
        );

        transactionDAO.save(tx);
        return tx;
    }

    /**
     * Validasi kuantitas sebelum checkout (untuk cek real-time di UI).
     * @return pesan error, atau null jika valid.
     */
    public String validateQuantity(String productId, int qty) {
        if (qty <= 0) return "Jumlah minimal 1 item.";
        Optional<Product> opt = productDAO.findById(productId);
        if (opt.isEmpty()) return "Produk tidak ditemukan.";
        if (opt.get().getStock() < qty)
            return "Stok tidak mencukupi (tersedia: " + opt.get().getStock() + ").";
        return null;
    }
}
