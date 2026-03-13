package common;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * UIHelper - Design System Helper cho MEPHAR
 * Cung cấp factory methods để tạo UI components đồng bộ
 * 
 * @version 4.0
 */
public class UIHelper {

	// ========== FONTS ==========
	public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
	public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 18);
	public static final Font FONT_SUBSECTION = new Font("Segoe UI", Font.BOLD, 15);
	public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 13);
	public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
	public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
	public static final Font FONT_BUTTON_SM = new Font("Segoe UI", Font.BOLD, 12);
	public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
	public static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);

	// ========== BUTTON FACTORY ==========

	public static JButton createStyledButton(String text, Color bgColor) {
		var btn = new JButton(text);
		btn.setFont(FONT_BUTTON);
		btn.setBackground(bgColor);
		btn.setForeground(isLightColor(bgColor) ? ColorScheme.TEXT_PRIMARY : ColorScheme.TEXT_WHITE);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.putClientProperty("JButton.buttonType", "roundRect");

		Color hoverColor = getHoverColor(bgColor);
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override public void mouseEntered(java.awt.event.MouseEvent e) {
				if (btn.isEnabled()) btn.setBackground(hoverColor);
			}
			@Override public void mouseExited(java.awt.event.MouseEvent e) {
				if (btn.isEnabled()) btn.setBackground(bgColor);
			}
		});

		return btn;
	}

	public static JButton createPrimaryButton(String text) {
		return createStyledButton(text, ColorScheme.PRIMARY);
	}

	public static JButton createSuccessButton(String text) {
		return createStyledButton(text, ColorScheme.SUCCESS);
	}

	public static JButton createDangerButton(String text) {
		return createStyledButton(text, ColorScheme.DANGER);
	}

	public static JButton createWarningButton(String text) {
		return createStyledButton(text, ColorScheme.WARNING);
	}

	public static JButton createNeutralButton(String text) {
		return createStyledButton(text, ColorScheme.NEUTRAL);
	}

	/** Outline button - nền trong, border màu */
	public static JButton createOutlineButton(String text, Color color) {
		var btn = new JButton(text);
		btn.setFont(FONT_BUTTON_SM);
		btn.setBackground(ColorScheme.PANEL_BG);
		btn.setForeground(color);
		btn.setFocusPainted(false);
		btn.setBorderPainted(true);
		btn.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(color, 1),
			new EmptyBorder(6, 16, 6, 16)
		));
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.putClientProperty("JButton.buttonType", "roundRect");

		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override public void mouseEntered(java.awt.event.MouseEvent e) {
				if (btn.isEnabled()) {
					btn.setBackground(ColorScheme.withAlpha(color, 15));
					btn.setForeground(color.darker());
				}
			}
			@Override public void mouseExited(java.awt.event.MouseEvent e) {
				if (btn.isEnabled()) {
					btn.setBackground(ColorScheme.PANEL_BG);
					btn.setForeground(color);
				}
			}
		});

		return btn;
	}

	// ========== INPUT FACTORY ==========

	public static JTextField createStyledTextField() {
		var txt = new JTextField();
		txt.setFont(FONT_INPUT);
		txt.setPreferredSize(new Dimension(0, ColorScheme.INPUT_HEIGHT));
		txt.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
			new EmptyBorder(8, 12, 8, 12)
		));
		txt.setBackground(ColorScheme.INPUT_BG);
		return txt;
	}

	public static JTextField createStyledTextFieldDisabled() {
		var txt = createStyledTextField();
		txt.setEditable(false);
		txt.setBackground(ColorScheme.INPUT_DISABLED);
		return txt;
	}

	// ========== CARD / SECTION FACTORY ==========

	/** Tạo card panel trắng với shadow nhẹ */
	public static JPanel createCard() {
		var card = new JPanel();
		card.setBackground(ColorScheme.PANEL_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
			new EmptyBorder(ColorScheme.SPACING_LG, ColorScheme.SPACING_LG,
				ColorScheme.SPACING_LG, ColorScheme.SPACING_LG)
		));
		return card;
	}

	/** Tạo section label */
	public static JLabel createSectionLabel(String text) {
		var lbl = new JLabel(text);
		lbl.setFont(FONT_SUBSECTION);
		lbl.setForeground(ColorScheme.TEXT_PRIMARY);
		return lbl;
	}

	/** Tạo field label */
	public static JLabel createFieldLabel(String text) {
		var lbl = new JLabel(text);
		lbl.setFont(FONT_LABEL);
		lbl.setForeground(ColorScheme.TEXT_PRIMARY);
		return lbl;
	}

	/** Tạo text phụ */
	public static JLabel createMutedLabel(String text) {
		var lbl = new JLabel(text);
		lbl.setFont(FONT_SMALL);
		lbl.setForeground(ColorScheme.TEXT_MUTED);
		return lbl;
	}

	// ========== INFO BANNER ==========

	public static JPanel createInfoBanner(String htmlMessage) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.INFO_LIGHT);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(147, 197, 253), 1),
			BorderFactory.createEmptyBorder(10, 14, 10, 14)
		));

		JLabel lblMessage = new JLabel(htmlMessage);
		lblMessage.setFont(FONT_LABEL);
		lblMessage.setForeground(new Color(30, 64, 175));

		panel.add(lblMessage, BorderLayout.CENTER);
		return panel;
	}

	// ========== EMPTY STATE ==========

	public static JPanel createEmptyState(String icon, String title, String subtitle) {
		var panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setOpaque(false);
		panel.setBorder(new EmptyBorder(40, 20, 40, 20));

		var lblIcon = new JLabel(icon);
		lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
		lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblIcon);

		panel.add(Box.createVerticalStrut(12));

		var lblTitle = new JLabel(title);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
		lblTitle.setForeground(ColorScheme.TEXT_SECONDARY);
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(lblTitle);

		if (subtitle != null) {
			panel.add(Box.createVerticalStrut(6));
			var lblSub = new JLabel(subtitle);
			lblSub.setFont(FONT_SMALL);
			lblSub.setForeground(ColorScheme.TEXT_MUTED);
			lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(lblSub);
		}

		return panel;
	}

	// ========== UTILS ==========

	private static boolean isLightColor(Color color) {
		double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
		return luminance > 200;
	}

	private static Color getHoverColor(Color color) {
		return color.darker();
	}
}
