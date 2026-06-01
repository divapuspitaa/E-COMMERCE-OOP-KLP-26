package proyek.p.dao;

import java.util.List;
import java.util.Optional;

import proyek.p.model.DataStore;
import proyek.p.model.User;

/**
 * Data Access Object untuk operasi CRUD pengguna.
 * Menjadi jembatan antara controller dan DataStore (in-memory storage).
 */
public class UserDAO {

    private final DataStore store = DataStore.getInstance();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua pengguna (admin, seller, customer). */
    public List<User> findAll() {
        return store.getAllUsers();
    }

    /** Mencari pengguna berdasarkan ID. */
    public Optional<User> findById(String id) {
        return store.findUserById(id);
    }

    /** Mengambil semua pengguna dengan role ADMIN. */
    public List<User> findAllAdmins() {
        return store.getAdmins();
    }

    /** Mengambil semua pengguna dengan role SELLER. */
    public List<User> findAllSellers() {
        return store.getSellers();
    }

    /** Mengambil semua pengguna dengan role CUSTOMER. */
    public List<User> findAllCustomers() {
        return store.getCustomers();
    }

    /** Mengecek apakah username sudah dipakai pengguna lain. */
    public boolean isUsernameTaken(String username) {
        return store.usernameExists(username);
    }

    /** Login: cari pengguna cocok username + password + aktif. */
    public Optional<User> login(String username, String password) {
        return store.login(username, password);
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    /** Mendaftarkan pengguna baru ke store. */
    public void save(User user) {
        store.register(user);
    }

    // ── Update ───────────────────────────────────────────────────────────────────

    /**
     * Memperbarui username pengguna.
     * @return false jika username sudah dipakai pengguna lain.
     */
    public boolean updateUsername(String userId, String newUsername) {
        return store.updateUsername(userId, newUsername);
    }

    /** Memperbarui password pengguna. */
    public void updatePassword(String userId, String newPassword) {
        store.updatePassword(userId, newPassword);
    }

    /** Mengaktifkan atau menonaktifkan akun pengguna. */
    public void setActive(String userId, boolean active) {
        store.setUserActive(userId, active);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    /** Menghapus pengguna beserta produk miliknya (jika seller). */
    public void delete(String userId) {
        store.deleteUser(userId);
    }

    // ── Utility ──────────────────────────────────────────────────────────────────

    /** Generate ID unik untuk pengguna baru. */
    public String generateId() {
        return store.generateId();
    }

    /** Generate ID numerik untuk admin baru. */
    public String generateAdminId() {
        return store.generateAdminId();
    }

    /** Validasi kode rahasia admin. */
    public boolean validateAdminSecret(String secret) {
        return store.validateAdminSecret(secret);
    }

    /**
     * Cek apakah admin peminta boleh menghapus admin target
     * (berdasarkan urutan ID numerik).
     */
    public boolean canAdminDelete(String requesterId, String targetId) {
        return store.canAdminDelete(requesterId, targetId);
    }
}
