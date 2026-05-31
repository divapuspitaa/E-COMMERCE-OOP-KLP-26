package proyek.p.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;

/**
 * Factory for reusable polished UI components.
 */
public final class UIFactory {
    private UIFactory() {}

    // ── Labels ───────────────────────────────────────────────────────────────────
    public static Label heading(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        return lbl;
    }

    public static Label subheading(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        return lbl;
    }

    public static Label caption(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12; -fx-text-fill: " + Theme.TEXT_MUTED + ";");
        return lbl;
    }

    public static Label bodyText(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 14; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        return lbl;
    }

    // ── Buttons ──────────────────────────────────────────────────────────────────
    public static Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(Theme.BTN_PRIMARY);
        btn.setOnMouseEntered(e -> btn.setStyle(Theme.BTN_PRIMARY + "-fx-background-color: #222;"));
        btn.setOnMouseExited(e  -> btn.setStyle(Theme.BTN_PRIMARY));
        return btn;
    }

    public static Button accentBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(Theme.BTN_ACCENT);
        btn.setOnMouseEntered(e -> btn.setStyle(Theme.BTN_ACCENT + "-fx-background-color: " + Theme.ACCENT_DARK + ";"));
        btn.setOnMouseExited(e  -> btn.setStyle(Theme.BTN_ACCENT));
        return btn;
    }

    public static Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(Theme.BTN_DANGER);
        return btn;
    }

    public static Button outlineBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(Theme.BTN_OUTLINE);
        return btn;
    }

    // ── Inputs ───────────────────────────────────────────────────────────────────
    public static TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(Theme.INPUT_STYLE);
        tf.setPrefHeight(42);
        return tf;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setStyle(Theme.INPUT_STYLE);
        pf.setPrefHeight(42);
        return pf;
    }

    public static TextArea textArea(String prompt, int rows) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setStyle(Theme.INPUT_STYLE + "-fx-pref-row-count: " + rows + ";");
        ta.setWrapText(true);
        return ta;
    }

    public static Label formLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        return lbl;
    }

    public static VBox formField(String label, Control input) {
        VBox box = new VBox(6, formLabel(label), input);
        return box;
    }

    // ── Cards ────────────────────────────────────────────────────────────────────
    public static VBox card(Node... children) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle(Theme.CARD_STYLE);
        card.getChildren().addAll(children);
        return card;
    }

    // ── Stat Cards ───────────────────────────────────────────────────────────────
    public static VBox statCard(String value, String label, String color) {
        Label valLbl = new Label(value);
        valLbl.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label nameLbl = new Label(label);
        nameLbl.setStyle("-fx-font-size: 13; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        Rectangle bar = new Rectangle(40, 4);
        bar.setFill(Color.web(color));
        bar.setArcWidth(4);
        bar.setArcHeight(4);

        VBox card = new VBox(8, bar, valLbl, nameLbl);
        card.setPadding(new Insets(20));
        card.setStyle(Theme.CARD_STYLE);
        card.setPrefWidth(180);
        return card;
    }

    // ── Separator ────────────────────────────────────────────────────────────────
    public static Separator divider() {
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + Theme.BORDER + ";");
        return sep;
    }

    // ── Badge ────────────────────────────────────────────────────────────────────
    public static Label badge(String text, String bgColor) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-background-color: " + bgColor + "22;" +
            "-fx-text-fill: " + bgColor + ";" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 3 10 3 10;" +
            "-fx-font-size: 11; -fx-font-weight: bold;"
        );
        return lbl;
    }

    // ── Navbar ───────────────────────────────────────────────────────────────────
    public static HBox navbar(String title, String username, String role, Runnable onLogout) {
        Label logo = new Label("DIVERYU26");
        logo.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: " + Theme.WHITE + "; -fx-letter-spacing: 3;");

        Label titleLbl = new Label("/ " + title);
        titleLbl.setStyle("-fx-font-size: 14; -fx-text-fill: " + Theme.ACCENT + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(username);
        userInfo.setStyle("-fx-font-size: 13; -fx-text-fill: " + Theme.WHITE + "; -fx-font-weight: bold;");

        Label roleBadge = badge(role, Theme.ACCENT);

        Button logoutBtn = new Button("Keluar");
        logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + Theme.WHITE + ";" +
            "-fx-border-color: rgba(255,255,255,0.3);" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 16 6 16;" +
            "-fx-font-size: 12;" +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> onLogout.run());

        HBox nav = new HBox(12, logo, titleLbl, spacer, userInfo, roleBadge, logoutBtn);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0, 24, 0, 24));
        nav.setPrefHeight(60);
        nav.setStyle("-fx-background-color: " + Theme.NAV_BG + ";");
        return nav;
    }

    // ── Alert ────────────────────────────────────────────────────────────────────
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }
}
