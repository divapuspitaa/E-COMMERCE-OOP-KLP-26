package proyek.p.seller;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.DataStore;
import proyek.p.model.Product;
import proyek.p.model.Seller;
import proyek.p.model.Transaction;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

public class SellerDashboard {
    private final Stage     stage;
    private final Seller    seller;
    private final DataStore store = DataStore.getInstance();

    public SellerDashboard(Stage stage, Seller seller) {
        this.stage  = stage;
        this.seller = seller;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setTop(UIFactory.navbar("Seller Dashboard", seller.getUsername(), "SELLER", App::showLogin));
        root.setLeft(buildSidebar(root));
        root.setCenter(wrapScroll(buildProductListPanel()));
        root.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("DIVERYU26 — Seller Dashboard");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private VBox buildSidebar(BorderPane root) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setBackground(new Background(new BackgroundFill(Theme.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        sidebar.setBorder(new Border(new BorderStroke(
            Theme.BORDER, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0)
        )));
        sidebar.setPadding(new Insets(24, 0, 24, 0));

        ToggleGroup group = new ToggleGroup();

        ToggleButton btnList    = sidebarBtn("📦  Produk Saya",   group, true);
        ToggleButton btnAdd     = sidebarBtn("➕  Tambah Produk", group, false);
        ToggleButton btnRevenue = sidebarBtn("💰  Pendapatan",    group, false);
        ToggleButton btnProfile = sidebarBtn("👤  Profil Saya",   group, false);

        btnList.setOnAction(e    -> root.setCenter(wrapScroll(buildProductListPanel())));
        btnAdd.setOnAction(e     -> root.setCenter(wrapScroll(buildAddProductPanel(root))));
        btnRevenue.setOnAction(e -> root.setCenter(wrapScroll(buildRevenuePanel())));
        btnProfile.setOnAction(e -> root.setCenter(wrapScroll(buildProfilePanel())));

        VBox profile = buildProfileSnippet();

        sidebar.getChildren().addAll(
            btnList, btnAdd, btnRevenue, btnProfile,
            new Region() {{ VBox.setVgrow(this, Priority.ALWAYS); }},
            profile
        );
        return sidebar;
    }

    private VBox buildProfileSnippet() {
        Label nameLbl = new Label(seller.getUsername());
        nameLbl.setFont(Theme.bodyFontBold(13));
        nameLbl.setTextFill(Theme.TEXT_PRIMARY);

        Label emailLbl = new Label(seller.getEmail());
        emailLbl.setFont(Theme.bodyFont(11));
        emailLbl.setTextFill(Theme.TEXT_MUTED);

        Label badge = UIFactory.badge("SELLER", Theme.ACCENT);

        VBox box = new VBox(4, badge, nameLbl, emailLbl);
        box.setPadding(new Insets(16, 20, 16, 20));
        box.setBackground(new Background(new BackgroundFill(
            Color.rgb(0, 194, 168, 0.05), CornerRadii.EMPTY, Insets.EMPTY
        )));
        return box;
    }

    private ToggleButton sidebarBtn(String text, ToggleGroup group, boolean selected) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setSelected(selected);
        btn.setPrefWidth(220);
        btn.setPrefHeight(48);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 24));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setFont(Theme.bodyFont(14));
        btn.setTextFill(Theme.TEXT_SECONDARY);
        btn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Apply initial state if already selected
        if (selected) applySelectedStyle(btn);

        btn.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            if (isSelected) applySelectedStyle(btn);
            else            applyDeselectedStyle(btn);
        });
        return btn;
    }

    private void applySelectedStyle(ToggleButton btn) {
        btn.setTextFill(Theme.ACCENT);
        btn.setFont(Theme.bodyFontBold(14));
        btn.setBackground(new Background(new BackgroundFill(
            Color.rgb(0, 194, 168, 0.08), CornerRadii.EMPTY, Insets.EMPTY
        )));
        btn.setBorder(new Border(new BorderStroke(
            Theme.ACCENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 3)
        )));
    }

    private void applyDeselectedStyle(ToggleButton btn) {
        btn.setTextFill(Theme.TEXT_SECONDARY);
        btn.setFont(Theme.bodyFont(14));
        btn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        btn.setBorder(Border.EMPTY);
    }

    private ScrollPane wrapScroll(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    // ── Product List Panel ────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildProductListPanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));

        List<Product> myProducts = store.getProductsBySeller(seller.getId());

        Label title    = UIFactory.heading("Produk Saya");
        Label subtitle = UIFactory.bodyText("Kamu memiliki " + myProducts.size() + " produk terdaftar.");

        double totalValue = myProducts.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();
        HBox stats = new HBox(16,
            UIFactory.statCard(String.valueOf(myProducts.size()), "Total Produk", Theme.ACCENT_HEX),
            UIFactory.statCard(String.format("Rp %,.0f", totalValue), "Nilai Inventori", Theme.SUCCESS_HEX)
        );

        TableView<Product> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(380);
        table.setStyle("-fx-font-size: 13;");

        TableColumn<Product, String> colName = new TableColumn<>("Nama Produk");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setMinWidth(180);

        TableColumn<Product, String> colCat = new TableColumn<>("Kategori");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> colPrice = new TableColumn<>("Harga");
        colPrice.setMinWidth(130);
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                setText(getTableRow().getItem().getFormattedPrice());
            }
        });

        TableColumn<Product, Integer> colStock = new TableColumn<>("Stok");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Product, Void> colAction = new TableColumn<>("Aksi");
        colAction.setMinWidth(200);
        colAction.setCellFactory(col -> new TableCell<>() {
            final Button editBtn   = UIFactory.outlineBtn("Edit");
            final Button deleteBtn = UIFactory.dangerBtn("Hapus");
            {
                editBtn.setPadding(new Insets(6, 14, 6, 14));
                deleteBtn.setPadding(new Insets(6, 14, 6, 14));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Product p = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showEditDialog(p));
                deleteBtn.setOnAction(e -> {
                    if (UIFactory.showConfirm("Hapus Produk", "Hapus produk \"" + p.getName() + "\"?")) {
                        store.deleteProduct(p.getId());
                        show();
                    }
                });
                HBox box = new HBox(8, editBtn, deleteBtn);
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colName, colCat, colPrice, colStock, colAction);
        table.getItems().addAll(myProducts);

        VBox tableCard = UIFactory.card(table);
        panel.getChildren().addAll(title, subtitle, stats, tableCard);
        return panel;
    }

    // ── Add Product Panel ─────────────────────────────────────────────────────
    private VBox buildAddProductPanel(BorderPane root) {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("Tambah Produk Baru");
        Label subtitle = UIFactory.bodyText("Isi detail produk yang ingin Anda jual.");

        TextField nameField  = UIFactory.inputField("Nama produk");
        nameField.setPrefWidth(480);
        TextArea  descArea   = UIFactory.textArea("Deskripsi produk...", 4);
        descArea.setPrefWidth(480);
        TextField priceField = UIFactory.inputField("Contoh: 299000");
        priceField.setPrefWidth(480);
        TextField stockField = UIFactory.inputField("Contoh: 50");
        stockField.setPrefWidth(480);

        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Sepatu", "Pakaian", "Aksesoris", "Tas", "Olahraga", "Lainnya");
        catBox.setPromptText("Pilih kategori");
        catBox.setStyle("-fx-font-size: 13;");
        catBox.setPrefWidth(480);

        Label errorLbl = new Label();
        errorLbl.setFont(Theme.bodyFont(13));
        errorLbl.setTextFill(Theme.DANGER);
        errorLbl.setVisible(false);

        Button submitBtn = UIFactory.accentBtn("Simpan Produk");
        submitBtn.setPrefWidth(480);
        submitBtn.setPrefHeight(48);

        submitBtn.setOnAction(e -> {
            errorLbl.setVisible(false);
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            String cat  = catBox.getValue();

            if (name.isEmpty() || desc.isEmpty() || cat == null) {
                showFieldError(errorLbl, "Nama, deskripsi, dan kategori wajib diisi."); return;
            }
            double price;
            int    stock;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException ex) { showFieldError(errorLbl, "Format harga tidak valid."); return; }
            try { stock = Integer.parseInt(stockField.getText().trim()); }
            catch (NumberFormatException ex) { showFieldError(errorLbl, "Format stok tidak valid."); return; }

            Product p = new Product(
                store.generateId(), name, desc, price, stock, cat,
                seller.getId(), seller.getUsername()
            );
            store.addProduct(p);
            UIFactory.showAlert("Berhasil!", "Produk berhasil ditambahkan.", Alert.AlertType.INFORMATION);
            show();
        });

        VBox form = UIFactory.card(
            UIFactory.subheading("Detail Produk"),
            UIFactory.divider(),
            UIFactory.formField("Nama Produk *", nameField),
            UIFactory.formField("Deskripsi *",   descArea),
            UIFactory.formField("Harga (Rp) *",  priceField),
            UIFactory.formField("Stok *",         stockField),
            UIFactory.formField("Kategori *",     catBox),
            errorLbl,
            submitBtn
        );
        form.setMaxWidth(540);

        panel.getChildren().addAll(title, subtitle, form);
        return panel;
    }

    // ── Edit Product Dialog ───────────────────────────────────────────────────
    private void showEditDialog(Product product) {
        Stage dialog = new Stage();
        dialog.setTitle("Edit Produk");
        dialog.initOwner(stage);

        TextField nameField  = UIFactory.inputField("Nama produk");
        nameField.setText(product.getName());
        nameField.setPrefWidth(400);

        TextArea descArea = UIFactory.textArea("Deskripsi", 3);
        descArea.setText(product.getDescription());
        descArea.setPrefWidth(400);

        TextField priceField = UIFactory.inputField("Harga");
        priceField.setText(String.valueOf((int) product.getPrice()));
        priceField.setPrefWidth(400);

        TextField stockField = UIFactory.inputField("Stok");
        stockField.setText(String.valueOf(product.getStock()));
        stockField.setPrefWidth(400);

        ComboBox<String> catBox = new ComboBox<>();
        catBox.getItems().addAll("Sepatu", "Pakaian", "Aksesoris", "Tas", "Olahraga", "Lainnya");
        catBox.setValue(product.getCategory());
        catBox.setStyle("-fx-font-size: 13;");
        catBox.setPrefWidth(400);

        Button saveBtn   = UIFactory.accentBtn("Simpan Perubahan");
        Button cancelBtn = UIFactory.outlineBtn("Batal");
        saveBtn.setPrefWidth(190);
        cancelBtn.setPrefWidth(190);

        Label errorLbl = new Label();
        errorLbl.setFont(Theme.bodyFont(13));
        errorLbl.setTextFill(Theme.DANGER);

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            String cat  = catBox.getValue();

            if (name.isEmpty() || desc.isEmpty() || cat == null) {
                errorLbl.setText("Semua field wajib diisi."); return;
            }
            double price;
            int    stock;
            try { price = Double.parseDouble(priceField.getText().trim()); }
            catch (NumberFormatException ex) { errorLbl.setText("Format harga tidak valid."); return; }
            try { stock = Integer.parseInt(stockField.getText().trim()); }
            catch (NumberFormatException ex) { errorLbl.setText("Format stok tidak valid."); return; }

            product.setName(name);
            product.setDescription(desc);
            product.setPrice(price);
            product.setStock(stock);
            product.setCategory(cat);
            dialog.close();
            UIFactory.showAlert("Berhasil!", "Produk berhasil diperbarui.", Alert.AlertType.INFORMATION);
            show();
        });
        cancelBtn.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(12, saveBtn, cancelBtn);

        VBox content = new VBox(14,
            UIFactory.subheading("Edit: " + product.getName()),
            UIFactory.divider(),
            UIFactory.formField("Nama Produk", nameField),
            UIFactory.formField("Deskripsi",   descArea),
            UIFactory.formField("Harga (Rp)",  priceField),
            UIFactory.formField("Stok",         stockField),
            UIFactory.formField("Kategori",     catBox),
            errorLbl, btnRow
        );
        content.setPadding(new Insets(28));
        content.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        dialog.setScene(new Scene(content, 460, 580));
        dialog.showAndWait();
    }

    private void showFieldError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
    }

    // ── Revenue Panel ─────────────────────────────────────────────────────────
    private VBox buildRevenuePanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("💰 Pendapatan");
        Label subtitle = UIFactory.bodyText("Riwayat penjualan dan pendapatan dari produk Anda.");

        List<Transaction> txList        = store.getTransactionsBySeller(seller.getId());
        double            totalRevenue  = store.getTotalRevenueBySeller(seller.getId());
        long              totalItemSold = txList.stream().mapToLong(Transaction::getQuantity).sum();

        HBox stats = new HBox(16,
            UIFactory.statCard(String.format("Rp %,.0f", totalRevenue), "Total Pendapatan", Theme.SUCCESS_HEX),
            UIFactory.statCard(String.valueOf(txList.size()),            "Jumlah Transaksi", Theme.ACCENT_HEX),
            UIFactory.statCard(String.valueOf(totalItemSold),            "Item Terjual",     Theme.WARNING_HEX)
        );

        if (txList.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));

            Label emptyIcon = new Label("📊");
            emptyIcon.setFont(Theme.bodyFont(52));

            Label emptyMsg = new Label("Belum ada transaksi masuk");
            emptyMsg.setFont(Theme.bodyFont(16));
            emptyMsg.setTextFill(Theme.TEXT_MUTED);

            Label emptyHint = UIFactory.caption("Pendapatan akan tampil di sini setelah customer membeli produk Anda.");
            emptyBox.getChildren().addAll(emptyIcon, emptyMsg, emptyHint);
            panel.getChildren().addAll(title, subtitle, stats, emptyBox);
            return panel;
        }

        // Revenue banner with gradient
        VBox revBanner = buildRevenueBanner(totalRevenue, txList.size());

        // Transaction table
        Label txTitle = UIFactory.subheading("Riwayat Transaksi");

        @SuppressWarnings("unchecked")
        TableView<Transaction> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(340);
        table.setStyle("-fx-font-size: 13;");

        TableColumn<Transaction, String> colDate = new TableColumn<>("Tanggal");
        colDate.setMinWidth(150);
        colDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                setText(getTableRow().getItem().getFormattedDate());
            }
        });

        TableColumn<Transaction, String> colProd = new TableColumn<>("Produk");
        colProd.setMinWidth(180);
        colProd.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                setText(getTableRow().getItem().getProductName());
            }
        });

        TableColumn<Transaction, String> colCust = new TableColumn<>("Pembeli");
        colCust.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                setText(getTableRow().getItem().getCustomerName());
            }
        });

        TableColumn<Transaction, Integer> colQty = new TableColumn<>("Qty");
        colQty.setMaxWidth(60);
        colQty.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                setText(String.valueOf(getTableRow().getItem().getQuantity()));
            }
        });

        TableColumn<Transaction, Double> colTotal = new TableColumn<>("Total");
        colTotal.setMinWidth(130);
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null); setText(null); return;
                }
                Label lbl = new Label(getTableRow().getItem().getFormattedTotal());
                lbl.setFont(Theme.bodyFontBold(13));
                lbl.setTextFill(Theme.SUCCESS);
                setGraphic(lbl);
                setText(null);
            }
        });

        table.getColumns().addAll(colDate, colProd, colCust, colQty, colTotal);
        table.getItems().addAll(txList);

        VBox tableCard = UIFactory.card(txTitle, UIFactory.divider(), table);
        panel.getChildren().addAll(title, subtitle, stats, revBanner, tableCard);
        return panel;
    }

    private VBox buildRevenueBanner(double totalRevenue, int txCount) {
        VBox revBanner = new VBox(6);
        revBanner.setAlignment(Pos.CENTER_LEFT);
        revBanner.setPadding(new Insets(20, 24, 20, 24));

        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#00c2a8")),
            new Stop(1, Color.web("#00a085"))
        };
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops
        );
        revBanner.setBackground(new Background(new BackgroundFill(
            gradient, new CornerRadii(12), Insets.EMPTY
        )));

        Label revLabel = new Label("Total Pendapatan Anda");
        revLabel.setFont(Theme.bodyFont(13));
        revLabel.setTextFill(Color.rgb(255, 255, 255, 0.8));

        Label revAmount = new Label(String.format("Rp %,.0f", totalRevenue));
        revAmount.setFont(Theme.bodyFontBold(32));
        revAmount.setTextFill(Theme.WHITE);

        Label revSub = new Label("Dari " + txCount + " transaksi berhasil");
        revSub.setFont(Theme.bodyFont(12));
        revSub.setTextFill(Color.rgb(255, 255, 255, 0.7));

        revBanner.getChildren().addAll(revLabel, revAmount, revSub);
        return revBanner;
    }

    // ── Profile Panel ─────────────────────────────────────────────────────────
    private VBox buildProfilePanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));
        panel.setMaxWidth(600);

        Label title    = UIFactory.heading("Profil Saya");
        Label subtitle = UIFactory.bodyText("Ubah nama akun atau kata sandi Anda.");

        VBox infoCard = UIFactory.card(
            infoRow("👤", "Username", seller.getUsername()),
            infoRow("📧", "Email",    seller.getEmail()),
            infoRow("🔑", "Role",     "SELLER")
        );

        // ── Change username ──
        TextField newUsernameField = UIFactory.inputField("Username baru");
        newUsernameField.setText(seller.getUsername());
        newUsernameField.setPrefWidth(400);

        Button saveUsernameBtn = UIFactory.accentBtn("Simpan Username");
        saveUsernameBtn.setPrefWidth(400);

        Label usernameMsg = new Label();
        usernameMsg.setFont(Theme.bodyFont(12));

        saveUsernameBtn.setOnAction(e -> {
            String newName = newUsernameField.getText().trim();
            if (newName.length() < 4) {
                usernameMsg.setTextFill(Theme.DANGER);
                usernameMsg.setText("Username minimal 4 karakter.");
                return;
            }
            if (DataStore.getInstance().updateUsername(seller.getId(), newName)) {
                seller.setUsername(newName);
                usernameMsg.setTextFill(Theme.SUCCESS);
                usernameMsg.setText("✅ Username berhasil diperbarui.");
                show();
            } else {
                usernameMsg.setTextFill(Theme.DANGER);
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

        // ── Change password ──
        PasswordField currentPassField = UIFactory.passwordField("Kata sandi saat ini");
        currentPassField.setPrefWidth(400);
        PasswordField newPassField     = UIFactory.passwordField("Kata sandi baru (min. 6 karakter)");
        newPassField.setPrefWidth(400);
        PasswordField confirmPassField = UIFactory.passwordField("Ulangi kata sandi baru");
        confirmPassField.setPrefWidth(400);

        Button savePassBtn = UIFactory.accentBtn("Simpan Kata Sandi");
        savePassBtn.setPrefWidth(400);

        Label passMsg = new Label();
        passMsg.setFont(Theme.bodyFont(12));

        savePassBtn.setOnAction(e -> {
            String current = currentPassField.getText();
            String newPass  = newPassField.getText();
            String confirm  = confirmPassField.getText();

            if (!seller.getPassword().equals(current)) {
                passMsg.setTextFill(Theme.DANGER);
                passMsg.setText("Kata sandi saat ini tidak cocok.");
                return;
            }
            if (newPass.length() < 6) {
                passMsg.setTextFill(Theme.DANGER);
                passMsg.setText("Kata sandi baru minimal 6 karakter.");
                return;
            }
            if (!newPass.equals(confirm)) {
                passMsg.setTextFill(Theme.DANGER);
                passMsg.setText("Konfirmasi kata sandi tidak cocok.");
                return;
            }
            DataStore.getInstance().updatePassword(seller.getId(), newPass);
            seller.setPassword(newPass);
            passMsg.setTextFill(Theme.SUCCESS);
            passMsg.setText("✅ Kata sandi berhasil diperbarui.");
            currentPassField.clear();
            newPassField.clear();
            confirmPassField.clear();
        });

        VBox passCard = UIFactory.card(
            UIFactory.subheading("Ganti Kata Sandi"),
            UIFactory.divider(),
            UIFactory.formField("Kata Sandi Saat Ini",        currentPassField),
            UIFactory.formField("Kata Sandi Baru",            newPassField),
            UIFactory.formField("Konfirmasi Kata Sandi Baru", confirmPassField),
            passMsg,
            savePassBtn
        );

        panel.getChildren().addAll(title, subtitle, infoCard, usernameCard, passCard);
        return panel;
    }

    private HBox infoRow(String icon, String label, String value) {
        Label iconLbl  = new Label(icon);
        iconLbl.setFont(Theme.bodyFont(16));

        Label labelLbl = new Label(label + ":");
        labelLbl.setFont(Theme.bodyFont(13));
        labelLbl.setTextFill(Theme.TEXT_SECONDARY);
        labelLbl.setMinWidth(100);

        Label valueLbl = new Label(value);
        valueLbl.setFont(Theme.bodyFontBold(13));
        valueLbl.setTextFill(Theme.TEXT_PRIMARY);

        HBox row = new HBox(12, iconLbl, labelLbl, valueLbl);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));
        return row;
    }
}