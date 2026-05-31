package test.woi.controller;

import java.util.List;
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

/**
 * AdminManageUsersController
 * Fitur eksklusif Admin:
 *  - Melihat semua akun (Penjual, Pembeli, dan Admin lain)
 *  - Menonaktifkan / mengaktifkan kembali akun Penjual & Pembeli
 *  - Menghapus akun Penjual & Pembeli
 *  - Menghapus akun Admin lain yang memiliki admin_seq LEBIH BESAR dari miliknya
 *    (admin dengan ID lebih muda = admin_seq lebih besar)
 */

public class AdminManageUsersController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colSeq, colName, colUsername,
            colEmail, colRole, colStatus, colActions;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private Label lblCount, lblMyInfo;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private List<User> allUsers;
    private User me;

    @FXML
    public void initialize() {
        me = SessionManager.getInstance().getCurrentUser();

        lblMyInfo.setText("Admin #" + me.getAdminSeq() + "  |  " + me.getDisplayName());

        cmbFilter.getItems().addAll("Semua", "Administrator", "Penjual", "Pembeli");
        cmbFilter.setValue("Semua");
        cmbFilter.setOnAction(e -> applyFilter());

        setupTable();
        loadUsers();
    }

    // ─── Table Setup ──────────────────────────────────────────────────────────

    private void setupTable() {
        colSeq.setCellValueFactory(d -> {
            User u = d.getValue();
            return new SimpleStringProperty(
                u.isAdmin() ? "#" + u.getAdminSeq() : "-"
            );
        });
        colName.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFullName() != null ? d.getValue().getFullName() : "-"));
        colUsername.setCellValueFactory(d -> new SimpleStringProperty("@" + d.getValue().getUsername()));
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEmail() != null ? d.getValue().getEmail() : "-"));
        colRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole().getDisplayName()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isActive() ? "✅ Aktif" : "❌ Nonaktif"));

        // Warna role
        colRole.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                String color = switch (item) {
                    case "Administrator" -> "#8B0000";
                    case "Penjual"       -> "#2980B9";
                    default              -> "#27AE60";
                };
                setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;");
            }
        });

        // Warna status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.startsWith("✅")
                    ? "-fx-text-fill:#155724;-fx-font-weight:bold;"
                    : "-fx-text-fill:#721C24;-fx-font-weight:bold;");
            }
        });

        // Kolom aksi
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnToggle = new Button();
            final Button btnDelete = new Button("🗑 Hapus");
            final HBox box = new HBox(6, btnToggle, btnDelete);

            {
                btnToggle.setStyle("-fx-padding:3 8;-fx-font-size:11px;");
                btnDelete.setStyle("-fx-padding:3 8;-fx-font-size:11px;-fx-background-color:#C0392B;-fx-text-fill:white;-fx-background-radius:5;");
            }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }

                User u = getTableView().getItems().get(getIndex());

                // Diri sendiri — tidak bisa diubah
                if (u.getId().equals(me.getId())) {
                    setGraphic(new Label("(Anda)"));
                    return;
                }

                // Cek apakah target adalah Admin
                boolean targetIsAdmin = u.isAdmin();

                if (targetIsAdmin) {
                    // Hanya bisa hapus admin yang admin_seq-nya LEBIH BESAR (lebih muda)
                    boolean canDelete = u.getAdminSeq() > me.getAdminSeq();
                    if (canDelete) {
                        btnDelete.setDisable(false);
                        btnDelete.setOpacity(1.0);
                        btnDelete.setOnAction(e -> confirmAndDeleteAdmin(u));
                    } else {
                        btnDelete.setDisable(true);
                        btnDelete.setOpacity(0.4);
                        btnDelete.setOnAction(null);
                    }
                    // Admin tidak bisa di-toggle aktif/nonaktif oleh admin lain
                    HBox onlyDelete = new HBox(btnDelete);
                    setGraphic(onlyDelete);
                    setPadding(new Insets(4));
                    return;
                }

                // Target adalah Seller / Customer — bisa toggle & hapus
                btnToggle.setText(u.isActive() ? "🔴 Nonaktifkan" : "🟢 Aktifkan");
                btnToggle.setStyle("-fx-padding:3 8;-fx-font-size:11px;"
                    + (u.isActive()
                    ? "-fx-background-color:#E74C3C;-fx-text-fill:white;-fx-background-radius:5;"
                    : "-fx-background-color:#27AE60;-fx-text-fill:white;-fx-background-radius:5;"));

                btnToggle.setOnAction(e -> {
                    u.setActive(!u.isActive());
                    userDAO.update(u);
                    tableUsers.refresh();
                });

                btnDelete.setDisable(false);
                btnDelete.setOpacity(1.0);
                btnDelete.setOnAction(e -> confirmAndDelete(u));

                setGraphic(box);
                setPadding(new Insets(4));
            }
        });

        tableUsers.setItems(userData);
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadUsers() {
        allUsers = userDAO.findAll();
        // Urutkan: Admin dulu (seq terkecil), lalu Seller, lalu Customer
        allUsers.sort((a, b) -> {
            int roleOrd = Integer.compare(roleOrder(a), roleOrder(b));
            if (roleOrd != 0) return roleOrd;
            if (a.isAdmin() && b.isAdmin()) return Integer.compare(a.getAdminSeq(), b.getAdminSeq());
            return 0;
        });
        applyFilter();
    }

    private int roleOrder(User u) {
        return switch (u.getRole()) {
            case ADMIN    -> 0;
            case SELLER   -> 1;
            case CUSTOMER -> 2;
        };
    }

    private void applyFilter() {
        String sel = cmbFilter.getValue();
        List<User> filtered = switch (sel == null ? "Semua" : sel) {
            case "Administrator" -> allUsers.stream().filter(User::isAdmin).collect(Collectors.toList());
            case "Penjual"       -> allUsers.stream().filter(User::isSeller).collect(Collectors.toList());
            case "Pembeli"       -> allUsers.stream().filter(User::isCustomer).collect(Collectors.toList());
            default              -> allUsers;
        };
        userData.setAll(filtered);
        lblCount.setText(filtered.size() + " akun ditampilkan");
    }

    // ─── Konfirmasi & Aksi ────────────────────────────────────────────────────

    private void confirmAndDelete(User target) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Akun");
        confirm.setHeaderText("Hapus akun " + target.getRole().getDisplayName() + "?");
        confirm.setContentText("Akun @" + target.getUsername() + " akan dihapus secara permanen.\nTindakan ini tidak bisa dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean deleted = userDAO.delete(target.getId());
                if (deleted) {
                    showInfo("Akun @" + target.getUsername() + " berhasil dihapus.");
                    loadUsers();
                } else {
                    showError("Gagal menghapus akun.");
                }
            }
        });
    }

    private void confirmAndDeleteAdmin(User target) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Hapus Akun Admin");
        confirm.setHeaderText("Hapus Admin #" + target.getAdminSeq() + "?");
        confirm.setContentText(
            "Anda akan menghapus akun Admin @" + target.getUsername() + ".\n" +
            "Admin #" + target.getAdminSeq() + " dibuat lebih muda dari Anda (Admin #" + me.getAdminSeq() + ").\n" +
            "Tindakan ini tidak bisa dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean deleted = userDAO.delete(target.getId());
                if (deleted) {
                    showInfo("Admin #" + target.getAdminSeq() + " (@" + target.getUsername() + ") berhasil dihapus.");
                    loadUsers();
                } else {
                    showError("Gagal menghapus akun admin.");
                }
            }
        });
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    @FXML private void handleRefresh()   { loadUsers(); }
    @FXML private void handleDashboard() { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD); }
    @FXML private void handleProducts()  { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_PRODUCTS); }
    @FXML private void handleOrders()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_ORDERS); }
    @FXML private void handleUsers()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_USERS); }

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
}
