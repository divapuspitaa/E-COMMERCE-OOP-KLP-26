package test.woi.service;

import java.util.Optional;

import test.woi.dao.UserDAO;
import test.woi.model.User;
import test.woi.util.SessionManager;

/**
 * AuthService - logika bisnis untuk autentikasi.
 */
public class AuthService {

    private static final String ADMIN_PASSWORD = "OOP26";

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager session = SessionManager.getInstance();

    public record LoginResult(boolean success, String message, User user) {}
    public record RegisterResult(boolean success, String message) {}

    public LoginResult login(String username, String password) {
        if (username == null || username.isBlank())
            return new LoginResult(false, "Username tidak boleh kosong.", null);
        if (password == null || password.isBlank())
            return new LoginResult(false, "Password tidak boleh kosong.", null);

        Optional<User> found = userDAO.authenticate(username.trim(), password.trim());
        if (found.isEmpty())
            return new LoginResult(false, "Username atau password salah.", null);

        User user = found.get();
        session.login(user);
        return new LoginResult(true, "Login berhasil! Selamat datang, " + user.getDisplayName(), user);
    }

    /**
     * Registrasi akun.
     * @param adminSecretPassword  Hanya wajib diisi jika role=ADMIN; kosong/null untuk role lain.
     */
    public RegisterResult register(String username, String password, String confirmPassword,
                                   String fullName, String email, String phone, String address,
                                   User.Role role, String adminSecretPassword) {

        if (username == null || username.isBlank())
            return new RegisterResult(false, "Username tidak boleh kosong.");
        if (username.length() < 4)
            return new RegisterResult(false, "Username minimal 4 karakter.");
        if (password == null || password.length() < 6)
            return new RegisterResult(false, "Password minimal 6 karakter.");
        if (!password.equals(confirmPassword))
            return new RegisterResult(false, "Konfirmasi password tidak cocok.");
        if (email == null || !email.contains("@"))
            return new RegisterResult(false, "Format email tidak valid.");
        if (userDAO.usernameExists(username.trim()))
            return new RegisterResult(false, "Username sudah digunakan.");

        // Validasi password admin
        if (role == User.Role.ADMIN) {
            if (adminSecretPassword == null || !adminSecretPassword.equals(ADMIN_PASSWORD)) {
                return new RegisterResult(false, "Password admin tidak valid. Akses ditolak.");
            }
        }

        // Pastikan role valid
        User.Role assignedRole = switch (role) {
            case ADMIN   -> User.Role.ADMIN;
            case SELLER  -> User.Role.SELLER;
            default      -> User.Role.CUSTOMER;
        };

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setPassword(password);
        newUser.setFullName(fullName != null ? fullName.trim() : username);
        newUser.setEmail(email.trim());
        newUser.setPhone(phone != null ? phone.trim() : "");
        newUser.setAddress(address != null ? address.trim() : "");
        newUser.setRole(assignedRole);
        newUser.setActive(true);
        newUser.setBalance(0);

        // Tentukan admin_seq jika role ADMIN
        if (assignedRole == User.Role.ADMIN) {
            newUser.setAdminSeq(userDAO.getNextAdminSeq());
        } else {
            newUser.setAdminSeq(0);
        }

        boolean saved = userDAO.save(newUser);
        if (saved) return new RegisterResult(true, "Registrasi berhasil! Silakan login.");
        return new RegisterResult(false, "Registrasi gagal. Coba lagi.");
    }

    /** Overload tanpa adminSecretPassword untuk kompatibilitas (role non-admin) */
    public RegisterResult register(String username, String password, String confirmPassword,
                                   String fullName, String email, String phone, String address,
                                   User.Role role) {
        return register(username, password, confirmPassword, fullName, email, phone, address, role, null);
    }

    public void logout() {
        session.logout();
    }
}
