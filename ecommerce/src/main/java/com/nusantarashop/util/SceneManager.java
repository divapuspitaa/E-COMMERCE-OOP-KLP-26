package com.nusantarashop.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager - mengelola navigasi antar scene dalam aplikasi.
 * Menyimpan referensi stage utama dan cache scene.
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
        ADMIN_USERS
    }

    private static SceneManager instance;
    private Stage primaryStage;
    private final Map<SceneName, String> fxmlPaths;

    private SceneManager() {
        fxmlPaths = new HashMap<>();
        fxmlPaths.put(SceneName.LOGIN,           "/com/nusantarashop/fxml/LoginView.fxml");
        fxmlPaths.put(SceneName.REGISTER,        "/com/nusantarashop/fxml/RegisterView.fxml");
        fxmlPaths.put(SceneName.HOME,            "/com/nusantarashop/fxml/HomeView.fxml");
        fxmlPaths.put(SceneName.PRODUCT_DETAIL,  "/com/nusantarashop/fxml/ProductDetailView.fxml");
        fxmlPaths.put(SceneName.CART,            "/com/nusantarashop/fxml/CartView.fxml");
        fxmlPaths.put(SceneName.CHECKOUT,        "/com/nusantarashop/fxml/CheckoutView.fxml");
        fxmlPaths.put(SceneName.ORDER_HISTORY,   "/com/nusantarashop/fxml/OrderHistoryView.fxml");
        fxmlPaths.put(SceneName.PROFILE,         "/com/nusantarashop/fxml/ProfileView.fxml");
        fxmlPaths.put(SceneName.ADMIN_DASHBOARD, "/com/nusantarashop/fxml/AdminDashboardView.fxml");
        fxmlPaths.put(SceneName.ADMIN_PRODUCTS,  "/com/nusantarashop/fxml/AdminProductsView.fxml");
        fxmlPaths.put(SceneName.ADMIN_ORDERS,    "/com/nusantarashop/fxml/AdminOrdersView.fxml");
        fxmlPaths.put(SceneName.ADMIN_USERS,     "/com/nusantarashop/fxml/AdminUsersView.fxml");
    }

    public static synchronized SceneManager getInstance() {
        if (instance == null) instance = new SceneManager();
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() { return primaryStage; }

    public void switchTo(SceneName sceneName) {
        String fxmlPath = fxmlPaths.get(sceneName);
        if (fxmlPath == null) {
            System.err.println("[Scene] FXML path tidak ditemukan untuk: " + sceneName);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // Load global stylesheet
            String css = getClass().getResource("/com/nusantarashop/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("[Scene] Pindah ke: " + sceneName);
        } catch (IOException e) {
            System.err.println("[Scene] Gagal memuat FXML " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public <T> T loadController(SceneName sceneName) throws IOException {
        String fxmlPath = fxmlPaths.get(sceneName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }
}
