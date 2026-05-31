package test.woi.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import test.woi.dao.UserDAO;
import test.woi.model.Order;
import test.woi.model.User;
import test.woi.service.OrderService;
import test.woi.service.ProductService;
import test.woi.util.SceneManager;
import test.woi.util.SessionManager;

public class AdminDashboardController {

    @FXML private Label lblAdminName, lblDate, lblRoleBadge;
    @FXML private Label lblRevenue, lblOrderCount, lblProductCount, lblUserCount;
    @FXML private TableView<Order> tableRecentOrders;
    @FXML private TableColumn<Order, String> colOrderNum, colCustomer, colTotal, colStatus, colOrderDate;

    private final OrderService orderService = new OrderService();
    private final ProductService productService = new ProductService();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        User me = SessionManager.getInstance().getCurrentUser();
        lblAdminName.setText(me.getDisplayName());
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id"))));

        if (me.isAdmin()) {
            lblRoleBadge.setText("Administrator #" + me.getAdminSeq());
        } else {
            lblRoleBadge.setText("Penjual");
        }

        setupTable();
        loadStats();
    }

    private void setupTable() {
        colOrderNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrderNumber()));
        colCustomer.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCustomerName()));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFormattedTotal()));
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getDisplayName()));
        colOrderDate.setCellValueFactory(d -> {
            String date = d.getValue().getCreatedAt() != null
                ? d.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "-";
            return new SimpleStringProperty(date);
        });

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                Order.Status s = getTableView().getItems().get(getIndex()).getStatus();
                String color = switch (s) {
                    case SELESAI    -> "#155724";
                    case DIBATALKAN -> "#721C24";
                    case DIKIRIM    -> "#0C5460";
                    case DIPROSES   -> "#4A235A";
                    case DIBAYAR    -> "#004085";
                    default         -> "#856404";
                };
                setStyle("-fx-text-fill:" + color + ";-fx-font-weight:bold;");
            }
        });
    }

    private void loadStats() {
        lblRevenue.setText(String.format("Rp %,.0f", orderService.getTotalRevenue()));
        lblOrderCount.setText(String.valueOf(orderService.countOrders()));
        lblProductCount.setText(String.valueOf(productService.countProducts()));
        lblUserCount.setText(String.valueOf(userDAO.countAll()));

        List<Order> orders = orderService.getAllOrders();
        List<Order> recent = orders.stream().limit(10).toList();
        tableRecentOrders.setItems(FXCollections.observableArrayList(recent));
    }

    @FXML private void handleRefresh()   { loadStats(); }
    @FXML private void handleDashboard() { /* sudah di sini */ }

    @FXML private void handleProducts()  { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_PRODUCTS); }
    @FXML private void handleOrders()    { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_ORDERS); }
    @FXML private void handleUsers()     { SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_USERS); }

    @FXML private void handleSellerProfile() { SceneManager.getInstance().switchTo(SceneManager.SceneName.SELLER_PROFILE); }

    /** Tombol khusus Admin — kelola akun */
    @FXML private void handleManageUsers() {
        SceneManager.getInstance().switchTo(SceneManager.SceneName.ADMIN_MANAGE_USERS);
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin keluar?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                SessionManager.getInstance().logout();
                SceneManager.getInstance().switchTo(SceneManager.SceneName.LOGIN);
            }
        });
    }
}
