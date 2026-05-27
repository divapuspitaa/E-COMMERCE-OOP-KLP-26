package com.nusantarashop.controller;

import com.nusantarashop.model.CartItem;
import com.nusantarashop.util.SceneManager;
import com.nusantarashop.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class CartController {

    @FXML private VBox cartList;
    @FXML private Label lblItemCount, lblSubtotal, lblShipping, lblDiscount, lblTotal, lblEmpty;

    private final SessionManager session = SessionManager.getInstance();

    @FXML
    public void initialize() {
        renderCart();
    }

    private void renderCart() {
        List<CartItem> items = session.getCart();
        cartList.getChildren().clear();

        boolean hasPhysical = items.stream().anyMatch(i -> !i.isDigital());
        double shippingCost = hasPhysical ? 15000 : 0;
        double subtotal = session.getCartTotal();
        double discount = 0;

        // Calculate discount (original price - discounted price)
        for (CartItem item : items) {
            double origTotal = item.getProductPrice() * item.getQuantity();
            double discTotal = item.getDiscountedPrice() * item.getQuantity();
            discount += origTotal - discTotal;
        }

        boolean empty = items.isEmpty();
        lblEmpty.setVisible(empty);
        lblEmpty.setManaged(empty);

        for (CartItem item : items) {
            cartList.getChildren().add(buildCartRow(item));
        }

        lblItemCount.setText(session.getCartCount() + " item");
        lblSubtotal.setText(String.format("Rp %,.0f", subtotal));
        lblShipping.setText(shippingCost > 0 ? String.format("Rp %,.0f", shippingCost) : "Gratis");
        lblDiscount.setText(discount > 0 ? "-" + String.format("Rp %,.0f", discount) : "Rp 0");
        lblTotal.setText(String.format("Rp %,.0f", subtotal + shippingCost));
    }

    private HBox buildCartRow(CartItem item) {
        HBox row = new HBox(14);
        row.getStyleClass().add("cart-item-row");
        row.setAlignment(Pos.CENTER_LEFT);

        // Image placeholder
        StackPane imgPane = new StackPane();
        imgPane.setMinSize(70, 70);
        imgPane.setMaxSize(70, 70);
        imgPane.setStyle("-fx-background-color:#F0F2F5;-fx-background-radius:8;");
        Label img = new Label(item.isDigital() ? "📱" : "📦");
        img.setStyle("-fx-font-size:28px;");
        imgPane.getChildren().add(img);

        // Info
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(item.getProductName());
        name.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#1A1A2E;-fx-wrap-text:true;");
        name.setWrapText(true);

        Label type = new Label(item.isDigital() ? "🔵 Produk Digital" : "📦 Produk Fisik");
        type.setStyle("-fx-font-size:11px;-fx-text-fill:#6C757D;");

        Label price = new Label(item.getFormattedPrice() + " / item");
        price.setStyle("-fx-font-size:12px;-fx-text-fill:#C0392B;");

        info.getChildren().addAll(name, type, price);

        // Qty controls
        HBox qtyBox = new HBox(8);
        qtyBox.setAlignment(Pos.CENTER);
        Button btnMinus = new Button("−");
        btnMinus.getStyleClass().add("btn-outline");
        btnMinus.setStyle("-fx-min-width:30;-fx-min-height:30;-fx-padding:0;");

        Label lblQty = new Label(String.valueOf(item.getQuantity()));
        lblQty.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-min-width:30;-fx-alignment:center;");

        Button btnPlus = new Button("+");
        btnPlus.getStyleClass().add("btn-primary");
        btnPlus.setStyle("-fx-min-width:30;-fx-min-height:30;-fx-padding:0;");

        btnMinus.setOnAction(e -> {
            session.updateCartItemQty(item.getProductId(), item.getQuantity() - 1);
            renderCart();
        });
        btnPlus.setOnAction(e -> {
            session.updateCartItemQty(item.getProductId(), item.getQuantity() + 1);
            renderCart();
        });
        qtyBox.getChildren().addAll(btnMinus, lblQty, btnPlus);

        // Subtotal
        Label subtotalLbl = new Label(item.getFormattedSubtotal());
        subtotalLbl.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#C0392B;-fx-min-width:100;-fx-alignment:center-right;");

        // Remove
        Button btnRemove = new Button("✕");
        btnRemove.getStyleClass().add("btn-icon");
        btnRemove.setStyle("-fx-text-fill:#C0392B;");
        btnRemove.setOnAction(e -> {
            session.removeFromCart(item.getProductId());
            renderCart();
        });

        row.getChildren().addAll(imgPane, info, qtyBox, subtotalLbl, btnRemove);
        return row;
    }

    @FXML
    private void handleCheckout() {
        if (session.getCart().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Keranjang masih kosong!").showAndWait();
            return;
        }
        SceneManager.getInstance().switchTo(SceneManager.SceneName.CHECKOUT);
    }

    @FXML
    private void handleClearCart() {
        if (!session.getCart().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Kosongkan semua item dari keranjang?");
            alert.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    session.clearCart();
                    renderCart();
                }
            });
        }
    }

    @FXML
    private void handleContinue() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME);
    }

    @FXML
    private void handleProfile() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.PROFILE);
    }
}
