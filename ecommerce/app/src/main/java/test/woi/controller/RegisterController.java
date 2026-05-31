package test.woi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import test.woi.model.User;
import test.woi.service.AuthService;
import test.woi.util.SceneManager;

public class RegisterController {

    @FXML private TextField txtFullName, txtUsername, txtEmail, txtPhone;
    @FXML private PasswordField txtPassword, txtConfirmPassword, txtAdminPassword;
    @FXML private TextArea txtAddress;
    @FXML private Label lblError, lblSuccess;
    @FXML private RadioButton rbCustomer, rbSeller, rbAdmin;
    @FXML private ToggleGroup roleGroup;
    @FXML private VBox vboxAdminPassword; // panel password admin (show/hide)

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        if (roleGroup == null) roleGroup = new ToggleGroup();
        rbCustomer.setToggleGroup(roleGroup);
        rbSeller.setToggleGroup(roleGroup);
        rbAdmin.setToggleGroup(roleGroup);
        rbCustomer.setUserData("CUSTOMER");
        rbSeller.setUserData("SELLER");
        rbAdmin.setUserData("ADMIN");
        rbCustomer.setSelected(true);

        // Tampilkan/sembunyikan field password admin saat pilih admin
        roleGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (newT != null) {
                boolean isAdmin = "ADMIN".equals(newT.getUserData().toString());
                vboxAdminPassword.setVisible(isAdmin);
                vboxAdminPassword.setManaged(isAdmin);
            }
        });

        // Sembunyikan panel password admin di awal
        vboxAdminPassword.setVisible(false);
        vboxAdminPassword.setManaged(false);
    }

    @FXML
    private void handleRegister() {
        hideMessages();

        User.Role selectedRole = User.Role.CUSTOMER;
        if (roleGroup.getSelectedToggle() != null) {
            String roleStr = roleGroup.getSelectedToggle().getUserData().toString();
            selectedRole = User.Role.valueOf(roleStr);
        }

        String adminSecretPass = (selectedRole == User.Role.ADMIN)
                ? txtAdminPassword.getText()
                : null;

        AuthService.RegisterResult result = authService.register(
            txtUsername.getText(), txtPassword.getText(), txtConfirmPassword.getText(),
            txtFullName.getText(), txtEmail.getText(), txtPhone.getText(),
            txtAddress.getText(), selectedRole, adminSecretPass
        );

        if (result.success()) {
            showSuccess(result.message());
            clearForm();
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
        txtAddress.clear(); txtAdminPassword.clear();
        rbCustomer.setSelected(true);
        vboxAdminPassword.setVisible(false);
        vboxAdminPassword.setManaged(false);
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
