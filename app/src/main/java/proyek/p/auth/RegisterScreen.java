package proyek.p.auth;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import proyek.p.App;
import proyek.p.model.Admin;
import proyek.p.model.Customer;
import proyek.p.model.DataStore;
import proyek.p.model.Seller;
import proyek.p.model.User;
import proyek.p.ui.Theme;
import proyek.p.ui.UIFactory;

public class RegisterScreen {
    private final Stage stage;

    public RegisterScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = buildRoot();
        root.setOpacity(0);
        Scene scene = new Scene(root, 900, 650);
        stage.setScene(scene);

        FadeTransition ft = new FadeTransition(Duration.millis(400), root);
        ft.setToValue(1);
        ft.play();
    }

    private VBox buildRoot() {
        // Header
        HBox header = new HBox();
        header.setPadding(new Insets(0, 24, 0, 24));
        header.setPrefHeight(60);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setBackground(new Background(new BackgroundFill(Theme.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Label logo = new Label("DIVERYU26");
        logo.setFont(Theme.bodyFontBold(20));
        logo.setTextFill(Theme.WHITE);

        Label slash = new Label(" / Daftarkan Akun");
        slash.setFont(Theme.bodyFont(14));
        slash.setTextFill(Theme.ACCENT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button backBtn = UIFactory.outlineBtn("← Kembali");
        backBtn.setTextFill(Theme.WHITE);
        backBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, new CornerRadii(8), Insets.EMPTY)));
        backBtn.setBorder(new Border(new BorderStroke(
            Color.rgb(255, 255, 255, 0.3), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(1)
        )));
        backBtn.setOnAction(e -> App.showLogin());

        header.getChildren().addAll(logo, slash, spacer, backBtn);

        // Content
        HBox content = new HBox(40);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(50, 80, 50, 80));
        content.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));

        content.getChildren().addAll(buildFormCard());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setBackground(new Background(new BackgroundFill(Theme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox root = new VBox(header, scrollPane);
        return root;
    }

    private VBox buildFormCard() {
        // Title section
        Label title = new Label("Buat Akun Baru");
        title.setFont(Theme.bodyFontBold(28));
        title.setTextFill(Theme.TEXT_PRIMARY);

        Label subtitle = UIFactory.bodyText("Isi data di bawah ini untuk membuat akun Anda");
        subtitle.setPadding(new Insets(0, 0, 16, 0));

        // Form fields
        TextField usernameField = UIFactory.inputField("Masukkan username unik");
        usernameField.setPrefWidth(420);

        TextField emailField = UIFactory.inputField("Contoh: nama@email.com");
        emailField.setPrefWidth(420);

        PasswordField passwordField = UIFactory.passwordField("Minimal 6 karakter");
        passwordField.setPrefWidth(420);

        PasswordField confirmField = UIFactory.passwordField("Ulangi password Anda");
        confirmField.setPrefWidth(420);

        // Role selection
        ToggleGroup roleGroup = new ToggleGroup();
        HBox roleBox = new HBox(12,
            roleCard("🏪", "Seller", "Jual produk Anda", User.Role.SELLER, roleGroup),
            roleCard("🛒", "Customer", "Beli produk favorit", User.Role.CUSTOMER, roleGroup),
            roleCard("⚙", "Admin", "Kelola platform", User.Role.ADMIN, roleGroup)
        );

        // Admin secret
        VBox adminSecretBox = UIFactory.formField("Password Admin (khusus role Admin)",
            UIFactory.passwordField("Masukkan password admin"));
        PasswordField adminSecretField = (PasswordField) adminSecretBox.getChildren().get(1);
        adminSecretBox.setVisible(false);
        adminSecretBox.setManaged(false);

        // Show admin field only when ADMIN role selected
        roleGroup.selectedToggleProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                boolean isAdmin = ((ToggleButton) nw).getUserData() == User.Role.ADMIN;
                adminSecretBox.setVisible(isAdmin);
                adminSecretBox.setManaged(isAdmin);
            }
        });

        Label errorLabel = new Label();
        errorLabel.setFont(Theme.bodyFont(13));
        errorLabel.setTextFill(Theme.DANGER);
        errorLabel.setVisible(false);

        Button registerBtn = UIFactory.accentBtn("Buat Akun");
        registerBtn.setPrefWidth(420);
        registerBtn.setPrefHeight(48);

        registerBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            String username = usernameField.getText().trim();
            String email    = emailField.getText().trim();
            String password = passwordField.getText();
            String confirm  = confirmField.getText();
            Toggle selectedToggle = roleGroup.getSelectedToggle();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Semua field wajib diisi."); return;
            }
            if (username.length() < 4) {
                showError(errorLabel, "Username minimal 4 karakter."); return;
            }
            if (!email.contains("@")) {
                showError(errorLabel, "Format email tidak valid."); return;
            }
            if (password.length() < 6) {
                showError(errorLabel, "Password minimal 6 karakter."); return;
            }
            if (!password.equals(confirm)) {
                showError(errorLabel, "Konfirmasi password tidak cocok."); return;
            }
            if (selectedToggle == null) {
                showError(errorLabel, "Pilih role terlebih dahulu."); return;
            }
            if (DataStore.getInstance().usernameExists(username)) {
                showError(errorLabel, "Username sudah digunakan."); return;
            }

            User.Role role = (User.Role) selectedToggle.getUserData();
            if (role == User.Role.ADMIN) {
                if (!DataStore.getInstance().validateAdminSecret(adminSecretField.getText())) {
                    showError(errorLabel, "Password admin tidak valid."); return;
                }
            }

            String id = (role == User.Role.ADMIN)
                    ? DataStore.getInstance().generateAdminId()
                    : DataStore.getInstance().generateId();
            User newUser = switch (role) {
                case ADMIN    -> new Admin(id, username, password, email);
                case SELLER   -> new Seller(id, username, password, email);
                case CUSTOMER -> new Customer(id, username, password, email);
            };
            DataStore.getInstance().register(newUser);
            UIFactory.showAlert("Berhasil!", "Akun berhasil dibuat. Silakan masuk.", Alert.AlertType.INFORMATION);
            App.showLogin();
        });

        VBox form = new VBox(14,
            title, subtitle,
            UIFactory.formField("Username", usernameField),
            UIFactory.formField("Email", emailField),
            UIFactory.formField("Password", passwordField),
            UIFactory.formField("Konfirmasi Password", confirmField),
            new VBox(8, UIFactory.formLabel("Pilih Role"), roleBox),
            adminSecretBox,
            errorLabel,
            registerBtn
        );
        form.setMaxWidth(420);
        form.setAlignment(Pos.TOP_LEFT);
        return form;
    }

    private ToggleButton roleCard(String icon, String name, String desc, User.Role role, ToggleGroup group) {
        Label iconLbl = new Label(icon);
        iconLbl.setFont(Theme.bodyFont(22));
        Label nameLbl = new Label(name);
        nameLbl.setFont(Theme.bodyFontBold(14));
        nameLbl.setTextFill(Theme.TEXT_PRIMARY);
        Label descLbl = new Label(desc);
        descLbl.setFont(Theme.bodyFont(11));
        descLbl.setTextFill(Theme.TEXT_MUTED);
        descLbl.setWrapText(true);

        VBox content = new VBox(4, iconLbl, nameLbl, descLbl);
        content.setAlignment(Pos.TOP_LEFT);

        ToggleButton btn = new ToggleButton();
        btn.setGraphic(content);
        btn.setUserData(role);
        btn.setToggleGroup(group);
        btn.setPrefSize(128, 90);
        btn.setPadding(new Insets(12));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setBackground(new Background(new BackgroundFill(Theme.WHITE, new CornerRadii(10), Insets.EMPTY)));
        btn.setBorder(new Border(new BorderStroke(
            Theme.BORDER, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1)
        )));

        btn.selectedProperty().addListener((obs, old, nw) -> {
            if (nw) {
                btn.setBackground(new Background(new BackgroundFill(
                    Color.web(Theme.ACCENT_HEX, 0.05), new CornerRadii(10), Insets.EMPTY
                )));
                btn.setBorder(new Border(new BorderStroke(
                    Theme.ACCENT, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1)
                )));
            } else {
                btn.setBackground(new Background(new BackgroundFill(Theme.WHITE, new CornerRadii(10), Insets.EMPTY)));
                btn.setBorder(new Border(new BorderStroke(
                    Theme.BORDER, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1)
                )));
            }
        });
        return btn;
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
    }
}
