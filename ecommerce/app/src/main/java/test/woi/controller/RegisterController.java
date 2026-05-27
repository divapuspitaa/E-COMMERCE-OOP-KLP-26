package test.woi.controller;

import test.woi.model.User;
import test.woi.service.AuthService;
import test.woi.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField txtFullName, txtUsername, txtEmail, txtPhone;
    @FXML private PasswordField txtPassword, txtConfirmPassword;
    @FXML private TextArea txtAddress;
    @FXML private Label lblError, lblSuccess;
    @FXML private RadioButton rbCustomer, rbSeller;
    @FXML private ToggleGroup roleGroup;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Pastikan ToggleGroup ter-assign manual (sama seperti fix checkout)
        if (roleGroup == null) roleGroup = new ToggleGroup();
        rbCustomer.setToggleGroup(roleGroup);
        rbSeller.setToggleGroup(roleGroup);
        rbCustomer.setUserData("CUSTOMER");
        rbSeller.setUserData("SELLER");
        rbCustomer.setSelected(true); // default: pembeli
    }

    @FXML
    private void handleRegister() {
        hideMessages();

        // Ambil role yang dipilih
        User.Role selectedRole = User.Role.CUSTOMER;
        if (roleGroup.getSelectedToggle() != null) {
            String roleStr = roleGroup.getSelectedToggle().getUserData().toString();
            selectedRole = User.Role.valueOf(roleStr);
        }

        AuthService.RegisterResult result = authService.register(
            txtUsername.getText(), txtPassword.getText(), txtConfirmPassword.getText(),
            txtFullName.getText(), txtEmail.getText(), txtPhone.getText(),
            txtAddress.getText(), selectedRole
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
        rbCustomer.setSelected(true);
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
