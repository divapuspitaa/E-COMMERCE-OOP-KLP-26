package com.nusantarashop.controller;

import com.nusantarashop.model.User;
import com.nusantarashop.service.AuthService;
import com.nusantarashop.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

/**
 * LoginController - mengontrol halaman login.
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Label lblError;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Enter key di password field langsung login
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        // Clear error on typing
        txtUsername.textProperty().addListener((obs, o, n) -> hideError());
        txtPassword.textProperty().addListener((obs, o, n) -> hideError());

        Platform.runLater(() -> txtUsername.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        btnLogin.setDisable(true);
        btnLogin.setText("Memproses...");

        AuthService.LoginResult result = authService.login(username, password);

        if (result.success()) {
            User user = result.user();
            if (user.isAdmin()) {
                SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD);
            } else {
                SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME);
            }
        } else {
            showError(result.message());
            btnLogin.setDisable(false);
            btnLogin.setText("Masuk");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
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
