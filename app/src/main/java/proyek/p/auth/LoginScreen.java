package proyek.p.auth;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.admin.AdminDashboard;
import proyek.p.customer.CustomerDashboard;
import proyek.p.model.*;
import proyek.p.seller.SellerDashboard;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

import java.util.Optional;

public class LoginScreen {
    private final Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // ── LEFT PANEL (Brand) ──────────────────────────────────────────────────
        VBox leftPanel = buildBrandPanel();

        // ── RIGHT PANEL (Form) ──────────────────────────────────────────────────
        VBox rightPanel = buildFormPanel();

        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel,  Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        Scene scene = new Scene(root, 1100, 720);
        stage.setScene(scene);
        stage.show();

        // Fade-in animation
        leftPanel.setOpacity(0);
        rightPanel.setOpacity(0);
        FadeTransition ft1 = new FadeTransition(Duration.millis(600), leftPanel);
        ft1.setToValue(1);
        FadeTransition ft2 = new FadeTransition(Duration.millis(600), rightPanel);
        ft2.setToValue(1);
        ft2.setDelay(Duration.millis(200));
        new ParallelTransition(ft1, ft2).play();
    }

    // ── Brand Panel ─────────────────────────────────────────────────────────────
    private VBox buildBrandPanel() {
        VBox panel = new VBox();
        panel.setPrefWidth(460);
        panel.setMinWidth(460);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60));
        panel.setStyle("-fx-background-color: " + Theme.BLACK + ";");

        // Decorative circles
        StackPane deco = new StackPane();
        Circle c1 = new Circle(160);
        c1.setFill(Color.web(Theme.ACCENT + "15"));
        Circle c2 = new Circle(110);
        c2.setFill(Color.web(Theme.ACCENT + "20"));
        c2.setTranslateX(40);
        c2.setTranslateY(-40);

        // Logo
        Label logo = new Label("DIVERYU26");
        logo.setStyle(
            "-fx-font-size: 48; -fx-font-weight: bold; " +
            "-fx-text-fill: " + Theme.WHITE + "; " +
            "-fx-letter-spacing: 6;"
        );

        Label tagline = new Label("Platform E-Commerce\nTerpercaya Indonesia");
        tagline.setStyle("-fx-font-size: 16; -fx-text-fill: rgba(255,255,255,0.6); -fx-text-alignment: center;");
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setAlignment(Pos.CENTER);

        // Teal accent line
        Rectangle accentLine = new Rectangle(60, 4);
        accentLine.setFill(Color.web(Theme.ACCENT));
        accentLine.setArcWidth(4);
        accentLine.setArcHeight(4);

        // Feature chips
        HBox chip1 = chip("🛍", "Seller");
        HBox chip2 = chip("🛒", "Customer");
        HBox chip3 = chip("⚙", "Admin");
        HBox chips = new HBox(12, chip1, chip2, chip3);
        chips.setAlignment(Pos.CENTER);

        deco.getChildren().addAll(c1, c2, logo);
        deco.setPrefSize(320, 260);

        VBox content = new VBox(16, deco, accentLine, tagline, chips);
        content.setAlignment(Pos.CENTER);

        // Rotating accent animation
        RotateTransition rot = new RotateTransition(Duration.seconds(20), c2);
        rot.setByAngle(360);
        rot.setCycleCount(Timeline.INDEFINITE);
        rot.setInterpolator(Interpolator.LINEAR);
        rot.play();

        panel.getChildren().add(content);
        VBox.setVgrow(content, Priority.ALWAYS);
        panel.setAlignment(Pos.CENTER);
        return panel;
    }

    private HBox chip(String icon, String label) {
        Label lbl = new Label(icon + " " + label);
        lbl.setStyle(
            "-fx-background-color: rgba(255,255,255,0.08);" +
            "-fx-text-fill: rgba(255,255,255,0.8);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 6 14 6 14;" +
            "-fx-font-size: 12;"
        );
        HBox box = new HBox(lbl);
        return box;
    }

    // ── Form Panel ──────────────────────────────────────────────────────────────
    private VBox buildFormPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 80, 60, 80));
        panel.setStyle("-fx-background-color: " + Theme.SURFACE + ";");

        Label title = new Label("Selamat Datang");
        title.setStyle("-fx-font-size: 32; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Label subtitle = UIFactory.bodyText("Masuk ke akun Anda untuk melanjutkan");
        subtitle.setPadding(new Insets(0, 0, 24, 0));

        TextField usernameField = UIFactory.inputField("Username");
        usernameField.setPrefWidth(380);

        PasswordField passwordField = UIFactory.passwordField("Password");
        passwordField.setPrefWidth(380);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: " + Theme.DANGER + "; -fx-font-size: 13;");
        errorLabel.setVisible(false);

        Button loginBtn = UIFactory.primaryBtn("Masuk");
        loginBtn.setPrefWidth(380);
        loginBtn.setPrefHeight(48);

        Separator sep = new Separator();
        sep.setMaxWidth(380);

        Label registerPrompt = UIFactory.bodyText("Belum punya akun?");
        Button registerBtn = UIFactory.accentBtn("Daftarkan Akun");
        registerBtn.setPrefWidth(380);
        registerBtn.setPrefHeight(44);

        // Hint
        VBox hintBox = buildHintBox();

        VBox form = new VBox(10,
            title, subtitle,
            UIFactory.formLabel("Username"), usernameField,
            UIFactory.formLabel("Password"), passwordField,
            errorLabel,
            loginBtn,
            sep,
            registerPrompt, registerBtn,
            hintBox
        );
        form.setAlignment(Pos.TOP_LEFT);
        form.setMaxWidth(380);

        // Login action
        Runnable doLogin = () -> {
            String u = usernameField.getText().trim();
            String p = passwordField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                showError(errorLabel, "Username dan password tidak boleh kosong.");
                return;
            }
            Optional<User> user = DataStore.getInstance().login(u, p);
            if (user.isEmpty()) {
                showError(errorLabel, "Username atau password salah / akun dinonaktifkan.");
                return;
            }
            navigateToDashboard(user.get());
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        registerBtn.setOnAction(e -> new RegisterScreen(stage).show());

        panel.getChildren().add(form);
        return panel;
    }

    private VBox buildHintBox() {
        Label hint = new Label(
            "Demo Akun:\n" +
            "• admin / admin123  (Admin)\n" +
            "• budi_seller / budi123  (Seller)\n" +
            "• andi_buy / andi123  (Customer)"
        );
        hint.setStyle(
            "-fx-font-size: 11; -fx-text-fill: " + Theme.TEXT_MUTED + ";" +
            "-fx-background-color: rgba(0,194,168,0.06);" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 12 14 12 14;" +
            "-fx-border-color: rgba(0,194,168,0.2);" +
            "-fx-border-radius: 8;"
        );
        VBox box = new VBox(hint);
        box.setPadding(new Insets(16, 0, 0, 0));
        return box;
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), lbl);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void navigateToDashboard(User user) {
        switch (user.getRole()) {
            case ADMIN    -> new AdminDashboard(stage, (proyek.p.model.Admin) user).show();
            case SELLER   -> new SellerDashboard(stage, (proyek.p.model.Seller) user).show();
            case CUSTOMER -> new CustomerDashboard(stage, (proyek.p.model.Customer) user).show();
        }
    }
}
