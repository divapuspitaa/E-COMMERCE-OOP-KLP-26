package test.woi.controller;

import test.woi.model.User;
import test.woi.service.AuthService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

/**
 * LoginController - mengontrol halaman login.
 */
public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnLogin;
    @FXML private Label         lblError;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        txtUsername.textProperty().addListener((obs, o, n) -> hideError());
        txtPassword.textProperty().addListener((obs, o, n) -> hideError());
        Platform.runLater(() -> txtUsername.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validasi cepat di UI thread
        if (username == null || username.isBlank()) {
            showError("Username tidak boleh kosong.");
            return;
        }
        if (password == null || password.isBlank()) {
            showError("Password tidak boleh kosong.");
            return;
        }

        // Nonaktifkan button & tampilkan loading
        btnLogin.setDisable(true);
        btnLogin.setText("Memproses...");
        hideError();

        // Jalankan autentikasi di background thread agar UI tidak freeze
        Task<AuthService.LoginResult> task = new Task<>() {
            @Override
            protected AuthService.LoginResult call() {
                return authService.login(username.trim(), password.trim());
            }
        };

        task.setOnSucceeded(evt -> {
            // Kembali ke JavaFX thread
            AuthService.LoginResult result = task.getValue();
            if (result.success()) {
                try {
                    User user = result.user();
                    if (user.isAdmin()) {
                        SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD);
                    } else if (user.isSeller()) {
                        SceneManager.getInstance().switchTo(SceneManager.SceneName.SELLER_DASHBOARD);
                    } else {
                        SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME);
                    }
                } catch (Exception ex) {
                    System.err.println("[Login] Gagal pindah scene: " + ex.getMessage());
                    ex.printStackTrace();
                    resetButton();
                    showError("Gagal membuka halaman. Coba lagi. (" + ex.getMessage() + ")");
                }
            } else {
                resetButton();
                showError(result.message());
                txtPassword.clear();
                txtPassword.requestFocus();
            }
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            System.err.println("[Login] Task error: " + (ex != null ? ex.getMessage() : "unknown"));
            if (ex != null) ex.printStackTrace();
            resetButton();
            showError("Terjadi kesalahan sistem. Coba lagi.");
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void resetButton() {
        btnLogin.setDisable(false);
        btnLogin.setText("Masuk");
    }

    @FXML
    private void handleGoRegister() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.REGISTER);
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void hideError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}
