package test.woi.controller;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
                    case "Administrator" -> "#8B0000";
                    case "Penjual"       -> "#2980B9";
                    default              -> "#27AE60";
                };
                setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;");
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnToggle = new Button();
            { btnToggle.setStyle("-fx-padding:4 10;-fx-font-size:11px;"); }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());
                String selfId = SessionManager.getInstance().getCurrentUser().getId();
                if (u.getId().equals(selfId)) { setGraphic(new Label("(Anda)")); return; }

                // Admin tidak bisa di-toggle di sini (hanya di halaman Kelola Akun)
                if (u.isAdmin()) { setGraphic(new Label("—")); return; }

                btnToggle.setText(u.isActive() ? "🔴 Nonaktifkan" : "🟢 Aktifkan");
                btnToggle.getStyleClass().clear();
                btnToggle.getStyleClass().add(u.isActive() ? "btn-danger" : "btn-success");
                btnToggle.setOnAction(e -> {
                    u.setActive(!u.isActive());
                    userDAO.update(u);
                    tableUsers.refresh();
                });
                setGraphic(btnToggle);
                setPadding(new Insets(4));
            }
        });

        tableUsers.setItems(userData);
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
    @FXML private void handleManageUsers()  { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_MANAGE_USERS); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
    }
}
