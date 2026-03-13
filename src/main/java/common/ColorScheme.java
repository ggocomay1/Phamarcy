package common;

import java.awt.Color;

/**
 * ColorScheme - Design System MEPHAR Pharmacy
 * Tone sáng, sạch, y tế - chuyên nghiệp
 * @version 5.0 – Pharmacy Theme
 */
public class ColorScheme {

    // ========== BRANDING (Teal y tế) ==========
    public static final Color PRIMARY = new Color(20, 184, 166);         // #14B8A6 teal
    public static final Color PRIMARY_HOVER = new Color(13, 148, 136);   // #0D9488
    public static final Color PRIMARY_DARK = new Color(15, 118, 110);    // #0F766E
    public static final Color PRIMARY_LIGHT = new Color(204, 251, 241);  // #CCFBF1

    // ========== SIDEBAR (Navy y tế) ==========
    public static final Color SIDEBAR_BG = new Color(15, 39, 68);             // #0F2744
    public static final Color SIDEBAR_TEXT = new Color(160, 180, 200);         // muted
    public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;
    public static final Color SIDEBAR_HOVER = new Color(24, 58, 90);           // #183A5A
    public static final Color SIDEBAR_ACTIVE = new Color(20, 184, 166);        // teal
    public static final Color SIDEBAR_DIVIDER = new Color(40, 65, 95);

    // ========== BACKGROUNDS ==========
    public static final Color BACKGROUND = new Color(245, 248, 251);     // #F5F8FB
    public static final Color PANEL_BG = Color.WHITE;

    // ========== TEXT ==========
    public static final Color TEXT_PRIMARY = new Color(31, 45, 61);      // #1F2D3D
    public static final Color TEXT_SECONDARY = new Color(107, 122, 140); // #6B7A8C
    public static final Color TEXT_MUTED = new Color(156, 169, 184);     // #9CA9B8
    public static final Color TEXT_WHITE = Color.WHITE;

    // ========== STATUS ==========
    public static final Color SUCCESS = new Color(34, 160, 107);         // #22A06B
    public static final Color SUCCESS_LIGHT = new Color(220, 252, 231);
    public static final Color WARNING = new Color(245, 158, 11);         // #F59E0B
    public static final Color WARNING_LIGHT = new Color(254, 249, 195);
    public static final Color DANGER = new Color(229, 72, 77);           // #E5484D
    public static final Color DANGER_LIGHT = new Color(254, 226, 226);
    public static final Color DANGER_HOVER = new Color(200, 50, 50);
    public static final Color INFO = new Color(20, 184, 166);            // teal
    public static final Color INFO_LIGHT = new Color(204, 251, 241);

    // ========== BORDER ==========
    public static final Color BORDER = new Color(227, 234, 242);         // #E3EAF2
    public static final Color BORDER_FOCUS = new Color(20, 184, 166);

    // ========== INPUT ==========
    public static final Color INPUT_BG = Color.WHITE;
    public static final Color INPUT_FOCUS = Color.WHITE;
    public static final Color INPUT_DISABLED = new Color(249, 250, 251);
    public static final Color INPUT_ERROR = new Color(254, 226, 226);

    // ========== CHART ==========
    public static final Color CHART_1 = new Color(20, 184, 166);
    public static final Color CHART_2 = new Color(34, 160, 107);
    public static final Color CHART_3 = new Color(245, 158, 11);
    public static final Color CHART_4 = new Color(229, 72, 77);
    public static final Color CHART_5 = new Color(139, 92, 246);

    // ========== TABLE ==========
    public static final Color NEUTRAL = new Color(229, 231, 235);
    public static final Color SHADOW = new Color(0, 0, 0, 8);
    public static final Color TABLE_HEADER_BG = new Color(245, 248, 251);
    public static final Color TABLE_ALT_ROW = new Color(249, 250, 252);
    public static final Color TABLE_HOVER = new Color(240, 245, 250);

    // ========== SPACING ==========
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 12;
    public static final int SPACING_LG = 16;
    public static final int SPACING_XL = 20;
    public static final int SPACING_2XL = 24;

    // ========== RADIUS ==========
    public static final int RADIUS_SM = 6;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;

    // ========== COMPONENT HEIGHTS ==========
    public static final int INPUT_HEIGHT = 40;
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_HEIGHT_LG = 48;

    // ========== UTILS ==========
    public static Color getHoverColor(Color color) {
        return color.darker();
    }
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
