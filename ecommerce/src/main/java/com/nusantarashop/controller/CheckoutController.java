package com.nusantarashop.controller;

import com.nusantarashop.model.CartItem;
import com.nusantarashop.model.Order;
import com.nusantarashop.model.User;
import com.nusantarashop.service.OrderService;
import com.nusantarashop.util.SceneManager;
import com.nusantarashop.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class CheckoutController {

    @FXML private TextField txtRecipient, txtPhone;
    @FXML private TextArea txtAddress, txtNotes;
    @FXML private RadioButton rbSaldo, rbTransfer, rbCOD;
    @FXML private ToggleGroup paymentGroup;
    @FXML private Label lblSaldoInfo, lblSubtotal, lblShipping, lblTotal, lblError;
    @FXML private VBox itemSummaryBox;
    @FXML private Button btnOrder;

    private final SessionManager session = SessionManager.getInstance();
    private final OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        User user = session.getCurrentUser();
        if (user != null) {
            txtRecipient.setText(user.getFullName());
            txtPhone.setText(user.getPhone());
            txtAddress.setText(user.getAddress());
            lblSaldoInfo.setText("Saldo Anda: " + String.format("Rp %,.0f", user.getBalance()));
        }
        rbSaldo.setSelected(true);
        renderSummary();
    }

    private void renderSummary() {
        List<CartItem> items = session.getCart();
        boolean hasPhysical = items.stream().anyMatch(i -> !i.isDigital());
        double shipping = hasPhysical ? 15000 : 0;
        double subtotal = session.getCartTotal();

        itemSummaryBox.getChildren().clear();
        for (CartItem item : items) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            Label name = new Label(item.getProductName() + " x" + item.getQuantity());
            name.setStyle("-fx-font-size:12px;-fx-text-fill:#495057;-fx-wrap-text:true;");
            name.setMaxWidth(160);
            name.setWrapText(true);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label price = new Label(item.getFormattedSubtotal());
            price.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#1A1A2E;");
            row.getChildren().addAll(name, spacer, price);
            itemSummaryBox.getChildren().add(row);
        }

        lblSubtotal.setText(String.format("Rp %,.0f", subtotal));
        lblShipping.setText(shipping > 0 ? String.format("Rp %,.0f", shipping) : "Gratis");
        lblTotal.setText(String.format("Rp %,.0f", subtotal + shipping));
    }

    @FXML
    private void handlePlaceOrder() {
        hideError();
        String address = (txtRecipient.getText().trim().isEmpty() ? "" : txtRecipient.getText().trim() + "\n")
                + txtAddress.getText().trim();
        if (txtAddress.getText().isBlank()) {
            showError("Alamat pengiriman wajib diisi.");
            return;
        }
        if (paymentGroup.getSelectedToggle() == null) {
            showError("Pilih metode pembayaran terlebih dahulu.");
            return;
        }

        String method = paymentGroup.getSelectedToggle().getUserData().toString();
        btnOrder.setDisable(true);
        btnOrder.setText("Memproses...");

        OrderService.CheckoutResult result = orderService.checkout(address, method, txtNotes.getText());

        if (result.success()) {
            showSuccessDialog(result.order());
        } else {
            showError(result.message());
            btnOrder.setDisable(false);
            btnOrder.setText("✅ Buat Pesanan");
        }
    }

    private void showSuccessDialog(Order order) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pesanan Berhasil! 🎉");
        alert.setHeaderText("Terima kasih telah berbelanja!");
        alert.setContentText(
            "No. Pesanan: " + order.getOrderNumber() + "\n" +
            "Total: " + order.getFormattedTotal() + "\n" +
            "Status: " + order.getStatus().getDisplayName() + "\n\n" +
            "Pesanan Anda akan segera diproses."
        );
        alert.showAndWait();
        SceneManager.getInstance().switchTo(SceneManager.SceneName.ORDER_HISTORY);
    }

    @FXML
    private void handleBack() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.CART);
    }

    private void showError(String msg) {
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }

    private void hideError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
}
