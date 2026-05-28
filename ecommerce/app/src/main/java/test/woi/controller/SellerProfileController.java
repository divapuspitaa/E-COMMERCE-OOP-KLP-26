package test.woi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import test.woi.dao.UserDAO;
import test.woi.model.User;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;

/**
 * SellerProfileController - Mengelola halaman ubah profil khusus penjual.
 * Memungkinkan penjual mengubah username dan password akun mereka.
 */
public class SellerProfileController {

    @FXML private Label lblSellerName;
    @FXML private Label lblCurrentUsername;

    // Username fields
    @FXML private TextField txtNewUsername;
    @FXML private Label lblUsernameMsg;

    // Password fields
    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblPasswordMsg;

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        lblSellerName.setText(currentUser.getDisplayName());
        lblCurrentUsername.setText("@" + currentUser.getUsername());
    }

    // ── Simpan Username ──────────────────────────────────────────────────────

    @FXML
    private void handleSaveUsername() {
        String newUsername = txtNewUsername.getText().trim();

        // Validasi input
        if (newUsername.isEmpty()) {
            showMsg(lblUsernameMsg, "⚠️ Username baru tidak boleh kosong.", "warn");
            return;
        }
        if (newUsername.equals(currentUser.getUsername())) {
            showMsg(lblUsernameMsg, "⚠️ Username baru sama dengan username saat ini.", "warn");
            return;
        }
        if (newUsername.length() < 4) {
            showMsg(lblUsernameMsg, "⚠️ Username minimal 4 karakter.", "warn");
            return;
        }
        if (!newUsername.matches("[a-zA-Z0-9._]+")) {
            showMsg(lblUsernameMsg, "⚠️ Username hanya boleh huruf, angka, titik, atau underscore.", "warn");
            return;
        }

        // Cek apakah username sudah dipakai user lain
        if (userDAO.usernameExists(newUsername)) {
            showMsg(lblUsernameMsg, "❌ Username sudah digunakan oleh akun lain.", "error");
            return;
        }

        // Konfirmasi
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ubah username menjadi \"" + newUsername + "\"?\nAnda akan diminta login ulang.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Konfirmasi Ubah Username");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                String oldUsername = currentUser.getUsername();
                currentUser.setUsername(newUsername);
                boolean success = userDAO.updateUsernameAndPassword(currentUser);
                if (success) {
                    showMsg(lblUsernameMsg, "✅ Username berhasil diubah. Silakan login ulang.", "success");
                    txtNewUsername.clear();
                    // Logout dan kembali ke halaman login
                    SessionManager.getInstance().logout();
                    SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
                } else {
                    // Rollback jika gagal
                    currentUser.setUsername(oldUsername);
                    showMsg(lblUsernameMsg, "❌ Gagal menyimpan username. Coba lagi.", "error");
                }
            }
        });
    }

    // ── Simpan Password ──────────────────────────────────────────────────────

    @FXML
    private void handleSavePassword() {
        String currentPw  = txtCurrentPassword.getText();
        String newPw      = txtNewPassword.getText();
        String confirmPw  = txtConfirmPassword.getText();

        // Validasi input
        if (currentPw.isEmpty() || newPw.isEmpty() || confirmPw.isEmpty()) {
            showMsg(lblPasswordMsg, "⚠️ Semua field password harus diisi.", "warn");
            return;
        }

        // Cek password saat ini
        if (!currentPw.equals(currentUser.getPassword())) {
            showMsg(lblPasswordMsg, "❌ Password saat ini tidak sesuai.", "error");
            return;
        }

        if (newPw.equals(currentPw)) {
            showMsg(lblPasswordMsg, "⚠️ Password baru sama dengan password saat ini.", "warn");
            return;
        }
        if (newPw.length() < 6) {
            showMsg(lblPasswordMsg, "⚠️ Password baru minimal 6 karakter.", "warn");
            return;
        }
        if (!newPw.equals(confirmPw)) {
            showMsg(lblPasswordMsg, "❌ Konfirmasi password tidak cocok.", "error");
            return;
        }

        // Konfirmasi
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Ubah password akun Anda?\nAnda akan diminta login ulang.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setTitle("Konfirmasi Ubah Password");
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                String oldPw = currentUser.getPassword();
                currentUser.setPassword(newPw);
                boolean success = userDAO.updateUsernameAndPassword(currentUser);
                if (success) {
                    txtCurrentPassword.clear();
                    txtNewPassword.clear();
                    txtConfirmPassword.clear();
                    // Logout dan kembali ke halaman login
                    SessionManager.getInstance().logout();
                    SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
                } else {
                    currentUser.setPassword(oldPw);
                    showMsg(lblPasswordMsg, "❌ Gagal menyimpan password. Coba lagi.", "error");
                }
            }
        });
    }

    // ── Navigasi Sidebar ────────────────────────────────────────────────────

    @FXML private void handleDashboard()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.SELLER_DASHBOARD); }
    @FXML private void handleProducts()      { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_PRODUCTS); }
    @FXML private void handleOrders()        { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_ORDERS); }
    @FXML private void handleUsers()         { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_USERS); }
    @FXML private void handleSellerProfile() { /* sudah di halaman ini */ }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                SessionManager.getInstance().logout();
                SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
            }
        });
    }

    // ── Helper ──────────────────────────────────────────────────────────────

    private void showMsg(Label label, String text, String type) {
        label.setText(text);
        label.setStyle(switch (type) {
            case "success" -> "-fx-text-fill:#155724;-fx-font-weight:bold;-fx-font-size:12px;";
            case "error"   -> "-fx-text-fill:#721C24;-fx-font-weight:bold;-fx-font-size:12px;";
            default        -> "-fx-text-fill:#856404;-fx-font-size:12px;";  // warn
        });
    }
}
