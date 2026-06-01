package proyek.p.controller;

import java.util.Optional;

import proyek.p.dao.UserDAO;
import proyek.p.model.Admin;
import proyek.p.model.Customer;
import proyek.p.model.Seller;
import proyek.p.model.User;

/**
 * Controller yang menangani logika autentikasi:
 * login, registrasi, dan validasi input.
 */
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    // ── Login ─────────────────────────────────────────────────────────────────────

    /**
     * Melakukan login dengan username dan password.
     * @return Optional berisi User jika berhasil, kosong jika gagal.
     */
    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank()) return Optional.empty();
        if (password == null || password.isBlank()) return Optional.empty();
        return userDAO.login(username.trim(), password);
    }

    // ── Registrasi ───────────────────────────────────────────────────────────────

    /**
     * Mendaftarkan Customer baru.
     * @return pesan error, atau null jika berhasil.
     */
    public String registerCustomer(String username, String password,
                                   String confirmPassword, String email) {
        String err = validateRegisterInput(username, password, confirmPassword, email);
        if (err != null) return err;

        String id = "c-" + userDAO.generateId();
        userDAO.save(new Customer(id, username.trim(), password, email.trim()));
        return null;
    }

    /**
     * Mendaftarkan Seller baru.
     * @return pesan error, atau null jika berhasil.
     */
    public String registerSeller(String username, String password,
                                 String confirmPassword, String email) {
        String err = validateRegisterInput(username, password, confirmPassword, email);
        if (err != null) return err;

        String id = "s-" + userDAO.generateId();
        userDAO.save(new Seller(id, username.trim(), password, email.trim()));
        return null;
    }

    /**
     * Mendaftarkan Admin baru. Memerlukan kode rahasia.
     * @return pesan error, atau null jika berhasil.
     */
    public String registerAdmin(String username, String password,
                                String confirmPassword, String email, String secret) {
        if (!userDAO.validateAdminSecret(secret)) {
            return "Kode rahasia admin tidak valid.";
        }
        String err = validateRegisterInput(username, password, confirmPassword, email);
        if (err != null) return err;

        String id = userDAO.generateAdminId();
        userDAO.save(new Admin(id, username.trim(), password, email.trim()));
        return null;
    }

    // ── Validasi ─────────────────────────────────────────────────────────────────

    /**
     * Validasi bersama untuk semua tipe registrasi.
     * @return pesan error pertama ditemukan, atau null jika semua valid.
     */
    private String validateRegisterInput(String username, String password,
                                         String confirmPassword, String email) {
        if (username == null || username.isBlank())
            return "Username tidak boleh kosong.";
        if (username.trim().length() < 4)
            return "Username minimal 4 karakter.";
        if (userDAO.isUsernameTaken(username.trim()))
            return "Username sudah digunakan.";
        if (password == null || password.length() < 6)
            return "Password minimal 6 karakter.";
        if (!password.equals(confirmPassword))
            return "Konfirmasi password tidak cocok.";
        if (email == null || !email.contains("@"))
            return "Format email tidak valid.";
        return null;
    }

    /**
     * Validasi username saja (untuk cek real-time di form).
     * @return pesan error, atau null jika valid.
     */
    public String validateUsername(String username) {
        if (username == null || username.isBlank()) return "Username tidak boleh kosong.";
        if (username.trim().length() < 4) return "Username minimal 4 karakter.";
        if (userDAO.isUsernameTaken(username.trim())) return "Username sudah digunakan.";
        return null;
    }

    /**
     * Validasi password saja (untuk cek real-time di form).
     * @return pesan error, atau null jika valid.
     */
    public String validatePassword(String password, String confirm) {
        if (password == null || password.length() < 6) return "Password minimal 6 karakter.";
        if (!password.equals(confirm)) return "Konfirmasi password tidak cocok.";
        return null;
    }
}
