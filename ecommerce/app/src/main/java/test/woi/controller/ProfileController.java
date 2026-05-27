package test.woi.controller;

import test.woi.dao.UserDAO;
import test.woi.model.Order;
import test.woi.model.User;
import test.woi.service.OrderService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProfileController {

    // ── Label info kiri ───────────────────────────────────────────
    @FXML private Label lblAvatar, lblName, lblRole, lblUsername, lblBalance;
    @FXML private Label lblOrderCount, lblDoneCount;

    // ── Section PEMBELI (field) ───────────────────────────────────
    @FXML private VBox sectionEditProfil;
    @FXML private VBox sectionUbahPassword;
    @FXML private TextField  txtFullName, txtEmail, txtPhone, txtUsernameDisp;
    @FXML private TextArea   txtAddress;
    @FXML private PasswordField txtNewPwd, txtConfirmPwd;
    @FXML private Label lblMsg, lblPwdMsg;

    // ── Section PENJUAL (field) ───────────────────────────────────
    @FXML private VBox        sectionSellerCred;
    @FXML private TextField   txtSellerUsername;
    @FXML private PasswordField txtSellerOldPwd;          // verifikasi untuk ubah username
    @FXML private PasswordField txtSellerOldPwdForPwd;    // verifikasi untuk ubah password
    @FXML private PasswordField txtSellerNewPwd, txtSellerConfirmPwd;
    @FXML private Label lblSellerCredMsg, lblSellerPwdMsg;

    private final SessionManager session    = SessionManager.getInstance();
    private final UserDAO        userDAO    = new UserDAO();
    private final OrderService   orderService = new OrderService();

    @FXML
    public void initialize() {
        populateForm();
        loadStats();
        applyRoleLayout();
    }

    // ── Layout per role ───────────────────────────────────────────

    private void applyRoleLayout() {
        boolean isSeller = session.isLoggedIn() && session.getCurrentUser().isSeller();
        setSection(sectionEditProfil,   !isSeller);
        setSection(sectionUbahPassword, !isSeller);
        setSection(sectionSellerCred,    isSeller);
    }

    private void setSection(VBox section, boolean visible) {
        if (section == null) return;
        section.setVisible(visible);
        section.setManaged(visible);
    }

    // ── Populate ──────────────────────────────────────────────────

    private void populateForm() {
        User user = session.getCurrentUser();
        if (user == null) return;

        lblName.setText(user.getDisplayName());
        lblRole.setText(user.getRole().getDisplayName());
        lblUsername.setText("@" + user.getUsername());
        lblBalance.setText(String.format("Rp %,.0f", user.getBalance()));
        lblAvatar.setText(user.isSeller() ? "🏪" : "👤");

        txtFullName.setText(nvl(user.getFullName()));
        txtEmail.setText(nvl(user.getEmail()));
        txtPhone.setText(nvl(user.getPhone()));
        txtAddress.setText(nvl(user.getAddress()));
        txtUsernameDisp.setText(user.getUsername());

        if (txtSellerUsername != null) txtSellerUsername.setText(user.getUsername());
    }

    private void loadStats() {
        List<Order> orders = orderService.getMyOrders();
        lblOrderCount.setText(String.valueOf(orders.size()));
        long done = orders.stream().filter(o -> o.getStatus() == Order.Status.SELESAI).count();
        lblDoneCount.setText(String.valueOf(done));
    }

    // ── Handler PEMBELI ───────────────────────────────────────────

    @FXML
    private void handleSave() {
        User user = session.getCurrentUser();
        if (user == null || user.isSeller()) return;

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
        User user = session.getCurrentUser();
        if (user == null || user.isSeller()) return;

        String newPwd   = txtNewPwd.getText();
        String confirm  = txtConfirmPwd.getText();

        if (newPwd.length() < 6)          { showMsg(lblPwdMsg, "⚠ Kata sandi minimal 6 karakter.", false); return; }
        if (!newPwd.equals(confirm))       { showMsg(lblPwdMsg, "⚠ Konfirmasi kata sandi tidak cocok.", false); return; }

        user.setPassword(newPwd);
        if (userDAO.updateUsernameAndPassword(user)) {
            showMsg(lblPwdMsg, "✓ Kata sandi berhasil diubah.", true);
            txtNewPwd.clear(); txtConfirmPwd.clear();
        } else {
            showMsg(lblPwdMsg, "⚠ Gagal mengubah kata sandi.", false);
        }
    }

    // ── Handler PENJUAL: Ubah Username ────────────────────────────

    @FXML
    private void handleSellerSaveUsername() {
        User user = session.getCurrentUser();
        if (user == null) return;

        String newUsername = txtSellerUsername.getText().trim();
        String oldPwd      = txtSellerOldPwd.getText();

        if (oldPwd.isBlank()) {
            showMsg(lblSellerCredMsg, "⚠ Masukkan kata sandi lama untuk verifikasi.", false); return;
        }
        if (!oldPwd.equals(user.getPassword())) {
            showMsg(lblSellerCredMsg, "⚠ Kata sandi lama tidak sesuai.", false); return;
        }
        if (newUsername.length() < 3) {
            showMsg(lblSellerCredMsg, "⚠ Username minimal 3 karakter.", false); return;
        }
        if (!newUsername.equals(user.getUsername()) && userDAO.usernameExists(newUsername)) {
            showMsg(lblSellerCredMsg, "⚠ Username sudah digunakan akun lain.", false); return;
        }

        user.setUsername(newUsername);
        if (userDAO.updateUsernameAndPassword(user)) {
            showMsg(lblSellerCredMsg, "✓ Username berhasil diperbarui.", true);
            txtSellerOldPwd.clear();
            populateForm();
        } else {
            showMsg(lblSellerCredMsg, "⚠ Gagal menyimpan username.", false);
        }
    }

    // ── Handler PENJUAL: Ubah Kata Sandi ─────────────────────────

    @FXML
    private void handleSellerSavePassword() {
        User user = session.getCurrentUser();
        if (user == null) return;

        String oldPwd   = txtSellerOldPwdForPwd.getText();
        String newPwd   = txtSellerNewPwd.getText();
        String confirm  = txtSellerConfirmPwd.getText();

        if (oldPwd.isBlank()) {
            showMsg(lblSellerPwdMsg, "⚠ Masukkan kata sandi lama untuk verifikasi.", false); return;
        }
        if (!oldPwd.equals(user.getPassword())) {
            showMsg(lblSellerPwdMsg, "⚠ Kata sandi lama tidak sesuai.", false); return;
        }
        if (newPwd.length() < 6) {
            showMsg(lblSellerPwdMsg, "⚠ Kata sandi baru minimal 6 karakter.", false); return;
        }
        if (!newPwd.equals(confirm)) {
            showMsg(lblSellerPwdMsg, "⚠ Konfirmasi kata sandi tidak cocok.", false); return;
        }

        user.setPassword(newPwd);
        if (userDAO.updateUsernameAndPassword(user)) {
            showMsg(lblSellerPwdMsg, "✓ Kata sandi berhasil diubah.", true);
            txtSellerOldPwdForPwd.clear();
            txtSellerNewPwd.clear();
            txtSellerConfirmPwd.clear();
        } else {
            showMsg(lblSellerPwdMsg, "⚠ Gagal mengubah kata sandi.", false);
        }
    }

    // ── Handler Umum ──────────────────────────────────────────────

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
        new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?")
            .showAndWait()
            .filter(b -> b == ButtonType.OK)
            .ifPresent(b -> {
                session.logout();
                SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
            });
    }

    @FXML private void handleHome()   { SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME); }
    @FXML private void handleOrders() { SceneManager.getInstance().switchTo(SceneManager.SceneName.ORDER_HISTORY); }
    @FXML private void handleCart()   { SceneManager.getInstance().switchTo(SceneManager.SceneName.CART); }

    // ── Util ──────────────────────────────────────────────────────

    private void showMsg(Label lbl, String text, boolean success) {
        if (lbl == null) return;
        lbl.setText(text);
        lbl.setStyle("-fx-font-size:12px;-fx-text-fill:" + (success ? "#27AE60" : "#C0392B") + ";");
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private String nvl(String s) { return s != null ? s : ""; }
}
