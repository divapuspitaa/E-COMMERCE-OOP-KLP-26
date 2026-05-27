package com.nusantarashop.controller;

import com.nusantarashop.dao.UserDAO;
import com.nusantarashop.model.Order;
import com.nusantarashop.model.User;
import com.nusantarashop.service.OrderService;
import com.nusantarashop.util.SceneManager;
import com.nusantarashop.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ProfileController {

    @FXML private Label lblAvatar, lblName, lblRole, lblUsername, lblBalance;
    @FXML private Label lblOrderCount, lblDoneCount;
    @FXML private Label lblMsg, lblPwdMsg;
    @FXML private TextField txtFullName, txtEmail, txtPhone, txtUsernameDisp;
    @FXML private TextArea txtAddress;
    @FXML private PasswordField txtNewPwd, txtConfirmPwd;

    private final SessionManager session = SessionManager.getInstance();
    private final UserDAO userDAO = new UserDAO();
    private final OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        populateForm();
        loadStats();
    }

    private void populateForm() {
        User user = session.getCurrentUser();
        if (user == null) return;

        lblName.setText(user.getDisplayName());
        lblRole.setText(user.getRole().getDisplayName());
        lblUsername.setText("@" + user.getUsername());
        lblBalance.setText(String.format("Rp %,.0f", user.getBalance()));
        lblAvatar.setText(user.isAdmin() ? "🛡" : user.isSeller() ? "🏪" : "👤");

        txtFullName.setText(user.getFullName() != null ? user.getFullName() : "");
        txtEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        txtPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        txtAddress.setText(user.getAddress() != null ? user.getAddress() : "");
        txtUsernameDisp.setText(user.getUsername());
    }

    private void loadStats() {
        List<Order> orders = orderService.getMyOrders();
        lblOrderCount.setText(String.valueOf(orders.size()));
        long done = orders.stream().filter(o -> o.getStatus() == Order.Status.SELESAI).count();
        lblDoneCount.setText(String.valueOf(done));
    }

    @FXML
    private void handleSave() {
        User user = session.getCurrentUser();
        if (user == null) return;

        user.setFullName(txtFullName.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setPhone(txtPhone.getText().trim());
        user.setAddress(txtAddress.getText().trim());

        if (userDAO.update(user)) {
            showMsg(lblMsg, "✓ Profil berhasil disimpan.", true);
            populateForm();
        } else {
            showMsg(lblMsg, "⚠ Gagal menyimpan profil.", false);
        }
    }

    @FXML
    private void handleChangePwd() {
        String newPwd = txtNewPwd.getText();
        String confirm = txtConfirmPwd.getText();

        if (newPwd.length() < 6) {
            showMsg(lblPwdMsg, "⚠ Password minimal 6 karakter.", false); return;
        }
        if (!newPwd.equals(confirm)) {
            showMsg(lblPwdMsg, "⚠ Konfirmasi password tidak cocok.", false); return;
        }

        User user = session.getCurrentUser();
        user.setPassword(newPwd);
        if (userDAO.update(user)) {
            showMsg(lblPwdMsg, "✓ Password berhasil diubah.", true);
            txtNewPwd.clear(); txtConfirmPwd.clear();
        } else {
            showMsg(lblPwdMsg, "⚠ Gagal mengubah password.", false);
        }
    }

    @FXML
    private void handleTopUp() {
        TextInputDialog dialog = new TextInputDialog("50000");
        dialog.setTitle("Top Up Saldo");
        dialog.setHeaderText("Masukkan jumlah top up (Rp):");
        dialog.setContentText("Jumlah:");
        dialog.showAndWait().ifPresent(val -> {
            try {
                double amount = Double.parseDouble(val.replace(",", "").replace(".", ""));
                if (amount <= 0) throw new NumberFormatException();
                User user = session.getCurrentUser();
                user.addBalance(amount);
                userDAO.update(user);
                lblBalance.setText(String.format("Rp %,.0f", user.getBalance()));
                showMsg(lblMsg, String.format("✓ Top up Rp %,.0f berhasil!", amount), true);
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Jumlah tidak valid.").showAndWait();
            }
        });
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                session.logout();
                SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
            }
        });
    }

    @FXML private void handleHome()   { SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME); }
    @FXML private void handleOrders() { SceneManager.getInstance().switchTo(SceneManager.SceneName.ORDER_HISTORY); }
    @FXML private void handleCart()   { SceneManager.getInstance().switchTo(SceneManager.SceneName.CART); }

    private void showMsg(Label lbl, String text, boolean success) {
        lbl.setText(text);
        lbl.setStyle("-fx-font-size:12px;-fx-text-fill:" + (success ? "#27AE60" : "#C0392B") + ";");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }
}
