package panels;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import common.ColorScheme;
import entity.NguoiDung;

/**
 * SidebarPanel - Modern dark sidebar navigation
 * Icons + smooth hover + blue active state
 * 
 * @version 4.0
 */
public class SidebarPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private Consumer<String> onMenuSelection;
	private String currentAction = "dashboard";
	private Map<String, JButton> menuItems = new HashMap<>();
	private JPanel menuPanel;

	// Menu icon mapping
	private static final Map<String, String> MENU_ICONS = Map.of(
		"dashboard", "📊",
		"banhang", "🛒",
		"nhaphang", "📥",
		"sanpham", "💊",
		"lohang", "📦",
		"khachhang", "👥",
		"nhacungcap", "🏭",
		"baocao", "📈",
		"nguoidung", "👤"
	);

	public SidebarPanel(NguoiDung currentUser, Consumer<String> onMenuSelection) {
		this.onMenuSelection = onMenuSelection;
		initialize(currentUser);
	}

	private void initialize(NguoiDung currentUser) {
		setBackground(ColorScheme.SIDEBAR_BG);
		setPreferredSize(new Dimension(250, 0));
		setLayout(new BorderLayout());

		// === Logo Header ===
		var headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setPreferredSize(new Dimension(250, 80));
		headerPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.SIDEBAR_DIVIDER),
			new EmptyBorder(0, 24, 0, 24)
		));

		var logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		logoPanel.setOpaque(false);

		var lblIcon = new JLabel("💊");
		lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		lblIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
		logoPanel.add(lblIcon);

		var lblLogo = new JLabel("MEPHAR");
		lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblLogo.setForeground(Color.WHITE);
		logoPanel.add(lblLogo);

		headerPanel.add(logoPanel, BorderLayout.WEST);
		add(headerPanel, BorderLayout.NORTH);

		// === Navigation Menu ===
		menuPanel = new JPanel();
		menuPanel.setOpaque(false);
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

		String role = currentUser.getVaiTro();
		addMenuItemIfAllowed(menuPanel, "Tổng quan", "dashboard", role);
		addMenuItemIfAllowed(menuPanel, "Bán hàng", "banhang", role);
		addMenuItemIfAllowed(menuPanel, "Nhập hàng", "nhaphang", role);
		addMenuItemIfAllowed(menuPanel, "Sản phẩm", "sanpham", role);
		addMenuItemIfAllowed(menuPanel, "Lô hàng", "lohang", role);
		addMenuItemIfAllowed(menuPanel, "Khách hàng", "khachhang", role);
		addMenuItemIfAllowed(menuPanel, "Nhà cung cấp", "nhacungcap", role);
		addMenuItemIfAllowed(menuPanel, "Báo cáo", "baocao", role);
		addMenuItemIfAllowed(menuPanel, "Người dùng", "nguoidung", role);

		var wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setOpaque(false);
		wrapperPanel.add(menuPanel, BorderLayout.NORTH);

		// Scrollable if too many items
		var scrollPane = new JScrollPane(wrapperPanel);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		add(scrollPane, BorderLayout.CENTER);

		// === Footer: Version ===
		var footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		footerPanel.setOpaque(false);
		footerPanel.setBorder(new EmptyBorder(8, 0, 12, 0));
		var lblVersion = new JLabel("v4.0 · MEPHAR");
		lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblVersion.setForeground(ColorScheme.withAlpha(Color.WHITE, 60));
		footerPanel.add(lblVersion);
		add(footerPanel, BorderLayout.SOUTH);
	}

	private void addMenuItemIfAllowed(JPanel parent, String text, String action, String role) {
		if (utils.PermissionManager.hasAccess(role, action)) {
			addMenuItem(parent, text, action);
		}
	}

	private void addMenuItem(JPanel parent, String text, String action) {
		String icon = MENU_ICONS.getOrDefault(action, "•");
		var btn = new JButton("  " + icon + "   " + text);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		btn.setForeground(ColorScheme.SIDEBAR_TEXT);
		btn.setBackground(ColorScheme.SIDEBAR_BG);
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
		btn.setAlignmentX(Component.LEFT_ALIGNMENT);
		btn.putClientProperty("JButton.buttonType", "roundRect");
		btn.putClientProperty("Component.arc", 10);
		btn.setBorder(new EmptyBorder(10, 14, 10, 14));

		btn.addActionListener(e -> {
			updateActiveState(action);
			if (onMenuSelection != null) {
				onMenuSelection.accept(action);
			}
		});

		// Hover effect
		btn.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				if (btn.isEnabled() && !action.equals(currentAction)) {
					btn.setBackground(ColorScheme.SIDEBAR_HOVER);
					btn.setForeground(Color.WHITE);
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				if (btn.isEnabled() && !action.equals(currentAction)) {
					btn.setBackground(ColorScheme.SIDEBAR_BG);
					btn.setForeground(ColorScheme.SIDEBAR_TEXT);
				}
			}
		});

		btn.addPropertyChangeListener("enabled", evt -> {
			if (!(boolean) evt.getNewValue()) {
				btn.setForeground(new Color(75, 85, 99));
			} else if (!action.equals(currentAction)) {
				btn.setForeground(ColorScheme.SIDEBAR_TEXT);
			}
		});

		parent.add(Box.createRigidArea(new Dimension(0, 3)));
		parent.add(btn);
		menuItems.put(action, btn);
	}

	public void updateActiveState(String action) {
		// Reset previous
		if (currentAction != null && menuItems.containsKey(currentAction)) {
			var oldBtn = menuItems.get(currentAction);
			oldBtn.setBackground(ColorScheme.SIDEBAR_BG);
			oldBtn.setForeground(oldBtn.isEnabled() ? ColorScheme.SIDEBAR_TEXT : new Color(75, 85, 99));
			oldBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		}

		// Set new active
		currentAction = action;
		if (menuItems.containsKey(action)) {
			var newBtn = menuItems.get(action);
			if (newBtn.isEnabled()) {
				newBtn.setBackground(ColorScheme.SIDEBAR_ACTIVE);
				newBtn.setForeground(Color.WHITE);
				newBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
			}
		}
	}
}
