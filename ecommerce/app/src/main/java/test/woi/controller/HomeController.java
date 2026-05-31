package test.woi.controller;

import test.woi.model.*;
import test.woi.service.ProductService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * HomeController - halaman utama belanja.
 * Menampilkan produk, kategori, dan search.
 */
public class HomeController {

    @FXML private Label lblWelcome, lblBalance, lblCartCount, lblProductCount;
    @FXML private Label lblSectionTitle, lblNoProduct;
    @FXML private Button btnCart;
    @FXML private TextField txtSearch;
    @FXML private FlowPane productGrid;
    @FXML private HBox categoryBox;
    @FXML private ComboBox<String> cmbSort;

    private final ProductService productService = new ProductService();
    private final SessionManager session = SessionManager.getInstance();

    private List<Product> allProducts = new ArrayList<>();
    private String activeCategory = "SEMUA";
    private Button activeCategoryBtn;

    @FXML
    public void initialize() {
        setupSort();
        setupCategoryChips();
        updateUserInfo();
        loadProducts();
    }

    private void setupSort() {
        cmbSort.getItems().addAll("Terbaru", "Harga Terendah", "Harga Tertinggi", "Rating Terbaik");
        cmbSort.setValue("Terbaru");
    }

    private void setupCategoryChips() {
        categoryBox.getChildren().clear();

        // "Semua" chip
        addCategoryChip("Semua", "SEMUA");

        for (Product.Category cat : Product.Category.values()) {
            addCategoryChip(cat.getDisplayName(), cat.name());
        }
    }

    private void addCategoryChip(String label, String value) {
        Button chip = new Button(label);
        chip.getStyleClass().add("category-chip");
        chip.setOnAction(e -> {
            activeCategory = value;
            if (activeCategoryBtn != null) {
                activeCategoryBtn.getStyleClass().removeAll("category-chip-active");
                activeCategoryBtn.getStyleClass().add("category-chip");
            }
            chip.getStyleClass().removeAll("category-chip");
            chip.getStyleClass().add("category-chip-active");
            activeCategoryBtn = chip;
            applyFilters();
        });

        if ("SEMUA".equals(value)) {
            chip.getStyleClass().removeAll("category-chip");
            chip.getStyleClass().add("category-chip-active");
            activeCategoryBtn = chip;
        }
        categoryBox.getChildren().add(chip);
    }

    private void updateUserInfo() {
        User user = session.getCurrentUser();
        if (user != null) {
            lblWelcome.setText("Halo, " + user.getDisplayName() + "! 👋");
            lblBalance.setText("Saldo: Rp " + String.format("%,.0f", user.getBalance()));
        }
        updateCartBadge();
    }

    private void updateCartBadge() {
        int count = session.getCartCount();
        lblCartCount.setText(String.valueOf(count));
        lblCartCount.setVisible(count > 0);
        lblCartCount.setManaged(count > 0);
        btnCart.setText("🛒 Keranjang" + (count > 0 ? " (" + count + ")" : ""));
    }

    private void loadProducts() {
        new Thread(() -> {
            List<Product> products = productService.getAllProducts();
            Platform.runLater(() -> {
                allProducts = products;
                lblProductCount.setText(products.size() + " produk tersedia");
                applyFilters();
            });
        }).start();
    }

    private void applyFilters() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();

        List<Product> filtered = allProducts.stream()
            .filter(p -> {
                boolean matchCat = "SEMUA".equals(activeCategory) || p.getCategory().name().equals(activeCategory);
                boolean matchSearch = keyword.isEmpty()
                    || p.getName().toLowerCase().contains(keyword)
                    || (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword));
                return matchCat && matchSearch;
            })
            .collect(java.util.stream.Collectors.toList());

        // Sort
        String sortVal = cmbSort.getValue();
        if ("Harga Terendah".equals(sortVal))
            filtered.sort(Comparator.comparingDouble(Product::getDiscountedPrice));
        else if ("Harga Tertinggi".equals(sortVal))
            filtered.sort(Comparator.comparingDouble(Product::getDiscountedPrice).reversed());
        else if ("Rating Terbaik".equals(sortVal))
            filtered.sort(Comparator.comparingDouble(Product::getRating).reversed());

        lblSectionTitle.setText(keyword.isEmpty() ? getCategoryLabel() : "Hasil Pencarian: \"" + keyword + "\"");
        renderProducts(filtered);
    }

    private String getCategoryLabel() {
        if ("SEMUA".equals(activeCategory)) return "Semua Produk";
        try { return Product.Category.valueOf(activeCategory).getDisplayName(); }
        catch (Exception e) { return "Produk"; }
    }

    private void renderProducts(List<Product> products) {
        productGrid.getChildren().clear();
        lblNoProduct.setVisible(products.isEmpty());
        lblNoProduct.setManaged(products.isEmpty());

        for (Product p : products) {
            productGrid.getChildren().add(buildProductCard(p));
        }
    }

    private VBox buildProductCard(Product product) {
        VBox card = new VBox(0);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(210);
        card.setMaxWidth(210);

        // Image Placeholder with emoji icon based on category
        StackPane imgPane = new StackPane();
        imgPane.getStyleClass().add("product-image-placeholder");
        imgPane.setPrefHeight(160);

        String emoji = getCategoryEmoji(product.getCategory());
        Label emojiLbl = new Label(emoji);
        emojiLbl.setStyle("-fx-font-size:56px;");
        imgPane.getChildren().add(emojiLbl);

        // Badges overlay
        HBox badges = new HBox(6);
        badges.setAlignment(Pos.TOP_LEFT);
        badges.setPadding(new Insets(8));
        StackPane.setAlignment(badges, Pos.TOP_LEFT);

        if (product instanceof PhysicalProduct pp && pp.hasDiscount()) {
            Label discBadge = new Label(pp.getDiscountLabel());
            discBadge.getStyleClass().add("discount-badge");
            badges.getChildren().add(discBadge);
        }
        if (product instanceof DigitalProduct) {
            Label digBadge = new Label("Digital");
            digBadge.getStyleClass().add("digital-badge");
            badges.getChildren().add(digBadge);
        }
        if (!badges.getChildren().isEmpty()) {
            imgPane.getChildren().add(badges);
        }

        // Card Body
        VBox body = new VBox(6);
        body.setPadding(new Insets(12));

        Label lblName = new Label(product.getName());
        lblName.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#1A1A2E;-fx-wrap-text:true;");
        lblName.setMaxWidth(186);
        lblName.setWrapText(true);
        lblName.setMaxHeight(40);

        // Rating
        HBox ratingBox = new HBox(4);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        Label stars = new Label(getStarString(product.getRating()));
        stars.setStyle("-fx-text-fill:#F39C12;-fx-font-size:11px;");
        Label ratingVal = new Label(String.format("%.1f (%d)", product.getRating(), product.getTotalReviews()));
        ratingVal.setStyle("-fx-font-size:10px;-fx-text-fill:#6C757D;");
        ratingBox.getChildren().addAll(stars, ratingVal);

        // Price
        VBox priceBox = new VBox(2);
        if (product instanceof PhysicalProduct pp && pp.hasDiscount()) {
            Label origPrice = new Label(product.getFormattedPrice());
            origPrice.getStyleClass().add("text-price-original");
            Label discPrice = new Label(product.getFormattedDiscountedPrice());
            discPrice.getStyleClass().add("text-price");
            priceBox.getChildren().addAll(origPrice, discPrice);
        } else {
            Label price = new Label(product.getFormattedDiscountedPrice());
            price.getStyleClass().add("text-price");
            priceBox.getChildren().add(price);
        }

        // Stock
        Label stockLbl = new Label(product.isInStock() ? "Stok: " + product.getStock() : "Habis");
        stockLbl.setStyle(product.isInStock()
            ? "-fx-font-size:10px;-fx-text-fill:#6C757D;"
            : "-fx-font-size:10px;-fx-text-fill:#C0392B;-fx-font-weight:bold;");

        // Add to Cart
        Button btnAdd = new Button(session.isInCart(product.getId()) ? "✓ Di Keranjang" : "🛒 Tambah");
        btnAdd.getStyleClass().add(session.isInCart(product.getId()) ? "btn-success" : "btn-primary");
        btnAdd.setMaxWidth(Double.MAX_VALUE);
        btnAdd.setDisable(!product.isInStock());
        btnAdd.setOnAction(e -> handleAddToCart(product, btnAdd));

        body.getChildren().addAll(lblName, ratingBox, priceBox, stockLbl, btnAdd);
        card.getChildren().addAll(imgPane, body);

        // Click card to detail (future enhancement)
        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) handleAddToCart(product, btnAdd);
        });

        return card;
    }

    private void handleAddToCart(Product product, Button btnAdd) {
        if (!product.isInStock()) return;
        CartItem item = new CartItem(product, 1);
        session.addToCart(item);
        btnAdd.getStyleClass().removeAll("btn-primary");
        btnAdd.getStyleClass().add("btn-success");
        btnAdd.setText("✓ Di Keranjang");
        updateCartBadge();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleSort() {
        applyFilters();
    }

    @FXML
    private void handleCart() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.CART);
    }

    @FXML
    private void handleOrders() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.ORDER_HISTORY);
    }

    @FXML
    private void handleProfile() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.PROFILE);
    }

    @FXML
    private void handleHome() {
        loadProducts();
        updateUserInfo();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Keluar");
        alert.setHeaderText(null);
        alert.setContentText("Apakah Anda yakin ingin keluar?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                session.logout();
                SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
            }
        });
    }

    private String getStarString(double rating) {
        int full = (int) rating;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < full ? "★" : "☆");
        return sb.toString();
    }

    private String getCategoryEmoji(Product.Category cat) {
        return switch (cat) {
            case ELEKTRONIK -> "💻";
            case FASHION -> "👗";
            case MAKANAN -> "🍜";
            case KECANTIKAN -> "💄";
            case OLAHRAGA -> "⚽";
            case BUKU -> "📚";
            case RUMAH -> "🏠";
            case OTOMOTIF -> "🚗";
            default -> "🛍";
        };
    }
}
