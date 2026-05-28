package test.woi.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import test.woi.model.User;
import test.woi.service.AuthService;
import test.woi.util.SceneManager;

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

        btnLogin.setDisable(true);
        btnLogin.setText("Memproses...");

        AuthService.LoginResult result = authService.login(username, password);

        if (result.success()) {
            User user = result.user();
            if (user.isAdmin()) {
                SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD);
            } else if (user.isSeller()) {
                SceneManager.getInstance().switchTo(SceneManager.SceneName.SELLER_DASHBOARD);
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
