package test.woi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager - mengelola navigasi antar scene dalam aplikasi.
 */
public class SceneManager {

    public enum SceneName {
        LOGIN,
        REGISTER,
        HOME,
        PRODUCT_DETAIL,
        CART,
        CHECKOUT,
        ORDER_HISTORY,
        PROFILE,
        ADMIN_DASHBOARD,
        ADMIN_PRODUCTS,
        ADMIN_ORDERS,
        ADMIN_USERS,
        ADMIN_PANEL,       // Dashboard khusus Admin (bukan Seller)
        ADMIN_MANAGE_USERS,// Kelola akun khusus Admin
        SELLER_PROFILE,
        SELLER_DASHBOARD
    }

    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<SceneName, String> fxmlPaths;

    private SceneManager() {
        fxmlPaths = new HashMap<>();
        fxmlPaths.put(SceneName.LOGIN,              "/test/woi/fxml/LoginView.fxml");
        fxmlPaths.put(SceneName.REGISTER,           "/test/woi/fxml/RegisterView.fxml");
        fxmlPaths.put(SceneName.HOME,               "/test/woi/fxml/HomeView.fxml");
        fxmlPaths.put(SceneName.PRODUCT_DETAIL,     "/test/woi/fxml/ProductDetailView.fxml");
        fxmlPaths.put(SceneName.CART,               "/test/woi/fxml/CartView.fxml");
        fxmlPaths.put(SceneName.CHECKOUT,           "/test/woi/fxml/CheckoutView.fxml");
        fxmlPaths.put(SceneName.ORDER_HISTORY,      "/test/woi/fxml/OrderHistoryView.fxml");
        fxmlPaths.put(SceneName.PROFILE,            "/test/woi/fxml/ProfileView.fxml");
        fxmlPaths.put(SceneName.ADMIN_DASHBOARD,    "/test/woi/fxml/AdminDashboardView.fxml");
        fxmlPaths.put(SceneName.ADMIN_PRODUCTS,     "/test/woi/fxml/AdminProductsView.fxml");
        fxmlPaths.put(SceneName.ADMIN_ORDERS,       "/test/woi/fxml/AdminOrdersView.fxml");
        fxmlPaths.put(SceneName.ADMIN_USERS,        "/test/woi/fxml/AdminUsersView.fxml");
        fxmlPaths.put(SceneName.ADMIN_PANEL,        "/test/woi/fxml/AdminPanelView.fxml");
        fxmlPaths.put(SceneName.ADMIN_MANAGE_USERS, "/test/woi/fxml/AdminManageUsersView.fxml");
        fxmlPaths.put(SceneName.SELLER_PROFILE,     "/test/woi/fxml/SellerProfileView.fxml");
        fxmlPaths.put(SceneName.SELLER_DASHBOARD,    "/test/woi/fxml/SellerDashboardView.fxml");
    }

    public static synchronized SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void setPrimaryStage(Stage stage) { this.primaryStage = stage; }
    public Stage getPrimaryStage() { return primaryStage; }

    public void switchTo(SceneName sceneName) {
        String fxmlPath = fxmlPaths.get(sceneName);
        if (fxmlPath == null) {
            throw new RuntimeException("[Scene] FXML path tidak ditemukan untuk: " + sceneName);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (getClass().getResource(fxmlPath) == null) {
                throw new IOException("Resource FXML tidak ditemukan: " + fxmlPath);
            }
            Parent root = loader.load();
            Scene scene = new Scene(root);
            var cssUrl = getClass().getResource("/test/woi/css/style.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("[Scene] Pindah ke: " + sceneName);
        } catch (IOException e) {
            System.err.println("[Scene] Gagal memuat FXML " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal memuat scene: " + sceneName + " -> " + e.getMessage(), e);
        }
    }

    public <T> T loadController(SceneName sceneName) throws IOException {
        String fxmlPath = fxmlPaths.get(sceneName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }
}
