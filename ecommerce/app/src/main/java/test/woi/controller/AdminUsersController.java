package test.woi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import test.woi.dao.UserDAO;
import test.woi.model.User;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;

public class AdminUsersController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colName, colUsername, colEmail,
            colRole, colBalance, colActive, colActions;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Label lblCount;

    private final UserDAO userDAO = new UserDAO();
    private ObservableList<User> userData = FXCollections.observableArrayList();
    private List<User> allUsers;

    @FXML
    public void initialize() {
        cmbRole.getItems().add("Semua Role");
        for (User.Role r : User.Role.values()) cmbRole.getItems().add(r.getDisplayName());
        cmbRole.setValue("Semua Role");

        setupTable();
        loadUsers();
    }

    private void setupTable() {
        colName.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFullName() != null ? d.getValue().getFullName() : "-"));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty("@" + d.getValue().getUsername()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "-"));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole().getDisplayName()));
        colBalance.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("Rp %,.0f", d.getValue().getBalance())));
        colActive.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isActive() ? "✅ Aktif" : "❌ Nonaktif"));

        colActive.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.startsWith("✅")
                    ? "-fx-text-fill:#155724;-fx-font-weight:bold;"
                    : "-fx-text-fill:#721C24;-fx-font-weight:bold;");
            }
        });

        colRole.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "Administrator" -> "#C0392B";
                    case "Penjual"       -> "#2980B9";
                    default              -> "#27AE60";
                };
                setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;");
            }
        });

        // Kolom Aksi: tombol Toggle Aktif/Nonaktif + tombol Hapus
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnToggle = new Button();
            final Button btnHapus  = new Button("🗑 Hapus");
            final HBox   box       = new HBox(6, btnToggle, btnHapus);

            {
                btnToggle.setStyle("-fx-padding:4 8;-fx-font-size:11px;");
                btnHapus.setStyle("-fx-padding:4 8;-fx-font-size:11px;" +
                        "-fx-background-color:#E74C3C;-fx-text-fill:white;" +
                        "-fx-background-radius:6;-fx-cursor:hand;");
                btnHapus.setOnMouseEntered(e ->
                        btnHapus.setStyle("-fx-padding:4 8;-fx-font-size:11px;" +
                                "-fx-background-color:#C0392B;-fx-text-fill:white;" +
                                "-fx-background-radius:6;-fx-cursor:hand;"));
                btnHapus.setOnMouseExited(e ->
                        btnHapus.setStyle("-fx-padding:4 8;-fx-font-size:11px;" +
                                "-fx-background-color:#E74C3C;-fx-text-fill:white;" +
                                "-fx-background-radius:6;-fx-cursor:hand;"));
            }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }

                User u = getTableView().getItems().get(getIndex());
                String selfId = SessionManager.getInstance().getCurrentUser().getId();

                // Akun sendiri tidak bisa dihapus/diubah dari sini
                if (u.getId().equals(selfId)) {
                    setGraphic(new Label("(Anda)"));
                    return;
                }

                // Tombol toggle aktif/nonaktif
                btnToggle.setText(u.isActive() ? "🔴 Nonaktifkan" : "🟢 Aktifkan");
                btnToggle.getStyleClass().clear();
                btnToggle.getStyleClass().add(u.isActive() ? "btn-danger" : "btn-success");
                btnToggle.setOnAction(e -> {
                    u.setActive(!u.isActive());
                    userDAO.update(u);
                    tableUsers.refresh();
                });

                // Tombol hapus akun
                btnHapus.setOnAction(e -> handleDeleteUser(u));

                setGraphic(box);
                setPadding(new Insets(4));
            }
        });

        tableUsers.setItems(userData);
    }

    private void handleDeleteUser(User u) {
        String nama = u.getFullName() != null ? u.getFullName() : u.getUsername();

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Akun");
        confirm.setHeaderText("Hapus akun \"" + nama + "\" (@" + u.getUsername() + ")?");
        confirm.setContentText("Tindakan ini tidak dapat dibatalkan.\nSemua data akun akan dihapus permanen.");
        confirm.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        // Ubah teks tombol OK menjadi "Ya, Hapus"
        ((Button) confirm.getDialogPane().lookupButton(ButtonType.OK)).setText("Ya, Hapus");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = userDAO.deleteById(u.getId());
            if (success) {
                userData.remove(u);
                allUsers.remove(u);
                lblCount.setText(userData.size() + " pengguna");
                showInfo("Akun @" + u.getUsername() + " berhasil dihapus.");
            } else {
                showError("Gagal menghapus akun. Coba lagi.");
            }
        }
    }

    private void loadUsers() {
        allUsers = userDAO.findAll();
        userData.setAll(allUsers);
        lblCount.setText(allUsers.size() + " pengguna");
    }

    @FXML
    private void handleFilter() {
        String sel = cmbRole.getValue();
        if (sel == null || "Semua Role".equals(sel)) {
            userData.setAll(allUsers);
        } else {
            List<User> filtered = allUsers.stream()
                    .filter(u -> u.getRole().getDisplayName().equals(sel))
                    .collect(Collectors.toList());
            userData.setAll(filtered);
        }
        lblCount.setText(userData.size() + " pengguna");
    }

    @FXML private void handleRefresh()      { loadUsers(); }
    @FXML private void handleDashboard()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD); }
    @FXML private void handleProducts()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_PRODUCTS); }
    @FXML private void handleOrders()       { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_ORDERS); }
    @FXML private void handleRefreshUsers() { loadUsers(); }
    @FXML private void handleSellerProfile(){ SceneManager.getInstance().switchTo(SceneManager.SceneName.SELLER_PROFILE); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
    }

    // ── Helper dialogs ───────────────────────────────────────────────────────

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
