package common;

import java.awt.Color;

/**
 * ColorScheme - Design System cho toàn bộ ứng dụng MEPHAR
 * Modern Admin Desktop App Theme
 * 
 * @version 4.0
 */
public class ColorScheme {

	// ========== BRANDING ==========
	public static final Color PRIMARY = new Color(37, 99, 235);       // #2563EB
	public static final Color PRIMARY_HOVER = new Color(29, 78, 216); // #1D4ED8
	public static final Color PRIMARY_DARK = new Color(30, 64, 175);  // #1E40AF
	public static final Color PRIMARY_LIGHT = new Color(219, 234, 254); // #DBEAFE

	// ========== SIDEBAR ==========
	public static final Color SIDEBAR_BG = new Color(15, 23, 42);         // #0F172A
	public static final Color SIDEBAR_TEXT = new Color(148, 163, 184);     // #94A3B8
	public static final Color SIDEBAR_TEXT_ACTIVE = Color.WHITE;
	public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);       // #1E293B
	public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);     // #2563EB
	public static final Color SIDEBAR_DIVIDER = new Color(51, 65, 85);     // #334155

	// ========== BACKGROUNDS ==========
	public static final Color BACKGROUND = new Color(248, 250, 252);  // #F8FAFC
	public static final Color PANEL_BG = Color.WHITE;                  // #FFFFFF

	// ========== TEXT ==========
	public static final Color TEXT_PRIMARY = new Color(17, 24, 39);    // #111827
	public static final Color TEXT_SECONDARY = new Color(107, 114, 128); // #6B7280
	public static final Color TEXT_MUTED = new Color(156, 163, 175);   // #9CA3AF
	public static final Color TEXT_WHITE = Color.WHITE;

	// ========== STATUS ==========
	public static final Color SUCCESS = new Color(22, 163, 74);        // #16A34A
	public static final Color SUCCESS_LIGHT = new Color(220, 252, 231); // #DCFCE7
	public static final Color WARNING = new Color(245, 158, 11);       // #F59E0B
	public static final Color WARNING_LIGHT = new Color(254, 249, 195); // #FEF9C3
	public static final Color DANGER = new Color(220, 38, 38);         // #DC2626
	public static final Color DANGER_LIGHT = new Color(254, 226, 226); // #FEE2E2
	public static final Color INFO = new Color(37, 99, 235);           // #2563EB
	public static final Color INFO_LIGHT = new Color(219, 234, 254);   // #DBEAFE

	// ========== BORDER ==========
	public static final Color BORDER = new Color(229, 231, 235);       // #E5E7EB
	public static final Color BORDER_FOCUS = new Color(37, 99, 235);   // #2563EB

	// ========== INPUT ==========
	public static final Color INPUT_BG = Color.WHITE;
	public static final Color INPUT_FOCUS = Color.WHITE;
	public static final Color INPUT_DISABLED = new Color(249, 250, 251); // #F9FAFB
	public static final Color INPUT_ERROR = new Color(254, 226, 226);    // #FEE2E2

	// ========== CHART COLORS ==========
	public static final Color CHART_1 = new Color(37, 99, 235);
	public static final Color CHART_2 = new Color(22, 163, 74);
	public static final Color CHART_3 = new Color(245, 158, 11);
	public static final Color CHART_4 = new Color(239, 68, 68);
	public static final Color CHART_5 = new Color(139, 92, 246);

	// ========== EXTRAS ==========
	public static final Color DANGER_HOVER = new Color(185, 28, 28);   // #B91C1C
	public static final Color NEUTRAL = new Color(229, 231, 235);      // #E5E7EB
	public static final Color SHADOW = new Color(0, 0, 0, 8);
	public static final Color TABLE_HEADER_BG = new Color(249, 250, 251); // #F9FAFB
	public static final Color TABLE_ALT_ROW = new Color(249, 250, 251);
	public static final Color TABLE_HOVER = new Color(243, 244, 246);     // #F3F4F6

	// ========== SPACING (as int px) ==========
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
