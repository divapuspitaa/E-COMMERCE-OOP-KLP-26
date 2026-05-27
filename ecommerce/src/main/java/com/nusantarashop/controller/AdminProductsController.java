package com.nusantarashop.controller;

import com.nusantarashop.model.PhysicalProduct;
import com.nusantarashop.model.Product;
import com.nusantarashop.service.ProductService;
import com.nusantarashop.util.SceneManager;
import com.nusantarashop.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class AdminProductsController {

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, String> colName, colCategory, colType, colPrice,
            colStock, colRating, colActive, colActions;
    @FXML private TextField txtSearch;
    @FXML private Label lblCount;

    private final ProductService productService = new ProductService();
    private ObservableList<Product> productData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadProducts();
    }

    private void setupTable() {
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colCategory.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCategory().getDisplayName()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductType()));
        colPrice.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFormattedDiscountedPrice()));
        colStock.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStock())));
        colRating.setCellValueFactory(d -> new SimpleStringProperty(String.format("⭐ %.1f", d.getValue().getRating())));
        colActive.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isActive() ? "✅ Aktif" : "❌ Nonaktif"));

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

        colStock.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                int stock = Integer.parseInt(item);
                setStyle(stock == 0 ? "-fx-text-fill:#C0392B;-fx-font-weight:bold;"
                    : stock < 5 ? "-fx-text-fill:#E67E22;-fx-font-weight:bold;"
                    : "-fx-text-fill:#155724;");
            }
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            final Button btnEdit = new Button("✏️ Edit");
            final Button btnToggle = new Button("🔄");
            {
                btnEdit.getStyleClass().add("btn-outline");
                btnEdit.setStyle("-fx-padding:4 10;-fx-font-size:11px;");
                btnToggle.getStyleClass().add("btn-ghost");
                btnToggle.setStyle("-fx-padding:4 8;-fx-font-size:11px;");
            }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Product p = getTableView().getItems().get(getIndex());
                btnEdit.setOnAction(e -> showEditDialog(p));
                btnToggle.setText(p.isActive() ? "🔴" : "🟢");
                btnToggle.setOnAction(e -> {
                    p.setActive(!p.isActive());
                    productService.updateProduct(p);
                    tableProducts.refresh();
                });
                HBox box = new HBox(6, btnEdit, btnToggle);
                box.setPadding(new Insets(2));
                setGraphic(box);
            }
        });

        tableProducts.setItems(productData);
    }

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();
        productData.setAll(products);
        lblCount.setText(products.size() + " produk");
    }

    @FXML
    private void handleSearch() {
        String kw = txtSearch.getText().trim();
        List<Product> results = kw.isEmpty()
            ? productService.getAllProducts()
            : productService.searchProducts(kw);
        productData.setAll(results);
        lblCount.setText(results.size() + " produk");
    }

    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }

    private void showProductDialog(Product existing) {
        boolean isEdit = existing != null;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Produk" : "Tambah Produk Baru");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setPadding(new Insets(20));
        grid.setMinWidth(480);

        TextField fName = new TextField(isEdit ? existing.getName() : "");
        TextArea fDesc = new TextArea(isEdit ? existing.getDescription() : "");
        fDesc.setPrefRowCount(2);
        TextField fPrice = new TextField(isEdit ? String.valueOf(existing.getPrice()) : "");
        TextField fStock = new TextField(isEdit ? String.valueOf(existing.getStock()) : "");
        ComboBox<Product.Category> fCat = new ComboBox<>(FXCollections.observableArrayList(Product.Category.values()));
        fCat.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Product.Category c, boolean empty) {
                super.updateItem(c, empty); setText(empty || c == null ? null : c.getDisplayName());
            }
        });
        fCat.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Product.Category c, boolean empty) {
                super.updateItem(c, empty); setText(empty || c == null ? null : c.getDisplayName());
            }
        });
        if (isEdit) fCat.setValue(existing.getCategory());
        TextField fDiscount = new TextField(isEdit && existing instanceof PhysicalProduct pp
            ? String.valueOf(pp.getDiscountPct()) : "0");
        TextField fWeight = new TextField(isEdit && existing instanceof PhysicalProduct pp
            ? String.valueOf(pp.getWeight()) : "300");

        int row = 0;
        grid.add(new Label("Nama Produk *"), 0, row); grid.add(fName, 1, row++);
        grid.add(new Label("Deskripsi"), 0, row); grid.add(fDesc, 1, row++);
        grid.add(new Label("Harga (Rp) *"), 0, row); grid.add(fPrice, 1, row++);
        grid.add(new Label("Stok *"), 0, row); grid.add(fStock, 1, row++);
        grid.add(new Label("Kategori *"), 0, row); grid.add(fCat, 1, row++);
        grid.add(new Label("Diskon (%)"), 0, row); grid.add(fDiscount, 1, row++);
        grid.add(new Label("Berat (gram)"), 0, row); grid.add(fWeight, 1, row++);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn != ButtonType.OK) return;
            try {
                PhysicalProduct p = isEdit && existing instanceof PhysicalProduct pp ? pp : new PhysicalProduct();
                p.setName(fName.getText().trim());
                p.setDescription(fDesc.getText().trim());
                p.setPrice(Double.parseDouble(fPrice.getText().trim()));
                p.setStock(Integer.parseInt(fStock.getText().trim()));
                p.setCategory(fCat.getValue() != null ? fCat.getValue() : Product.Category.LAINNYA);
                p.setDiscountPct(Double.parseDouble(fDiscount.getText().trim()));
                p.setWeight(Double.parseDouble(fWeight.getText().trim()));
                p.setSellerId(SessionManager.getInstance().getCurrentUser().getId());
                p.setActive(true);

                if (isEdit) productService.updateProduct(p);
                else productService.saveProduct(p);

                loadProducts();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Isi semua field numerik dengan benar.").showAndWait();
            }
        });
    }

    private void showEditDialog(Product p) { showProductDialog(p); }

    @FXML private void handleDashboard() { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD); }
    @FXML private void handleOrders()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_ORDERS); }
    @FXML private void handleUsers()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_USERS); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
    }
}
