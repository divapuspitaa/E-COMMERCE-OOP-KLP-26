package com.nusantarashop;

import com.nusantarashop.util.DatabaseManager;
import com.nusantarashop.util.SceneManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * MainApp - Titik masuk utama aplikasi NusantaraShop.
 *
 * Alur:
 *   1. Inisialisasi DatabaseManager (SQLite, buat tabel, seed data awal)
 *   2. Set up SceneManager dengan Stage utama
 *   3. Tampilkan halaman Login
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi database
        DatabaseManager.getInstance();

        // Siapkan SceneManager
        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.setPrimaryStage(primaryStage);

        // Konfigurasi Stage
        primaryStage.setTitle("🛍 NusantaraShop — Belanja Produk Lokal Terbaik");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(1100);
        primaryStage.setHeight(720);
        primaryStage.centerOnScreen();

        // Tutup database saat aplikasi ditutup
        primaryStage.setOnCloseRequest(e -> {
            DatabaseManager.getInstance().close();
            Platform.exit();
        });

        // Tampilkan halaman Login
        sceneManager.switchTo(SceneManager.SceneName.LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
