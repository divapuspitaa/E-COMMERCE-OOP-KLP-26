package proyek.p.customer;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.Customer;
import proyek.p.model.DataStore;
import proyek.p.model.Product;
import proyek.p.model.Transaction;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

public class CustomerDashboard {
    private final Stage    stage;
    private final Customer customer;
    private final DataStore store = DataStore.getInstance();

    public CustomerDashboard(Stage stage, Customer customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setTop(buildTopBar(root));
        root.setCenter(wrapScroll(buildShopPanel()));
        root.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("DIVERYU26 — Belanja");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    // ── Top Bar with search ───────────────────────────────────────────────────────
    private VBox buildTopBar(BorderPane root) {
        HBox navbar = UIFactory.navbar("Belanja", customer.getUsername(), "CUSTOMER", App::showLogin);

        // Category tabs under nav
        HBox cats = new HBox(0);
        cats.setBackground(new Background(new BackgroundFill(Theme.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        cats.setBorder(new Border(new BorderStroke(Theme.BORDER, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        cats.setPadding(new Insets(0, 24, 0, 24));
        cats.setAlignment(Pos.CENTER_LEFT);

        String[] categories = { "Semua", "Sepatu", "Pakaian", "Aksesoris", "Tas", "Olahraga" };
        ToggleGroup catGroup = new ToggleGroup();

        for (String cat : categories) {
            ToggleButton btn = catTabBtn(cat, catGroup, cat.equals("Semua"));
            btn.setOnAction(e -> {
                BorderPane bp = (BorderPane) stage.getScene().getRoot();
                bp.setCenter(wrapScroll(buildFilteredPanel(cat.equals("Semua") ? null : cat, "")));
            });
            cats.getChildren().add(btn);
        }

        // Riwayat tab
        ToggleButton btnRiwayat = catTabBtn("📋 Riwayat", catGroup, false);
        btnRiwayat.setOnAction(e -> {
            BorderPane bp = (BorderPane) stage.getScene().getRoot();
            bp.setCenter(wrapScroll(buildRiwayatPanel()));
        });
        cats.getChildren().add(btnRiwayat);

        ToggleButton btnProfil = catTabBtn("👤 Profil", catGroup, false);
        btnProfil.setOnAction(e -> {
            BorderPane bp = (BorderPane) stage.getScene().getRoot();
            bp.setCenter(wrapScroll(buildProfilePanel()));
        });
        cats.getChildren().add(btnProfil);

        // Search bar row
        HBox searchRow = new HBox(12);
        searchRow.setPadding(new Insets(12, 24, 12, 24));
        searchRow.setBackground(new Background(new BackgroundFill(Theme.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        searchRow.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = UIFactory.inputField("🔍  Cari produk, merek, kategori...");
        searchField.setPrefWidth(380);
        searchField.setPrefHeight(40);
        Button searchBtn = UIFactory.accentBtn("Cari");
        searchBtn.setPrefHeight(40);
        searchBtn.setOnAction(e -> {
            String q = searchField.getText().trim().toLowerCase();
            BorderPane bp = (BorderPane) stage.getScene().getRoot();
            bp.setCenter(wrapScroll(buildFilteredPanel(null, q)));
        });
        searchField.setOnAction(e -> searchBtn.fire());

        Label resultHint = UIFactory.caption("Menampilkan semua produk");
        searchRow.getChildren().addAll(searchField, searchBtn, resultHint);

        return new VBox(navbar, searchRow, cats);
    }

    private ToggleButton catTabBtn(String text, ToggleGroup group, boolean selected) {
        ToggleButton btn = new ToggleButton(text);
        btn.setToggleGroup(group);
        btn.setSelected(selected);
        btn.setPrefHeight(44);
        btn.setPadding(new Insets(0, 20, 0, 20));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        if (selected) {
            btn.setFont(Theme.bodyFontBold(13));
            btn.setTextFill(Theme.TEXT_PRIMARY);
            btn.setBorder(new Border(new BorderStroke(
                Theme.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 2, 0)
            )));
        } else {
            btn.setFont(Theme.bodyFont(13));
            btn.setTextFill(Theme.TEXT_SECONDARY);
            btn.setBorder(Border.EMPTY);
        }

        btn.selectedProperty().addListener((obs, o, nw) -> {
            if (nw) {
                btn.setFont(Theme.bodyFontBold(13));
                btn.setTextFill(Theme.TEXT_PRIMARY);
                btn.setBorder(new Border(new BorderStroke(
                    Theme.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 2, 0)
                )));
            } else {
                btn.setFont(Theme.bodyFont(13));
                btn.setTextFill(Theme.TEXT_SECONDARY);
                btn.setBorder(Border.EMPTY);
            }
        });
        return btn;
    }

    // ── Shop Panel ───────────────────────────────────────────────────────────────
    private VBox buildShopPanel() {
        return buildFilteredPanel(null, "");
    }

    private VBox buildFilteredPanel(String category, String query) {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(28));

        List<Product> products = store.getAllProducts().stream()
            .filter(p -> category == null || p.getCategory().equals(category))
            .filter(p -> query.isEmpty()
                    || p.getName().toLowerCase().contains(query)
                    || p.getSellerName().toLowerCase().contains(query)
                    || p.getCategory().toLowerCase().contains(query))
            .collect(Collectors.toList());

        String headingText = category != null ? category
                           : query.isEmpty()  ? "Semua Produk"
                           : "Hasil untuk \"" + query + "\"";

        Label heading  = UIFactory.heading(headingText);
        Label countLbl = UIFactory.bodyText(products.size() + " produk ditemukan");

        FlowPane grid = buildProductGrid(products);

        panel.getChildren().addAll(heading, countLbl, grid);
        return panel;
    }

    // ── Product Grid ─────────────────────────────────────────────────────────────
    private FlowPane buildProductGrid(List<Product> products) {
        FlowPane grid = new FlowPane(16, 16);
        grid.setPrefWrapLength(900);

        if (products.isEmpty()) {
            Label empty = new Label("😔 Tidak ada produk ditemukan");
            empty.setFont(Theme.bodyFont(16));
            empty.setTextFill(Theme.TEXT_MUTED);
            empty.setPadding(new Insets(40));
            grid.getChildren().add(empty);
            return grid;
        }

        for (Product p : products) {
            grid.getChildren().add(buildProductCard(p));
        }
        return grid;
    }

    private VBox buildProductCard(Product product) {
        String[] colors = { "#E8F4F0", "#FFF0F0", "#F0F0FF", "#FFFAF0", "#F0FFF4", "#FFF5E0" };
        int colorIdx = Math.abs(product.getId().hashCode()) % colors.length;
        String bgColor = colors[colorIdx];

        String emoji = switch (product.getCategory()) {
            case "Sepatu"     -> "👟";
            case "Pakaian"    -> "👕";
            case "Aksesoris"  -> "⌚";
            case "Tas"        -> "👜";
            case "Olahraga"   -> "⚽";
            default           -> "🛍";
        };

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(200, 160);
        imgBox.setBackground(new Background(new BackgroundFill(
            Color.web(bgColor), new CornerRadii(8, 8, 0, 0, false), Insets.EMPTY
        )));
        Label emojiLbl = new Label(emoji);
        emojiLbl.setFont(Theme.bodyFont(52));
        imgBox.getChildren().add(emojiLbl);

        Label catBadge = UIFactory.badge(product.getCategory(), Theme.ACCENT);

        Label nameLbl = new Label(product.getName());
        nameLbl.setFont(Theme.bodyFontBold(14));
        nameLbl.setTextFill(Theme.TEXT_PRIMARY);
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(190);

        Label sellerLbl = new Label("oleh " + product.getSellerName());
        sellerLbl.setFont(Theme.bodyFont(11));
        sellerLbl.setTextFill(Theme.TEXT_MUTED);

        Label priceLbl = new Label(product.getFormattedPrice());
        priceLbl.setFont(Theme.bodyFontBold(16));
        priceLbl.setTextFill(Theme.TEXT_PRIMARY);

        Label stockLbl = UIFactory.badge("Stok: " + product.getStock(), product.getStock() > 5 ? Theme.SUCCESS : Theme.WARNING);

        HBox priceRow = new HBox(8, priceLbl);
        priceRow.setAlignment(Pos.CENTER_LEFT);

        Button detailBtn = UIFactory.outlineBtn("Lihat Detail");
        detailBtn.setPrefWidth(200);
        detailBtn.setOnAction(e -> showProductDetail(product));

        VBox info = new VBox(6, catBadge, nameLbl, sellerLbl, priceRow, stockLbl, detailBtn);
        info.setPadding(new Insets(14));

        DropShadow normalShadow = new DropShadow();
        normalShadow.setColor(Color.rgb(0, 0, 0, 0.06));
        normalShadow.setRadius(10);
        normalShadow.setOffsetY(3);

        DropShadow hoverShadow = new DropShadow();
        hoverShadow.setColor(Color.rgb(0, 0, 0, 0.14));
        hoverShadow.setRadius(20);
        hoverShadow.setOffsetY(6);

        VBox card = new VBox(0, imgBox, info);
        card.setPrefWidth(218);
        card.setBackground(new Background(new BackgroundFill(Theme.WHITE, new CornerRadii(12), Insets.EMPTY)));
        card.setEffect(normalShadow);
        card.setCursor(javafx.scene.Cursor.HAND);

        card.setOnMouseEntered(e -> {
            card.setEffect(hoverShadow);
            card.setTranslateY(-3);
        });
        card.setOnMouseExited(e -> {
            card.setEffect(normalShadow);
            card.setTranslateY(0);
        });
        card.setOnMouseClicked(e -> showProductDetail(product));

        return card;
    }

    // ── Product Detail Dialog ────────────────────────────────────────────────────
    private void showProductDetail(Product product) {
        Stage dialog = new Stage();
        dialog.setTitle(product.getName());
        dialog.initOwner(stage);

        String[] colors = { "#E8F4F0", "#FFF0F0", "#F0F0FF", "#FFFAF0", "#F0FFF4", "#FFF5E0" };
        int colorIdx = Math.abs(product.getId().hashCode()) % colors.length;
        String emoji = switch (product.getCategory()) {
            case "Sepatu"    -> "👟";
            case "Pakaian"   -> "👕";
            case "Aksesoris" -> "⌚";
            case "Tas"       -> "👜";
            case "Olahraga"  -> "⚽";
            default          -> "🛍";
        };

        StackPane imgArea = new StackPane();
        imgArea.setPrefSize(460, 220);
        imgArea.setBackground(new Background(new BackgroundFill(Color.web(colors[colorIdx]), CornerRadii.EMPTY, Insets.EMPTY)));
        Label emojiLbl = new Label(emoji);
        emojiLbl.setFont(Theme.bodyFont(80));
        imgArea.getChildren().add(emojiLbl);

        Label catBadge  = UIFactory.badge(product.getCategory(), Theme.ACCENT);
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Theme.bodyFontBold(22));
        nameLabel.setTextFill(Theme.TEXT_PRIMARY);
        nameLabel.setWrapText(true);
        Label sellerLbl = UIFactory.bodyText("Dijual oleh: " + product.getSellerName());
        Label priceLbl  = new Label(product.getFormattedPrice());
        priceLbl.setFont(Theme.bodyFontBold(24));
        priceLbl.setTextFill(Theme.TEXT_PRIMARY);
        Label stockLbl  = UIFactory.badge("Stok tersedia: " + product.getStock(), Theme.SUCCESS);
        Label descTitle = UIFactory.subheading("Deskripsi");
        Label descLbl   = new Label(product.getDescription());
        descLbl.setFont(Theme.bodyFont(13));
        descLbl.setTextFill(Theme.TEXT_SECONDARY);
        descLbl.setWrapText(true);

        Spinner<Integer> qtySpinner = new Spinner<>(1, Math.max(1, product.getStock()), 1);
        qtySpinner.setPrefWidth(120);

        Label totalPreview = new Label("Total: " + product.getFormattedPrice());
        totalPreview.setFont(Theme.bodyFont(14));
        totalPreview.setTextFill(Theme.TEXT_MUTED);
        qtySpinner.valueProperty().addListener((obs, o, nw) ->
            totalPreview.setText("Total: " + String.format("Rp %,.0f", product.getPrice() * nw))
        );

        Button buyBtn = UIFactory.accentBtn("🛒  Beli Sekarang");
        buyBtn.setPrefWidth(280);
        buyBtn.setPrefHeight(48);
        buyBtn.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            double total = product.getPrice() * qty;

            product.setStock(product.getStock() - qty);

            Transaction tx = new Transaction(
                store.generateId(),
                customer.getId(), customer.getUsername(),
                product.getSellerId(), product.getId(), product.getName(),
                qty, total
            );
            store.addTransaction(tx);

            dialog.close();
            showSuccessScene(product, qty, total, tx);
        });

        VBox info = new VBox(12,
            catBadge, nameLabel, sellerLbl, UIFactory.divider(),
            priceLbl, stockLbl, UIFactory.divider(),
            descTitle, descLbl, UIFactory.divider(),
            new HBox(12, UIFactory.formLabel("Jumlah:"), qtySpinner),
            totalPreview,
            buyBtn
        );
        info.setPadding(new Insets(20));
        info.setBackground(new Background(new BackgroundFill(Theme.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox root = new VBox(imgArea, info);
        dialog.setScene(new Scene(root, 460, 660));
        dialog.showAndWait();
    }

    // ── Halaman Sukses Setelah Beli ───────────────────────────────────────────────
    private void showSuccessScene(Product product, int qty, double total, Transaction tx) {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setTop(UIFactory.navbar("Pembelian Berhasil", customer.getUsername(), "CUSTOMER", App::showLogin));

        VBox content = new VBox(0);
        content.setAlignment(Pos.TOP_CENTER);

        // Hero sukses
        VBox hero = new VBox(12);
        hero.setAlignment(Pos.CENTER);
        hero.setPadding(new Insets(48, 40, 40, 40));
        Stop[] heroStops = new Stop[] {
            new Stop(0, Color.web("#e8faf7")),
            new Stop(1, Theme.SURFACE)
        };
        LinearGradient heroGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, heroStops);
        hero.setBackground(new Background(new BackgroundFill(heroGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Label checkIcon = new Label("✅");
        checkIcon.setFont(Theme.bodyFont(64));

        Label heroTitle = new Label("Pembelian Berhasil!");
        heroTitle.setFont(Theme.bodyFontBold(28));
        heroTitle.setTextFill(Theme.TEXT_PRIMARY);

        Label heroSub = new Label("Terima kasih telah berbelanja di DIVERYU26");
        heroSub.setFont(Theme.bodyFont(15));
        heroSub.setTextFill(Theme.TEXT_SECONDARY);

        Label txId = new Label("ID Transaksi: #" + tx.getId().toUpperCase());
        txId.setFont(Theme.bodyFont(11));
        txId.setTextFill(Theme.TEXT_MUTED);
        txId.setBackground(new Background(new BackgroundFill(
            Color.web(Theme.ACCENT_HEX, 0.08), new CornerRadii(20), Insets.EMPTY
        )));
        txId.setPadding(new Insets(6, 14, 6, 14));
        txId.setBorder(new Border(new BorderStroke(
            Color.web(Theme.ACCENT_HEX, 0.25), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(1)
        )));

        hero.getChildren().addAll(checkIcon, heroTitle, heroSub, txId);

        // ── Detail order card ──
        String emoji = switch (product.getCategory()) {
            case "Sepatu"    -> "👟";
            case "Pakaian"   -> "👕";
            case "Aksesoris" -> "⌚";
            case "Tas"       -> "👜";
            case "Olahraga"  -> "⚽";
            default          -> "🛍";
        };

        DropShadow orderShadow = new DropShadow();
        orderShadow.setColor(Color.rgb(0, 0, 0, 0.07));
        orderShadow.setRadius(14);
        orderShadow.setOffsetY(4);

        VBox orderCard = new VBox(16);
        orderCard.setMaxWidth(560);
        orderCard.setPadding(new Insets(28));
        orderCard.setBackground(new Background(new BackgroundFill(Theme.WHITE, new CornerRadii(14), Insets.EMPTY)));
        orderCard.setEffect(orderShadow);

        Label orderTitle = UIFactory.subheading("📦  Detail Pesanan");

        Label prodEmojiLbl = new Label(emoji);
        prodEmojiLbl.setFont(Theme.bodyFont(36));
        VBox prodInfo = new VBox(4);
        Label prodName = new Label(product.getName());
        prodName.setFont(Theme.bodyFontBold(15));
        prodName.setTextFill(Theme.TEXT_PRIMARY);
        Label prodSeller = new Label("Dijual oleh: " + product.getSellerName());
        prodSeller.setFont(Theme.bodyFont(12));
        prodSeller.setTextFill(Theme.TEXT_MUTED);
        Label prodCat = UIFactory.badge(product.getCategory(), Theme.ACCENT);
        prodInfo.getChildren().addAll(prodName, prodSeller, prodCat);
        HBox prodRow = new HBox(16, prodEmojiLbl, prodInfo);
        prodRow.setAlignment(Pos.CENTER_LEFT);

        Separator sep1 = new Separator();

        // Rincian harga
        VBox priceDetails = new VBox(8);
        priceDetails.setBackground(new Background(new BackgroundFill(
            Color.web(Theme.ACCENT_HEX, 0.04), new CornerRadii(8), Insets.EMPTY
        )));
        priceDetails.setPadding(new Insets(14));
        HBox rowHarga   = detailRow("Harga satuan", product.getFormattedPrice());
        HBox rowJumlah  = detailRow("Jumlah", qty + " item");
        Separator sep2  = new Separator();
        HBox rowTotal   = detailRowBold("Total Pembayaran", String.format("Rp %,.0f", total));
        priceDetails.getChildren().addAll(rowHarga, rowJumlah, sep2, rowTotal);

        // Info pengiriman
        VBox shippingBox = new VBox(6);
        shippingBox.setBackground(new Background(new BackgroundFill(
            Color.rgb(255, 193, 7, 0.07), new CornerRadii(8), Insets.EMPTY
        )));
        shippingBox.setPadding(new Insets(14));
        shippingBox.setBorder(new Border(new BorderStroke(
            Color.rgb(255, 193, 7, 0.3), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1)
        )));
        Label shippingTitle = new Label("🚚  Estimasi Pengiriman");
        shippingTitle.setFont(Theme.bodyFontBold(13));
        shippingTitle.setTextFill(Theme.TEXT_PRIMARY);
        Label shippingInfo  = new Label("2–4 hari kerja • Pengiriman reguler");
        shippingInfo.setFont(Theme.bodyFont(12));
        shippingInfo.setTextFill(Theme.TEXT_SECONDARY);
        Label shippingDate  = new Label("Tanggal pesanan: " + tx.getFormattedDate());
        shippingDate.setFont(Theme.bodyFont(11));
        shippingDate.setTextFill(Theme.TEXT_MUTED);
        shippingBox.getChildren().addAll(shippingTitle, shippingInfo, shippingDate);

        orderCard.getChildren().addAll(orderTitle, UIFactory.divider(), prodRow, sep1, priceDetails, shippingBox);

        // ── Tombol aksi ──
        Button btnBelanjLagi = UIFactory.accentBtn("🛍  Lanjut Belanja");
        btnBelanjLagi.setPrefWidth(220);
        btnBelanjLagi.setPrefHeight(46);
        btnBelanjLagi.setOnAction(e -> show());

        Button btnRiwayat = UIFactory.outlineBtn("📋  Lihat Riwayat");
        btnRiwayat.setPrefWidth(220);
        btnRiwayat.setPrefHeight(46);
        btnRiwayat.setOnAction(e -> {
            BorderPane bp = (BorderPane) stage.getScene().getRoot();
            bp.setCenter(wrapScroll(buildRiwayatPanel()));
        });

        HBox btnRow = new HBox(16, btnBelanjLagi, btnRiwayat);
        btnRow.setAlignment(Pos.CENTER);
        btnRow.setPadding(new Insets(24, 0, 40, 0));

        VBox centerBox = new VBox(24, hero, orderCard, btnRow);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(0, 40, 40, 40));

        root.setCenter(wrapScroll(centerBox));

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("DIVERYU26 — Pembelian Berhasil");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setToValue(1);
        ft.play();
    }

    private HBox detailRow(String label, String value) {
        Label lbl = new Label(label);
        lbl.setFont(Theme.bodyFont(13));
        lbl.setTextFill(Theme.TEXT_SECONDARY);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.setFont(Theme.bodyFont(13));
        val.setTextFill(Theme.TEXT_PRIMARY);
        HBox row = new HBox(lbl, spacer, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private HBox detailRowBold(String label, String value) {
        Label lbl = new Label(label);
        lbl.setFont(Theme.bodyFontBold(14));
        lbl.setTextFill(Theme.TEXT_PRIMARY);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label val = new Label(value);
        val.setFont(Theme.bodyFontBold(16));
        val.setTextFill(Theme.ACCENT);
        HBox row = new HBox(lbl, spacer, val);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    // ── Panel Riwayat Pembelian ───────────────────────────────────────────────────
    private VBox buildRiwayatPanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));

        Label title    = UIFactory.heading("📋 Riwayat Pembelian");
        Label subtitle = UIFactory.bodyText("Semua transaksi yang pernah Anda lakukan.");

        List<Transaction> txList = store.getTransactionsByCustomer(customer.getId());

        double totalBelanja = txList.stream().mapToDouble(Transaction::getTotalPrice).sum();

        HBox stats = new HBox(16,
            UIFactory.statCard(String.valueOf(txList.size()), "Total Transaksi", Theme.ACCENT_HEX),
            UIFactory.statCard(String.format("Rp %,.0f", totalBelanja), "Total Belanja", Theme.SUCCESS_HEX)
        );

        if (txList.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            Label emptyIcon = new Label("🛒");
            emptyIcon.setFont(Theme.bodyFont(52));
            Label emptyMsg  = new Label("Belum ada transaksi");
            emptyMsg.setFont(Theme.bodyFont(16));
            emptyMsg.setTextFill(Theme.TEXT_MUTED);
            Label emptyHint = UIFactory.caption("Mulai belanja dan riwayat akan muncul di sini.");
            emptyBox.getChildren().addAll(emptyIcon, emptyMsg, emptyHint);
            panel.getChildren().addAll(title, subtitle, stats, emptyBox);
            return panel;
        }

        VBox txCards = new VBox(12);
        for (Transaction tx : txList) {
            txCards.getChildren().add(buildTransactionCard(tx));
        }

        panel.getChildren().addAll(title, subtitle, stats, txCards);
        return panel;
    }

    private VBox buildTransactionCard(Transaction tx) {
        DropShadow txShadow = new DropShadow();
        txShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        txShadow.setRadius(10);
        txShadow.setOffsetY(2);

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setBackground(new Background(new BackgroundFill(Theme.WHITE, new CornerRadii(10), Insets.EMPTY)));
        card.setEffect(txShadow);

        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label txIdLbl = new Label("#" + tx.getId().toUpperCase());
        txIdLbl.setFont(Theme.bodyFont(11));
        txIdLbl.setTextFill(Theme.TEXT_MUTED);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label dateLbl = new Label(tx.getFormattedDate());
        dateLbl.setFont(Theme.bodyFont(11));
        dateLbl.setTextFill(Theme.TEXT_MUTED);
        topRow.getChildren().addAll(txIdLbl, sp, dateLbl);

        Label prodName = new Label(tx.getProductName());
        prodName.setFont(Theme.bodyFontBold(14));
        prodName.setTextFill(Theme.TEXT_PRIMARY);

        HBox detailRow = new HBox(16);
        detailRow.setAlignment(Pos.CENTER_LEFT);
        Label qtyLbl   = UIFactory.badge(tx.getQuantity() + " item", Theme.ACCENT);
        Label totalLbl = new Label(tx.getFormattedTotal());
        totalLbl.setFont(Theme.bodyFontBold(15));
        totalLbl.setTextFill(Theme.ACCENT);
        Label statusBadge = UIFactory.badge("✓ Selesai", Theme.SUCCESS);
        detailRow.getChildren().addAll(qtyLbl, statusBadge, sp, totalLbl);

        card.getChildren().addAll(topRow, prodName, detailRow);
        return card;
    }

    private ScrollPane wrapScroll(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setBackground(new Background(new BackgroundFill(Theme.SURFACE, new CornerRadii(0), Insets.EMPTY)));
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    // ── Profile Panel ─────────────────────────────────────────────────────────────
    private VBox buildProfilePanel() {
        VBox panel = new VBox(24);
        panel.setPadding(new Insets(32));
        panel.setMaxWidth(600);

        Label title    = UIFactory.heading("Profil Saya");
        Label subtitle = UIFactory.bodyText("Ubah nama akun atau kata sandi Anda.");

        VBox infoCard = UIFactory.card(
            infoRow("👤", "Username", customer.getUsername()),
            infoRow("📧", "Email",    customer.getEmail()),
            infoRow("🔑", "Role",     "CUSTOMER")
        );

        // ── Change username card ──
        TextField newUsernameField = UIFactory.inputField("Username baru");
        newUsernameField.setText(customer.getUsername());
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
            if (store.updateUsername(customer.getId(), newName)) {
                customer.setUsername(newName);
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

        // ── Change password card ──
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

            if (!customer.getPassword().equals(current)) {
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
            store.updatePassword(customer.getId(), newPass);
            customer.setPassword(newPass);
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
