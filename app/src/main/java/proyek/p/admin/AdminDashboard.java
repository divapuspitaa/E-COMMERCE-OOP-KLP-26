package proyek.p.admin;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.*;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

import java.util.List;

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
        root.setLeft(buildSidebar());
        root.setCenter(buildMainContent());
        root.setStyle("-fx-background-color: " + Theme.SURFACE + ";");

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("ZALORA — Admin Dashboard");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setStyle(
            "-fx-background-color: " + Theme.WHITE + ";" +
            "-fx-border-color: " + Theme.BORDER + ";" +
            "-fx-border-width: 0 1 0 0;"
        );
        sidebar.setPadding(new Insets(24, 0, 24, 0));

        String[] labels  = { "📊  Overview", "👥  Kelola Seller", "🛒  Kelola Customer", "📦  Semua Produk" };
        VBox[] panels    = { buildOverviewPanel(), buildSellerPanel(), buildCustomerPanel(), buildAllProductsPanel() };

        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            ToggleButton btn = sidebarBtn(labels[i], group);
            if (i == 0) { btn.setSelected(true); }
            // store panel reference via userData
            btn.setUserData(panels[idx]);
            btn.setOnAction(e -> {
                BorderPane bp = (BorderPane) stage.getScene().getRoot();
                bp.setCenter((javafx.scene.Node) btn.getUserData());
            });
            sidebar.getChildren().add(btn);
        }

        return sidebar;
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
        ScrollPane sp = new ScrollPane(buildOverviewPanel());
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + Theme.SURFACE + "; -fx-background: " + Theme.SURFACE + ";");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
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
        Label subtitle = UIFactory.bodyText("Kelola platform ZALORA dari satu tempat.");

        HBox stats = new HBox(16,
            UIFactory.statCard(String.valueOf(totalUsers),    "Total Pengguna",  Theme.ACCENT),
            UIFactory.statCard(String.valueOf(sellerCount),   "Total Seller",    Theme.TEXT_PRIMARY),
            UIFactory.statCard(String.valueOf(customerCount), "Total Customer",  "#6366F1"),
            UIFactory.statCard(String.valueOf(productCount),  "Total Produk",    Theme.WARNING)
        );
        stats.setAlignment(Pos.CENTER_LEFT);

        // Recent activity card
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

        Label title    = UIFactory.heading("Kelola Seller");
        Label subtitle = UIFactory.bodyText("Lihat, nonaktifkan, atau hapus akun seller.");

        VBox tableCard = UIFactory.card(buildUserTable(store.getSellers(), true));
        panel.getChildren().addAll(title, subtitle, tableCard);
        return panel;
    }

    // ── Customer Panel ───────────────────────────────────────────────────────────
    private VBox buildCustomerPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("Kelola Customer");
        Label subtitle = UIFactory.bodyText("Lihat, nonaktifkan, atau hapus akun customer.");

        VBox tableCard = UIFactory.card(buildUserTable(store.getCustomers(), false));
        panel.getChildren().addAll(title, subtitle, tableCard);
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
                    refreshPanel(isSeller);
                });
                deleteBtn.setOnAction(e -> {
                    if (UIFactory.showConfirm("Hapus Akun", "Hapus akun " + u.getUsername() + "?")) {
                        store.deleteUser(u.getId());
                        refreshPanel(isSeller);
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

        Label title    = UIFactory.heading("Semua Produk");
        Label subtitle = UIFactory.bodyText("Daftar lengkap produk yang ada di platform.");

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

        VBox tableCard = UIFactory.card(table);
        panel.getChildren().addAll(title, subtitle, tableCard);
        return panel;
    }

    private void refreshPanel(boolean isSeller) {
        // Re-show the dashboard to refresh data
        show();
    }
}
