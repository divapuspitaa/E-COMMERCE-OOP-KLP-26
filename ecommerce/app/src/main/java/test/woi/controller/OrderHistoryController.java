package test.woi.controller;

import test.woi.model.CartItem;
import test.woi.model.Order;
import test.woi.service.OrderService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistoryController {

    @FXML private VBox orderList;
    @FXML private Label lblEmpty;
    @FXML private ComboBox<String> cmbFilter;

    private final OrderService orderService = new OrderService();
    private List<Order> allOrders;

    @FXML
    public void initialize() {
        cmbFilter.getItems().add("Semua Status");
        for (Order.Status s : Order.Status.values()) cmbFilter.getItems().add(s.getDisplayName());
        cmbFilter.setValue("Semua Status");

        loadOrders();
    }

    private void loadOrders() {
        allOrders = orderService.getMyOrders();
        renderOrders(allOrders);
    }

    @FXML
    private void handleFilter() {
        String sel = cmbFilter.getValue();
        if (sel == null || "Semua Status".equals(sel)) {
            renderOrders(allOrders);
            return;
        }
        List<Order> filtered = allOrders.stream()
                .filter(o -> o.getStatus().getDisplayName().equals(sel))
                .collect(Collectors.toList());
        renderOrders(filtered);
    }

    private void renderOrders(List<Order> orders) {
        orderList.getChildren().clear();
        boolean empty = orders == null || orders.isEmpty();
        lblEmpty.setVisible(empty);
        lblEmpty.setManaged(empty);
        if (empty) return;

        for (Order order : orders) {
            orderList.getChildren().add(buildOrderCard(order));
        }
    }

    private VBox buildOrderCard(Order order) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),8,0,0,2);");

        // Header
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 18, 14, 18));
        header.setStyle("-fx-background-color:#F8F9FA;-fx-background-radius:12 12 0 0;");

        VBox leftInfo = new VBox(3);
        Label orderNum = new Label("📦 " + order.getOrderNumber());
        orderNum.setStyle("-fx-font-weight:bold;-fx-font-size:13px;-fx-text-fill:#1A1A2E;");
        Label date = new Label(order.getCreatedAt() != null
                ? order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                : "-");
        date.setStyle("-fx-font-size:11px;-fx-text-fill:#6C757D;");
        leftInfo.getChildren().addAll(orderNum, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status chip
        Label statusChip = new Label(order.getStatus().getDisplayName());
        String statusStyle = getStatusStyle(order.getStatus());
        statusChip.setStyle(statusStyle + "-fx-background-radius:12;-fx-padding:4 12;-fx-font-size:11px;-fx-font-weight:bold;");

        Label total = new Label(order.getFormattedTotal());
        total.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#C0392B;");

        header.getChildren().addAll(leftInfo, spacer, statusChip, total);

        // Items
        VBox body = new VBox(6);
        body.setPadding(new Insets(14, 18, 14, 18));
        for (CartItem item : order.getItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            Label icon = new Label(item.isDigital() ? "📱" : "📦");
            icon.setStyle("-fx-font-size:16px;");
            Label itemName = new Label(item.getProductName() + " × " + item.getQuantity());
            itemName.setStyle("-fx-font-size:12px;-fx-text-fill:#495057;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            Label itemPrice = new Label(item.getFormattedSubtotal());
            itemPrice.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#1A1A2E;");
            itemRow.getChildren().addAll(icon, itemName, sp, itemPrice);
            body.getChildren().add(itemRow);
        }

        // Footer
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 18, 14, 18));
        footer.setStyle("-fx-border-color:#F0F2F5;-fx-border-width:1 0 0 0;");

        Label payMethod = new Label("💳 " + order.getPaymentMethod());
        payMethod.setStyle("-fx-font-size:11px;-fx-text-fill:#6C757D;");
        Label shipping = new Label("🚚 Ongkir: " + order.getFormattedShipping());
        shipping.setStyle("-fx-font-size:11px;-fx-text-fill:#6C757D;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Label itemCount = new Label(order.getItemCount() + " item");
        itemCount.setStyle("-fx-font-size:11px;-fx-text-fill:#6C757D;");

        footer.getChildren().addAll(payMethod, shipping, sp2, itemCount);

        card.getChildren().addAll(header, body, footer);
        return card;
    }

    private String getStatusStyle(Order.Status status) {
        return switch (status) {
            case MENUNGGU_PEMBAYARAN -> "-fx-background-color:#FFF3CD;-fx-text-fill:#856404;";
            case DIBAYAR            -> "-fx-background-color:#CCE5FF;-fx-text-fill:#004085;";
            case DIPROSES           -> "-fx-background-color:#E2D9F3;-fx-text-fill:#4A235A;";
            case DIKIRIM            -> "-fx-background-color:#D1ECF1;-fx-text-fill:#0C5460;";
            case SELESAI            -> "-fx-background-color:#D4EDDA;-fx-text-fill:#155724;";
            case DIBATALKAN         -> "-fx-background-color:#F8D7DA;-fx-text-fill:#721C24;";
        };
    }

    @FXML private void handleHome()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.HOME); }
    @FXML private void handleCart()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.CART); }
    @FXML private void handleProfile() { SceneManager.getInstance().switchTo(SceneManager.SceneName.PROFILE); }
}
