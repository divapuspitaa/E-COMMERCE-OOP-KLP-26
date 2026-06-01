package proyek.p.controller;

import proyek.p.dao.UserDAO;
import proyek.p.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Controller yang menangani manajemen pengguna oleh Admin:
 * melihat, menonaktifkan, menghapus, dan memperbarui profil.
 */
public class UserController {

    private final UserDAO userDAO = new UserDAO();

    // ── Read ─────────────────────────────────────────────────────────────────────

    /** Mengambil semua pengguna dari semua role. */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /** Mengambil semua pengguna dengan role SELLER. */
    public List<User> getAllSellers() {
        return userDAO.findAllSellers();
    }

    /** Mengambil semua pengguna dengan role CUSTOMER. */
    public List<User> getAllCustomers() {
        return userDAO.findAllCustomers();
    }

    /** Mengambil semua pengguna dengan role ADMIN. */
    public List<User> getAllAdmins() {
        return userDAO.findAllAdmins();
    }

    /** Mencari pengguna berdasarkan ID. */
    public Optional<User> getUserById(String id) {
        return userDAO.findById(id);
    }

    // ── Update Status ────────────────────────────────────────────────────────────

    /** Mengaktifkan akun pengguna. */
    public void activateUser(String userId) {
        userDAO.setActive(userId, true);
    }

    /** Menonaktifkan (suspend) akun pengguna. */
    public void deactivateUser(String userId) {
        userDAO.setActive(userId, false);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    /**
     * Menghapus pengguna. Untuk admin, hanya bisa dilakukan
     * oleh admin dengan ID numerik lebih kecil.
     * @return pesan error, atau null jika berhasil.
     */
    public String deleteUser(String requesterId, String targetId) {
        Optional<User> target = userDAO.findById(targetId);
        if (target.isEmpty()) return "Pengguna tidak ditemukan.";

        // Jika target adalah admin, cek otorisasi
        if (target.get().getRole() == User.Role.ADMIN) {
            if (!userDAO.canAdminDelete(requesterId, targetId)) {
                return "Anda tidak memiliki izin untuk menghapus admin ini.";
            }
        }

        userDAO.delete(targetId);
        return null;
    }

    // ── Update Profil ─────────────────────────────────────────────────────────────

    /**
     * Memperbarui username pengguna (dipakai dari halaman profil).
     * @return pesan error, atau null jika berhasil.
     */
    public String updateUsername(String userId, String currentUsername, String newUsername) {
        if (newUsername == null || newUsername.isBlank())
            return "Username baru tidak boleh kosong.";
        if (newUsername.trim().length() < 4)
            return "Username minimal 4 karakter.";
        if (newUsername.trim().equals(currentUsername))
            return "Username baru sama dengan yang lama.";
        boolean ok = userDAO.updateUsername(userId, newUsername.trim());
        if (!ok) return "Username sudah digunakan pengguna lain.";
        return null;
    }

    /**
     * Memperbarui password pengguna.
     * @return pesan error, atau null jika berhasil.
     */
    public String updatePassword(String userId, String currentPassword,
                                 String newPassword, String confirmPassword) {
        Optional<User> user = userDAO.findById(userId);
        if (user.isEmpty()) return "Pengguna tidak ditemukan.";
        if (!user.get().getPassword().equals(currentPassword))
            return "Password lama tidak sesuai.";
        if (newPassword == null || newPassword.length() < 6)
            return "Password baru minimal 6 karakter.";
        if (!newPassword.equals(confirmPassword))
            return "Konfirmasi password tidak cocok.";
        userDAO.updatePassword(userId, newPassword);
        return null;
    }
}
