package proyek.p.customer;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.Customer;
import proyek.p.model.DataStore;
import proyek.p.model.Product;
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
        root.setTop(buildTopBar());
        root.setCenter(wrapScroll(buildShopPanel()));
        root.setStyle("-fx-background-color: " + Theme.SURFACE + ";");

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.setTitle("ZALORA — Belanja");

        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    // ── Top Bar with search ───────────────────────────────────────────────────────
    private VBox buildTopBar() {
        HBox navbar = UIFactory.navbar("Belanja", customer.getUsername(), "CUSTOMER", App::showLogin);

        // Category tabs under nav
        HBox cats = new HBox(0);
        cats.setStyle("-fx-background-color: " + Theme.WHITE + "; -fx-border-color: " + Theme.BORDER + "; -fx-border-width: 0 0 1 0;");
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

        // Search bar row
        HBox searchRow = new HBox(12);
        searchRow.setPadding(new Insets(12, 24, 12, 24));
        searchRow.setStyle("-fx-background-color: " + Theme.WHITE + ";");
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
        String base = "-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-cursor: hand; -fx-background-radius: 0;";
        String sel  = "-fx-background-color: transparent; -fx-font-size: 13; -fx-text-fill: " + Theme.BLACK + "; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 0; -fx-border-color: transparent transparent " + Theme.BLACK + " transparent; -fx-border-width: 0 0 2 0;";
        btn.setStyle(selected ? sel : base);
        btn.selectedProperty().addListener((obs, o, nw) -> btn.setStyle(nw ? sel : base));
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
            empty.setStyle("-fx-font-size: 16; -fx-text-fill: " + Theme.TEXT_MUTED + "; -fx-padding: 40;");
            grid.getChildren().add(empty);
            return grid;
        }

        for (Product p : products) {
            grid.getChildren().add(buildProductCard(p));
        }
        return grid;
    }

    private VBox buildProductCard(Product product) {
        // Color block as "image"
        String[] colors = { "#E8F4F0", "#FFF0F0", "#F0F0FF", "#FFFAF0", "#F0FFF4", "#FFF5E0" };
        int colorIdx = Math.abs(product.getId().hashCode()) % colors.length;
        String bgColor = colors[colorIdx];

        // Emoji based on category
        String emoji = switch (product.getCategory()) {
            case "Sepatu"     -> "👟";
            case "Pakaian"    -> "👕";
            case "Aksesoris"  -> "⌚";
            case "Tas"        -> "👜";
            case "Olahraga"   -> "⚽";
            default           -> "🛍";
        };

        // Image placeholder
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(200, 160);
        imgBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 8 8 0 0;");
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 52;");
        imgBox.getChildren().add(emojiLbl);

        // Category badge
        Label catBadge = UIFactory.badge(product.getCategory(), Theme.ACCENT);

        // Product name
        Label nameLbl = new Label(product.getName());
        nameLbl.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-wrap-text: true;");
        nameLbl.setMaxWidth(190);
        nameLbl.setWrapText(true);

        // Seller
        Label sellerLbl = new Label("oleh " + product.getSellerName());
        sellerLbl.setStyle("-fx-font-size: 11; -fx-text-fill: " + Theme.TEXT_MUTED + ";");

        // Price
        Label priceLbl = new Label(product.getFormattedPrice());
        priceLbl.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        // Stock indicator
        Label stockLbl = UIFactory.badge("Stok: " + product.getStock(), product.getStock() > 5 ? Theme.SUCCESS : Theme.WARNING);

        HBox priceRow = new HBox(8, priceLbl);
        priceRow.setAlignment(Pos.CENTER_LEFT);

        // Buy button
        Button buyBtn = UIFactory.accentBtn("Beli Sekarang");
        buyBtn.setPrefWidth(200);
        buyBtn.setOnAction(e -> showProductDetail(product));

        Button detailBtn = UIFactory.outlineBtn("Detail");
        detailBtn.setPrefWidth(200);
        detailBtn.setOnAction(e -> showProductDetail(product));

        VBox info = new VBox(6, catBadge, nameLbl, sellerLbl, priceRow, stockLbl, detailBtn);
        info.setPadding(new Insets(14));

        VBox card = new VBox(0, imgBox, info);
        card.setPrefWidth(218);
        card.setStyle(
            "-fx-background-color: " + Theme.WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);" +
            "-fx-cursor: hand;"
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: " + Theme.WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 20, 0, 0, 6);" +
            "-fx-cursor: hand;" +
            "-fx-translate-y: -3;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: " + Theme.WHITE + ";" +
            "-fx-background-radius: 12;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 10, 0, 0, 3);" +
            "-fx-cursor: hand;"
        ));
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

        // Image area
        StackPane imgArea = new StackPane();
        imgArea.setPrefSize(460, 220);
        imgArea.setStyle("-fx-background-color: " + colors[colorIdx] + ";");
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size: 80;");
        imgArea.getChildren().add(emojiLbl);

        // Info
        Label catBadge  = UIFactory.badge(product.getCategory(), Theme.ACCENT);
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        nameLabel.setWrapText(true);
        Label sellerLbl = UIFactory.bodyText("Dijual oleh: " + product.getSellerName());
        Label priceLbl  = new Label(product.getFormattedPrice());
        priceLbl.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        Label stockLbl  = UIFactory.badge("Stok tersedia: " + product.getStock(), Theme.SUCCESS);
        Label descTitle = UIFactory.subheading("Deskripsi");
        Label descLbl   = new Label(product.getDescription());
        descLbl.setStyle("-fx-font-size: 13; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        descLbl.setWrapText(true);

        Spinner<Integer> qtySpinner = new Spinner<>(1, product.getStock(), 1);
        qtySpinner.setStyle(Theme.INPUT_STYLE);
        qtySpinner.setPrefWidth(120);

        Button buyBtn = UIFactory.accentBtn("🛒  Beli Sekarang");
        buyBtn.setPrefWidth(280);
        buyBtn.setPrefHeight(48);
        buyBtn.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            dialog.close();
            UIFactory.showAlert(
                "Pembelian Berhasil! 🎉",
                qty + "x " + product.getName() + "\n" +
                "Total: " + String.format("Rp %,.0f", product.getPrice() * qty) + "\n\n" +
                "Terima kasih telah berbelanja di ZALORA!",
                Alert.AlertType.INFORMATION
            );
        });

        VBox info = new VBox(12,
            catBadge, nameLabel, sellerLbl, UIFactory.divider(),
            priceLbl, stockLbl, UIFactory.divider(),
            descTitle, descLbl, UIFactory.divider(),
            new HBox(12, UIFactory.formLabel("Jumlah:"), qtySpinner),
            buyBtn
        );
        info.setPadding(new Insets(20));
        info.setStyle("-fx-background-color: " + Theme.WHITE + ";");

        VBox root = new VBox(imgArea, info);
        dialog.setScene(new Scene(root, 460, 640));
        dialog.showAndWait();
    }

    private ScrollPane wrapScroll(javafx.scene.Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + Theme.SURFACE + "; -fx-background: " + Theme.SURFACE + ";");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }
}
