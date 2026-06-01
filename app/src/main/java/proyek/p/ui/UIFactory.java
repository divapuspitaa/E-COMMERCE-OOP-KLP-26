package proyek.p.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
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
import javafx.scene.shape.Rectangle;

/**
 * Factory for reusable polished UI components.
 */
public final class UIFactory {
    private UIFactory() {}

    // Helper: buat Background solid satu warna
    private static Background solidBg(Color color, double radius) {
        BackgroundFill fill = new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY);
        return new Background(fill);
    }

    // Helper: buat Border solid satu warna
    private static Border solidBorder(Color color, double radius) {
        BorderStroke stroke = new BorderStroke(color, BorderStrokeStyle.SOLID,
                new CornerRadii(radius), new BorderWidths(1));
        return new Border(stroke);
    }

    // Helper: drop shadow ringan untuk card
    private static DropShadow cardShadow() {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.rgb(0, 0, 0, 0.06));
        ds.setRadius(12);
        ds.setOffsetX(0);
        ds.setOffsetY(3);
        return ds;
    }

    // ── Labels ───────────────────────────────────────────────────────────────────
    public static Label heading(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Theme.displayFont(28));
        lbl.setTextFill(Theme.TEXT_PRIMARY);
        return lbl;
    }

    public static Label subheading(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Theme.bodyFontBold(18));
        lbl.setTextFill(Theme.TEXT_PRIMARY);
        return lbl;
    }

    public static Label caption(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Theme.bodyFont(12));
        lbl.setTextFill(Theme.TEXT_MUTED);
        return lbl;
    }

    public static Label bodyText(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Theme.bodyFont(14));
        lbl.setTextFill(Theme.TEXT_SECONDARY);
        return lbl;
    }

    // ── Buttons ──────────────────────────────────────────────────────────────────
    public static Button primaryBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Theme.bodyFont(14));
        btn.setTextFill(Theme.WHITE);
        btn.setBackground(solidBg(Theme.BLACK, 8));
        btn.setPadding(new Insets(12, 24, 12, 24));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setBackground(solidBg(Color.web("#222222"), 8)));
        btn.setOnMouseExited(e  -> btn.setBackground(solidBg(Theme.BLACK, 8)));
        return btn;
    }

    public static Button accentBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Theme.bodyFont(14));
        btn.setTextFill(Theme.WHITE);
        btn.setBackground(solidBg(Theme.ACCENT, 8));
        btn.setPadding(new Insets(12, 24, 12, 24));
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setBackground(solidBg(Theme.ACCENT_DARK, 8)));
        btn.setOnMouseExited(e  -> btn.setBackground(solidBg(Theme.ACCENT, 8)));
        return btn;
    }

    public static Button dangerBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Theme.bodyFont(14));
        btn.setTextFill(Theme.WHITE);
        btn.setBackground(solidBg(Theme.DANGER, 8));
        btn.setPadding(new Insets(10, 20, 10, 20));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    public static Button outlineBtn(String text) {
        Button btn = new Button(text);
        btn.setFont(Theme.bodyFont(14));
        btn.setTextFill(Theme.BLACK);
        btn.setBackground(solidBg(Color.TRANSPARENT, 8));
        btn.setBorder(solidBorder(Theme.BLACK, 8));
        btn.setPadding(new Insets(11, 23, 11, 23));
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    // ── Inputs ───────────────────────────────────────────────────────────────────
    public static TextField inputField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Theme.bodyFont(14));
        tf.setBackground(solidBg(Theme.SURFACE, 8));
        tf.setBorder(solidBorder(Theme.BORDER, 8));
        tf.setPadding(new Insets(10, 14, 10, 14));
        tf.setPrefHeight(42);
        return tf;
    }

    public static PasswordField passwordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setFont(Theme.bodyFont(14));
        pf.setBackground(solidBg(Theme.SURFACE, 8));
        pf.setBorder(solidBorder(Theme.BORDER, 8));
        pf.setPadding(new Insets(10, 14, 10, 14));
        pf.setPrefHeight(42);
        return pf;
    }

    public static TextArea textArea(String prompt, int rows) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setFont(Theme.bodyFont(14));
        ta.setBackground(solidBg(Theme.SURFACE, 8));
        ta.setBorder(solidBorder(Theme.BORDER, 8));
        ta.setPadding(new Insets(10, 14, 10, 14));
        ta.setWrapText(true);
        return ta;
    }

    public static Label formLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Theme.bodyFontBold(13));
        lbl.setTextFill(Theme.TEXT_PRIMARY);
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
        card.setBackground(solidBg(Theme.CARD, 12));
        card.setEffect(cardShadow());
        card.getChildren().addAll(children);
        return card;
    }

    // ── Stat Cards ───────────────────────────────────────────────────────────────
    public static VBox statCard(String value, String label, String colorHex) {
        Color color = Color.web(colorHex);

        Label valLbl = new Label(value);
        valLbl.setFont(Theme.bodyFontBold(36));
        valLbl.setTextFill(color);

        Label nameLbl = new Label(label);
        nameLbl.setFont(Theme.bodyFont(13));
        nameLbl.setTextFill(Theme.TEXT_SECONDARY);

        Rectangle bar = new Rectangle(40, 4);
        bar.setFill(color);
        bar.setArcWidth(4);
        bar.setArcHeight(4);

        VBox card = new VBox(8, bar, valLbl, nameLbl);
        card.setPadding(new Insets(20));
        card.setBackground(solidBg(Theme.CARD, 12));
        card.setEffect(cardShadow());
        card.setPrefWidth(180);
        return card;
    }

    // ── Separator ────────────────────────────────────────────────────────────────
    public static Separator divider() {
        Separator sep = new Separator();
        sep.setBackground(solidBg(Theme.BORDER, 0));
        return sep;
    }

    // ── Badge ────────────────────────────────────────────────────────────────────
    public static Label badge(String text, Color bgColor) {
        Label lbl = new Label(text);
        // Warna background dengan opacity 13% (hex 22 ≈ 13%)
        Color fadedBg = Color.color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.13);
        lbl.setBackground(solidBg(fadedBg, 20));
        lbl.setTextFill(bgColor);
        lbl.setPadding(new Insets(3, 10, 3, 10));
        lbl.setFont(Theme.bodyFontBold(11));
        return lbl;
    }

    // ── Navbar ───────────────────────────────────────────────────────────────────
    public static HBox navbar(String title, String username, String role, Runnable onLogout) {
        Label logo = new Label("DIVERYU26");
        logo.setFont(Theme.bodyFontBold(20));
        logo.setTextFill(Theme.WHITE);

        Label titleLbl = new Label("/ " + title);
        titleLbl.setFont(Theme.bodyFont(14));
        titleLbl.setTextFill(Theme.ACCENT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label(username);
        userInfo.setFont(Theme.bodyFontBold(13));
        userInfo.setTextFill(Theme.WHITE);

        Label roleBadge = badge(role, Theme.ACCENT);

        Button logoutBtn = new Button("Keluar");
        logoutBtn.setFont(Theme.bodyFont(12));
        logoutBtn.setTextFill(Theme.WHITE);
        logoutBtn.setBackground(solidBg(Color.TRANSPARENT, 6));
        logoutBtn.setBorder(solidBorder(Color.rgb(255, 255, 255, 0.3), 6));
        logoutBtn.setPadding(new Insets(6, 16, 6, 16));
        logoutBtn.setCursor(javafx.scene.Cursor.HAND);
        logoutBtn.setOnAction(e -> onLogout.run());

        HBox nav = new HBox(12, logo, titleLbl, spacer, userInfo, roleBadge, logoutBtn);
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setPadding(new Insets(0, 24, 0, 24));
        nav.setPrefHeight(60);
        nav.setBackground(solidBg(Theme.NAV_BG, 0));
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
