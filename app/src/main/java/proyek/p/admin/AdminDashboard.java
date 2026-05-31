package proyek.p.admin;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.Admin;
import proyek.p.model.DataStore;
import proyek.p.model.Product;
import proyek.p.model.User;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

public class AdminDashboard {
    private final Stage stage;
    private final Admin admin;
    private final DataStore store = DataStore.getInstance();

    public AdminDashboard(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setTop(UIFactory.navbar("Admin Dashboard", admin.getUsername(), "ADMIN", App::showLogin));
        root.setLeft(buildSidebar(root));
        root.setCenter(buildMainContent());
        root.setStyle("-fx-background-color: " + Theme.SURFACE + ";");

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("DIVERYU26 — Admin Dashboard");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────────
    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setStyle(
            "-fx-background-color: " + Theme.WHITE + ";" +
            "-fx-border-color: " + Theme.BORDER + ";" +
            "-fx-border-width: 0 1 0 0;"
        );
        sidebar.setPadding(new Insets(24, 0, 24, 0));

        String[] labels = { "📊  Overview", "👥  Kelola Seller", "🛒  Kelola Customer",
                            "📦  Semua Produk", "🔑  Kelola Admin", "👤  Profil Saya" };

        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            ToggleButton btn = sidebarBtn(labels[i], group);
            if (i == 0) btn.setSelected(true);
            btn.setOnAction(e -> {
                BorderPane bp = (BorderPane) stage.getScene().getRoot();
                switch (idx) {
                    case 0 -> bp.setCenter(wrapScroll(buildOverviewPanel()));
                    case 1 -> bp.setCenter(wrapScroll(buildSellerPanel()));
                    case 2 -> bp.setCenter(wrapScroll(buildCustomerPanel()));
                    case 3 -> bp.setCenter(wrapScroll(buildAllProductsPanel()));
                    case 4 -> bp.setCenter(wrapScroll(buildAdminManagementPanel()));
                    case 5 -> bp.setCenter(wrapScroll(buildProfilePanel()));
                }
            });
            sidebar.getChildren().add(btn);
        }

        return sidebar;
    }

    private ScrollPane wrapScroll(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + Theme.SURFACE + "; -fx-background: " + Theme.SURFACE + ";");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    private ToggleButton sidebarBtn(String text, ToggleGroup group) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setPrefWidth(220);
        btn.setPrefHeight(48);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 24));
        String base = "-fx-background-color: transparent; -fx-font-size: 14; -fx-text-fill: "
                    + Theme.TEXT_SECONDARY + "; -fx-cursor: hand; -fx-background-radius: 0;";
        String selected = "-fx-background-color: rgba(0,194,168,0.08); -fx-font-size: 14; -fx-text-fill: "
                    + Theme.ACCENT + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0;"
                    + "-fx-border-color: transparent transparent transparent " + Theme.ACCENT
                    + "; -fx-border-width: 0 0 0 3;";
        btn.setStyle(base);
        btn.selectedProperty().addListener((obs, o, nw) -> btn.setStyle(nw ? selected : base));
        return btn;
    }

    // ── Main Content ─────────────────────────────────────────────────────────────
    private ScrollPane buildMainContent() {
        return wrapScroll(buildOverviewPanel());
    }

    // ── Overview Panel ───────────────────────────────────────────────────────────
    private VBox buildOverviewPanel() {
        int sellerCount   = store.getSellers().size();
        int customerCount = store.getCustomers().size();
        int productCount  = store.getAllProducts().size();
        int totalUsers    = store.getAllUsers().size();

        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("Selamat datang, " + admin.getUsername() + " 👋");
        Label subtitle = UIFactory.bodyText("Kelola platform DIVERYU26 dari satu tempat.");

        HBox stats = new HBox(16,
            UIFactory.statCard(String.valueOf(totalUsers),    "Total Pengguna",  Theme.ACCENT),
            UIFactory.statCard(String.valueOf(sellerCount),   "Total Seller",    Theme.TEXT_PRIMARY),
            UIFactory.statCard(String.valueOf(customerCount), "Total Customer",  "#6366F1"),
            UIFactory.statCard(String.valueOf(productCount),  "Total Produk",    Theme.WARNING)
        );
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox recentCard = UIFactory.card(
            UIFactory.subheading("Aktivitas Terbaru"),
            UIFactory.divider(),
            activityItem("🆕", "Akun admin " + admin.getUsername() + " login", "Baru saja"),
            activityItem("📦", "Total " + productCount + " produk terdaftar di platform", "Aktif"),
            activityItem("👥", sellerCount + " seller aktif mengelola toko mereka", "Aktif"),
            activityItem("🛒", customerCount + " customer terdaftar di platform", "Aktif")
        );
        VBox.setVgrow(recentCard, Priority.NEVER);

        panel.getChildren().addAll(title, subtitle, stats, recentCard);
        return panel;
    }

    private HBox activityItem(String icon, String text, String time) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 18;");
        Label textLbl = new Label(text);
        textLbl.setStyle("-fx-font-size: 13; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size: 11; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
        HBox row = new HBox(12, iconLbl, textLbl, spacer, timeLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 0, 8, 0));
        return row;
    }

    // ── Seller Panel ─────────────────────────────────────────────────────────────
    private VBox buildSellerPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(32));
        panel.getChildren().addAll(
            UIFactory.heading("Kelola Seller"),
            UIFactory.bodyText("Lihat, nonaktifkan, atau hapus akun seller."),
            UIFactory.card(buildUserTable(store.getSellers(), true))
        );
        return panel;
    }

    // ── Customer Panel ───────────────────────────────────────────────────────────
    private VBox buildCustomerPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(32));
        panel.getChildren().addAll(
            UIFactory.heading("Kelola Customer"),
            UIFactory.bodyText("Lihat, nonaktifkan, atau hapus akun customer."),
            UIFactory.card(buildUserTable(store.getCustomers(), false))
        );
        return panel;
    }

    @SuppressWarnings("unchecked")
    private TableView<User> buildUserTable(List<User> users, boolean isSeller) {
        TableView<User> table = new TableView<>();
        table.setStyle("-fx-font-size: 13;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setMinWidth(160);

        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMinWidth(200);

        TableColumn<User, String> colStatus = new TableColumn<>("Status");
        colStatus.setMinWidth(100);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                User u = (User) getTableRow().getItem();
                setGraphic(UIFactory.badge(u.isActive() ? "Aktif" : "Nonaktif",
                        u.isActive() ? Theme.SUCCESS : Theme.DANGER));
            }
        });

        TableColumn<User, Void> colAction = new TableColumn<>("Aksi");
        colAction.setMinWidth(200);
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button toggleBtn = UIFactory.outlineBtn("Nonaktifkan");
            final Button deleteBtn = UIFactory.dangerBtn("Hapus");
            {
                toggleBtn.setPadding(new Insets(6, 14, 6, 14));
                deleteBtn.setPadding(new Insets(6, 14, 6, 14));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());
                toggleBtn.setText(u.isActive() ? "Nonaktifkan" : "Aktifkan");
                toggleBtn.setOnAction(e -> {
                    store.setUserActive(u.getId(), !u.isActive());
                    show();
                });
                deleteBtn.setOnAction(e -> {
                    if (UIFactory.showConfirm("Hapus Akun", "Hapus akun " + u.getUsername() + "?")) {
                        store.deleteUser(u.getId());
                        show();
                    }
                });
                HBox box = new HBox(8, toggleBtn, deleteBtn);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colUsername, colEmail, colStatus, colAction);
        table.getItems().addAll(users);
        return table;
    }

    // ── All Products Panel ───────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildAllProductsPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(32));

        TableView<Product> table = new TableView<>();
        table.setStyle("-fx-font-size: 13;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(460);

        TableColumn<Product, String> colName = new TableColumn<>("Produk");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> colSeller = new TableColumn<>("Seller");
        colSeller.setCellValueFactory(new PropertyValueFactory<>("sellerName"));

        TableColumn<Product, String> colCat = new TableColumn<>("Kategori");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Harga");
        colPrice.setMinWidth(130);
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                setText(getTableRow().getItem().getFormattedPrice());
            }
        });

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stok");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        table.getColumns().addAll(colName, colSeller, colCat, colPrice, colStock);
        table.getItems().addAll(store.getAllProducts());

        panel.getChildren().addAll(
            UIFactory.heading("Semua Produk"),
            UIFactory.bodyText("Daftar lengkap produk yang ada di platform."),
            UIFactory.card(table)
        );
        return panel;
    }

    // ── Admin Management Panel ────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildAdminManagementPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("Kelola Akun Admin");
        Label subtitle = UIFactory.bodyText(
            "Admin dengan ID lebih rendah dapat menghapus admin dengan ID lebih tinggi. " +
            "ID Anda: #" + admin.getId()
        );

        TableView<User> table = new TableView<>();
        table.setStyle("-fx-font-size: 13;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<User, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMinWidth(60);

        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setMinWidth(160);

        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMinWidth(200);

        TableColumn<User, String> colStatus = new TableColumn<>("Status");
        colStatus.setMinWidth(100);
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                User u = (User) getTableRow().getItem();
                setGraphic(UIFactory.badge(u.isActive() ? "Aktif" : "Nonaktif",
                        u.isActive() ? Theme.SUCCESS : Theme.DANGER));
            }
        });

        TableColumn<User, Void> colAction = new TableColumn<>("Aksi");
        colAction.setMinWidth(140);
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button deleteBtn = UIFactory.dangerBtn("Hapus");
            {
                deleteBtn.setPadding(new Insets(6, 14, 6, 14));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());

                // Cannot delete yourself
                if (u.getId().equals(admin.getId())) {
                    Label selfLabel = new Label("(Akun Anda)");
                    selfLabel.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
                    setGraphic(selfLabel);
                    return;
                }

                boolean canDelete = store.canAdminDelete(admin.getId(), u.getId());
                deleteBtn.setDisable(!canDelete);
                if (!canDelete) {
                    deleteBtn.setStyle(deleteBtn.getStyle() + "-fx-opacity: 0.4;");
                    Tooltip.install(deleteBtn, new Tooltip("Hanya admin dengan ID lebih rendah yang bisa menghapus"));
                }

                deleteBtn.setOnAction(e -> {
                    if (!store.canAdminDelete(admin.getId(), u.getId())) {
                        UIFactory.showAlert("Akses Ditolak",
                            "Anda tidak memiliki izin untuk menghapus admin ini.\n" +
                            "Hanya admin dengan ID lebih rendah yang bisa menghapus admin ber-ID lebih tinggi.",
                            javafx.scene.control.Alert.AlertType.WARNING);
                        return;
                    }
                    if (UIFactory.showConfirm("Hapus Admin", "Hapus akun admin " + u.getUsername() + "?\nTindakan ini tidak dapat dibatalkan.")) {
                        store.deleteUser(u.getId());
                        show();
                    }
                });

                setGraphic(deleteBtn);
            }
        });

        table.getColumns().addAll(colId, colUsername, colEmail, colStatus, colAction);
        table.getItems().addAll(store.getAdmins());

        VBox infoCard = UIFactory.card(
            new HBox(8,
                new Label("ℹ️"),
                UIFactory.bodyText("ID admin Anda: #" + admin.getId() +
                    " — Anda dapat menghapus admin dengan ID numerik lebih besar dari ID Anda.")
            )
        );

        panel.getChildren().addAll(title, subtitle, infoCard, UIFactory.card(table));
        return panel;
    }

    // ── Profile Panel ─────────────────────────────────────────────────────────────
    private VBox buildProfilePanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));
        panel.setMaxWidth(600);

        Label title    = UIFactory.heading("Profil Saya");
        Label subtitle = UIFactory.bodyText("Ubah nama akun atau kata sandi Anda.");

        // ── Change username card ──
        TextField newUsernameField = UIFactory.inputField("Username baru");
        newUsernameField.setText(admin.getUsername());
        newUsernameField.setPrefWidth(400);

        Button saveUsernameBtn = UIFactory.accentBtn("Simpan Username");
        saveUsernameBtn.setPrefWidth(400);
        Label usernameMsg = new Label();
        usernameMsg.setStyle("-fx-font-size: 12;");

        saveUsernameBtn.setOnAction(e -> {
            String newName = newUsernameField.getText().trim();
            if (newName.length() < 4) {
                usernameMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.DANGER + ";");
                usernameMsg.setText("Username minimal 4 karakter.");
                return;
            }
            if (store.updateUsername(admin.getId(), newName)) {
                admin.setUsername(newName);
                usernameMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.SUCCESS + ";");
                usernameMsg.setText("✅ Username berhasil diperbarui.");
                // Update navbar
                show();
            } else {
                usernameMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.DANGER + ";");
                usernameMsg.setText("Username sudah digunakan.");
            }
        });

        VBox usernameCard = UIFactory.card(
            UIFactory.subheading("Ganti Username"),
            UIFactory.divider(),
            UIFactory.formField("Username Baru", newUsernameField),
            usernameMsg,
            saveUsernameBtn
        );

        // ── Change password card ──
        PasswordField currentPassField = UIFactory.passwordField("Kata sandi saat ini");
        currentPassField.setPrefWidth(400);
        PasswordField newPassField = UIFactory.passwordField("Kata sandi baru (min. 6 karakter)");
        newPassField.setPrefWidth(400);
        PasswordField confirmPassField = UIFactory.passwordField("Ulangi kata sandi baru");
        confirmPassField.setPrefWidth(400);

        Button savePassBtn = UIFactory.accentBtn("Simpan Kata Sandi");
        savePassBtn.setPrefWidth(400);
        Label passMsg = new Label();
        passMsg.setStyle("-fx-font-size: 12;");

        savePassBtn.setOnAction(e -> {
            String current = currentPassField.getText();
            String newPass  = newPassField.getText();
            String confirm  = confirmPassField.getText();

            if (!admin.getPassword().equals(current)) {
                passMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.DANGER + ";");
                passMsg.setText("Kata sandi saat ini tidak cocok.");
                return;
            }
            if (newPass.length() < 6) {
                passMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.DANGER + ";");
                passMsg.setText("Kata sandi baru minimal 6 karakter.");
                return;
            }
            if (!newPass.equals(confirm)) {
                passMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.DANGER + ";");
                passMsg.setText("Konfirmasi kata sandi tidak cocok.");
                return;
            }
            store.updatePassword(admin.getId(), newPass);
            admin.setPassword(newPass);
            passMsg.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.SUCCESS + ";");
            passMsg.setText("✅ Kata sandi berhasil diperbarui.");
            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();
        });

        VBox passCard = UIFactory.card(
            UIFactory.subheading("Ganti Kata Sandi"),
            UIFactory.divider(),
            UIFactory.formField("Kata Sandi Saat Ini", currentPassField),
            UIFactory.formField("Kata Sandi Baru", newPassField),
            UIFactory.formField("Konfirmasi Kata Sandi Baru", confirmPassField),
            passMsg,
            savePassBtn
        );

        // Info card
        VBox infoCard = UIFactory.card(
            activityDetail("👤", "Username", admin.getUsername()),
            activityDetail("🔑", "Role", "ADMIN"),
            activityDetail("🆔", "ID Admin", "#" + admin.getId())
        );

        panel.getChildren().addAll(title, subtitle, infoCard, usernameCard, passCard);
        return panel;
    }

    private HBox activityDetail(String icon, String label, String value) {
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16;");
        Label labelLbl = new Label(label + ":");
        labelLbl.setStyle("-fx-font-size: 13; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-min-width: 100;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        HBox row = new HBox(12, iconLbl, labelLbl, valueLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        return row;
    }
}
