package proyek.p.dao;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import proyek.p.model.DataStore;
import proyek.p.model.Transaction;

/**
 * Data Access Object untuk operasi transaksi.
 */
public class TransactionDAO {

    private final DataStore store = DataStore.getInstance();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua transaksi milik seller tertentu, terbaru di atas. */
    public List<Transaction> findBySellerId(String sellerId) {
        return store.getTransactionsBySeller(sellerId).stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /** Mengambil semua transaksi milik customer tertentu, terbaru di atas. */
    public List<Transaction> findByCustomerId(String customerId) {
        return store.getTransactionsByCustomer(customerId).stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /** Menghitung total pendapatan seller dari semua transaksinya. */
    public double getTotalRevenueBySeller(String sellerId) {
        return store.getTotalRevenueBySeller(sellerId);
    }

    /** Menghitung total belanja customer dari semua transaksinya. */
    public double getTotalSpendingByCustomer(String customerId) {
        return store.getTransactionsByCustomer(customerId).stream()
                .mapToDouble(Transaction::getTotalPrice)
                .sum();
    }

    /** Menghitung total item terjual oleh seller. */
    public int getTotalItemsSoldBySeller(String sellerId) {
        return store.getTransactionsBySeller(sellerId).stream()
                .mapToInt(Transaction::getQuantity)
                .sum();
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /** Menyimpan transaksi baru ke store. */
    public void save(Transaction transaction) {
        store.addTransaction(transaction);
    }
}
