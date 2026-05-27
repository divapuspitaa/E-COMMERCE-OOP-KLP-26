package test.woi.controller;

import test.woi.model.Order;
import test.woi.service.OrderService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminOrdersController {

    @FXML private TableView<Order> tableOrders;
    @FXML private TableColumn<Order, String> colOrderNum, colCustomer, colTotal,
            colPayment, colStatus, colDate, colAction;
    @FXML private ComboBox<String> cmbFilter;
    @FXML private Label lblCount;

    private final OrderService orderService = new OrderService();
    private ObservableList<Order> orderData = FXCollections.observableArrayList();
    private List<Order> allOrders;

    @FXML
    public void initialize() {
        cmbFilter.getItems().add("Semua Status");
        for (Order.Status s : Order.Status.values()) cmbFilter.getItems().add(s.getDisplayName());
        cmbFilter.setValue("Semua Status");

        setupTable();
        loadOrders();
    }

    private void setupTable() {
        colOrderNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrderNumber()));
        colCustomer.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFormattedTotal()));
        colPayment.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPaymentMethod()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getDisplayName()));
        colDate.setCellValueFactory(d -> {
            String dt = d.getValue().getCreatedAt() != null
                ? d.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))
                : "-";
            return new SimpleStringProperty(dt);
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                Order.Status s = getTableView().getItems().get(getIndex()).getStatus();
                String color = switch (s) {
                    case SELESAI   -> "#155724";
                    case DIBATALKAN-> "#721C24";
                    case DIKIRIM   -> "#0C5460";
                    case DIPROSES  -> "#4A235A";
                    case DIBAYAR   -> "#004085";
                    default        -> "#856404";
                };
                setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;");
            }
        });

        colAction.setCellFactory(col -> new TableCell<>() {
            final ComboBox<String> cmbStatus = new ComboBox<>();
            {
                for (Order.Status s : Order.Status.values()) cmbStatus.getItems().add(s.getDisplayName());
                cmbStatus.setStyle("-fx-font-size:11px;-fx-pref-width:130;");
            }

            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Order order = getTableView().getItems().get(getIndex());
                cmbStatus.setValue(order.getStatus().getDisplayName());
                cmbStatus.setOnAction(e -> {
                    String selectedName = cmbStatus.getValue();
                    for (Order.Status s : Order.Status.values()) {
                        if (s.getDisplayName().equals(selectedName)) {
                            orderService.updateStatus(order.getId(), s);
                            order.setStatus(s);
                            tableOrders.refresh();
                            break;
                        }
                    }
                });
                setGraphic(cmbStatus);
                setPadding(new Insets(2));
            }
        });

        tableOrders.setItems(orderData);
    }

    private void loadOrders() {
        allOrders = orderService.getAllOrders();
        orderData.setAll(allOrders);
        lblCount.setText(allOrders.size() + " pesanan");
    }

    @FXML
    private void handleFilter() {
        String sel = cmbFilter.getValue();
        if (sel == null || "Semua Status".equals(sel)) {
            orderData.setAll(allOrders);
        } else {
            List<Order> filtered = allOrders.stream()
                    .filter(o -> o.getStatus().getDisplayName().equals(sel))
                    .collect(Collectors.toList());
            orderData.setAll(filtered);
        }
        lblCount.setText(orderData.size() + " pesanan");
    }

    @FXML private void handleRefresh()   { loadOrders(); }
    @FXML private void handleDashboard() { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_DASHBOARD); }
    @FXML private void handleProducts()  { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_PRODUCTS); }
    @FXML private void handleUsers()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_USERS); }
    @FXML private void handleRefreshOrders() { loadOrders(); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
    }
}
