package proyek.p.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Centralized design tokens — inspired by Zalora's clean, bold commerce aesthetic.
 */
public final class Theme {
    private Theme() {}

    // Brand colors
    public static final Color BLACK         = Color.web("#0A0A0A");
    public static final Color WHITE         = Color.web("#FFFFFF");
    public static final Color ACCENT        = Color.web("#00C2A8");
    public static final Color ACCENT_DARK   = Color.web("#009983");
    public static final Color DANGER        = Color.web("#E8334A");
    public static final Color WARNING       = Color.web("#F59E0B");
    public static final Color SUCCESS       = Color.web("#10B981");
    public static final Color SURFACE       = Color.web("#F7F8FA");
    public static final Color CARD          = Color.web("#FFFFFF");
    public static final Color BORDER        = Color.web("#E8E8E8");
    public static final Color TEXT_PRIMARY  = Color.web("#0A0A0A");
    public static final Color TEXT_SECONDARY= Color.web("#6B7280");
    public static final Color TEXT_MUTED    = Color.web("#9CA3AF");
    public static final Color NAV_BG        = Color.web("#0A0A0A");
    public static final Color NAV_ACCENT    = Color.web("#00C2A8");

    // Hex strings (for places that still need raw hex, e.g. color math)
    public static final String BLACK_HEX        = "#0A0A0A";
    public static final String WHITE_HEX        = "#FFFFFF";
    public static final String ACCENT_HEX       = "#00C2A8";
    public static final String ACCENT_DARK_HEX  = "#009983";
    public static final String DANGER_HEX       = "#E8334A";
    public static final String WARNING_HEX      = "#F59E0B";
    public static final String SUCCESS_HEX      = "#10B981";
    public static final String SURFACE_HEX      = "#F7F8FA";
    public static final String CARD_HEX         = "#FFFFFF";
    public static final String BORDER_HEX       = "#E8E8E8";
    public static final String TEXT_PRIMARY_HEX    = "#0A0A0A";
    public static final String TEXT_SECONDARY_HEX  = "#6B7280";
    public static final String TEXT_MUTED_HEX      = "#9CA3AF";
    public static final String NAV_BG_HEX          = "#0A0A0A";
    public static final String NAV_ACCENT_HEX      = "#00C2A8";

    // Typography
    public static Font displayFont(double size) {
        return Font.font("Syne", FontWeight.BOLD, size);
    }
    public static Font bodyFont(double size) {
        return Font.font("DM Sans", size);
    }
    public static Font bodyFontBold(double size) {
        return Font.font("DM Sans", FontWeight.BOLD, size);
    }
}
