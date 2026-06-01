package proyek.p.auth;

import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.admin.AdminDashboard;
import proyek.p.customer.CustomerDashboard;
import proyek.p.model.DataStore;
import proyek.p.model.User;
import proyek.p.seller.SellerDashboard;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

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
        panel.setBackground(new Background(new BackgroundFill(Theme.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        // Decorative circles
        StackPane deco = new StackPane();
        Circle c1 = new Circle(160);
        c1.setFill(Color.web(Theme.ACCENT_HEX, 0.08));
        Circle c2 = new Circle(110);
        c2.setFill(Color.web(Theme.ACCENT_HEX, 0.12));
        c2.setTranslateX(40);
        c2.setTranslateY(-40);

        // Logo
        Label logo = new Label("DIVERYU26");
        logo.setFont(Theme.displayFont(48));
        logo.setTextFill(Theme.WHITE);

        Label tagline = new Label("Platform E-Commerce\nTerpercaya Indonesia");
        tagline.setFont(Theme.bodyFont(16));
        tagline.setTextFill(Color.rgb(255, 255, 255, 0.6));
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setAlignment(Pos.CENTER);

        // Teal accent line
        Rectangle accentLine = new Rectangle(60, 4);
        accentLine.setFill(Theme.ACCENT);
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
        lbl.setFont(Theme.bodyFont(12));
        lbl.setTextFill(Color.rgb(255, 255, 255, 0.8));
        lbl.setBackground(new Background(new BackgroundFill(
            Color.rgb(255, 255, 255, 0.08), new CornerRadii(20), Insets.EMPTY
        )));
        lbl.setPadding(new Insets(6, 14, 6, 14));
        HBox box = new HBox(lbl);
        return box;
    }

    // ── Form Panel ──────────────────────────────────────────────────────────────
    private VBox buildFormPanel() {
        VBox panel = new VBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 80, 60, 80));
        panel.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Selamat Datang");
        title.setFont(Theme.bodyFontBold(32));
        title.setTextFill(Theme.TEXT_PRIMARY);

        Label subtitle = UIFactory.bodyText("Masuk ke akun Anda untuk melanjutkan");
        subtitle.setPadding(new Insets(0, 0, 24, 0));

        TextField usernameField = UIFactory.inputField("Username");
        usernameField.setPrefWidth(380);

        PasswordField passwordField = UIFactory.passwordField("Password");
        passwordField.setPrefWidth(380);

        Label errorLabel = new Label();
        errorLabel.setFont(Theme.bodyFont(13));
        errorLabel.setTextFill(Theme.DANGER);
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
        hint.setFont(Theme.bodyFont(11));
        hint.setTextFill(Theme.TEXT_MUTED);
        hint.setBackground(new Background(new BackgroundFill(
            Color.web(Theme.ACCENT_HEX, 0.06), new CornerRadii(8), Insets.EMPTY
        )));
        hint.setPadding(new Insets(12, 14, 12, 14));
        hint.setBorder(new Border(new BorderStroke(
            Color.web(Theme.ACCENT_HEX, 0.2), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1)
        )));
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
