package com.nusantarashop.controller;

import com.nusantarashop.service.AuthService;
import com.nusantarashop.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField txtFullName, txtUsername, txtEmail, txtPhone;
    @FXML private PasswordField txtPassword, txtConfirmPassword;
    @FXML private TextArea txtAddress;
    @FXML private Label lblError, lblSuccess;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegister() {
        hideMessages();
        AuthService.RegisterResult result = authService.register(
            txtUsername.getText(), txtPassword.getText(), txtConfirmPassword.getText(),
            txtFullName.getText(), txtEmail.getText(), txtPhone.getText(), txtAddress.getText()
        );

        if (result.success()) {
            showSuccess(result.message());
            clearForm();
            // Auto redirect setelah 1.5 detik
            new Thread(() -> {
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                javafx.application.Platform.runLater(() ->
                    SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN));
            }).start();
        } else {
            showError(result.message());
        }
    }

    @FXML
    private void handleGoLogin() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
    }

    private void clearForm() {
        txtFullName.clear(); txtUsername.clear(); txtEmail.clear();
        txtPhone.clear(); txtPassword.clear(); txtConfirmPassword.clear();
        txtAddress.clear();
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true); lblError.setManaged(true);
        lblSuccess.setVisible(false); lblSuccess.setManaged(false);
    }

    private void showSuccess(String msg) {
        lblSuccess.setText("✓ " + msg);
        lblSuccess.setVisible(true); lblSuccess.setManaged(true);
        lblError.setVisible(false); lblError.setManaged(false);
    }

    private void hideMessages() {
        lblError.setVisible(false); lblError.setManaged(false);
        lblSuccess.setVisible(false); lblSuccess.setManaged(false);
    }
}
