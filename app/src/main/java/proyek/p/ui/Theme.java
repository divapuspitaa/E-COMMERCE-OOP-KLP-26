package proyek.p.ui;

/**
 * Centralized design tokens — inspired by Zalora's clean, bold commerce aesthetic.
 */
public final class Theme {
    private Theme() {}

    // Brand colors
    public static final String BLACK       = "#0A0A0A";
    public static final String WHITE       = "#FFFFFF";
    public static final String ACCENT      = "#00C2A8";   // teal
    public static final String ACCENT_DARK = "#009983";
    public static final String DANGER      = "#E8334A";
    public static final String WARNING     = "#F59E0B";
    public static final String SUCCESS     = "#10B981";
    public static final String SURFACE     = "#F7F8FA";
    public static final String CARD        = "#FFFFFF";
    public static final String BORDER      = "#E8E8E8";
    public static final String TEXT_PRIMARY   = "#0A0A0A";
    public static final String TEXT_SECONDARY = "#6B7280";
    public static final String TEXT_MUTED     = "#9CA3AF";
    public static final String NAV_BG         = "#0A0A0A";
    public static final String NAV_ACCENT     = "#00C2A8";

    // Typography (Google Fonts loaded via CSS)
    public static final String FONT_DISPLAY = "Syne";
    public static final String FONT_BODY    = "DM Sans";

    // Common CSS snippets
    public static final String CARD_STYLE =
        "-fx-background-color: " + CARD + ";" +
        "-fx-background-radius: 12;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 12, 0, 0, 3);";

    public static final String BTN_PRIMARY =
        "-fx-background-color: " + BLACK + ";" +
        "-fx-text-fill: " + WHITE + ";" +
        "-fx-background-radius: 8;" +
        "-fx-font-size: 14;" +
        "-fx-padding: 12 24 12 24;" +
        "-fx-cursor: hand;";

    public static final String BTN_ACCENT =
        "-fx-background-color: " + ACCENT + ";" +
        "-fx-text-fill: " + WHITE + ";" +
        "-fx-background-radius: 8;" +
        "-fx-font-size: 14;" +
        "-fx-padding: 12 24 12 24;" +
        "-fx-cursor: hand;";

    public static final String BTN_DANGER =
        "-fx-background-color: " + DANGER + ";" +
        "-fx-text-fill: " + WHITE + ";" +
        "-fx-background-radius: 8;" +
        "-fx-font-size: 14;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;";

    public static final String BTN_OUTLINE =
        "-fx-background-color: transparent;" +
        "-fx-text-fill: " + BLACK + ";" +
        "-fx-border-color: " + BLACK + ";" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-font-size: 14;" +
        "-fx-padding: 11 23 11 23;" +
        "-fx-cursor: hand;";

    public static final String INPUT_STYLE =
        "-fx-background-color: " + SURFACE + ";" +
        "-fx-background-radius: 8;" +
        "-fx-border-color: " + BORDER + ";" +
        "-fx-border-radius: 8;" +
        "-fx-padding: 10 14 10 14;" +
        "-fx-font-size: 14;";
}
