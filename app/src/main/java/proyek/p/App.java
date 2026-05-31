package proyek.p;

import javafx.application.Application;
import javafx.stage.Stage;
import proyek.p.auth.LoginScreen;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("DIVERYU26 — E-Commerce Platform");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        showLogin();
    }

    public static Stage getStage() { return primaryStage; }

    public static void showLogin() {
        new LoginScreen(primaryStage).show();
    }

    public static void main(String[] args) { launch(args); }
}
