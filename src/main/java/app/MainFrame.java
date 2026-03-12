package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import common.ColorScheme;
import common.ConnectDB;
import entity.NguoiDung;
import panels.BaoCaoPanel;
import panels.BanHangPanel;
import panels.DashboardPanel;
import panels.KhachHangPanel;
import panels.LoHangPanel;
import panels.NguoiDungPanel;
import panels.NhaCungCapPanel;
import panels.NhapHangPanel;
import panels.SanPhamPanel;
import panels.SidebarPanel;

/**
 * MainFrame - Frame chính của ứng dụng với sidebar navigation cải tiến
 * Updated: Premium Dark Sidebar Support with Rounded Buttons
 * 
 * @author Improved by Agent
 * @version 3.0
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private NguoiDung currentUser;
	private SidebarPanel sidebarPanel;
	private JPanel mainContentPanel;
	private java.awt.CardLayout cardLayout;

	// Panel references for refreshing data (Requirement: AUTO_REFRESH_DATA_AFTER_IMPORT)
	private SanPhamPanel sanPhamPanel;
	private LoHangPanel loHangPanel;

	/**
	 * Create the frame.
	 */
	public MainFrame(NguoiDung nguoiDung) {
		this.currentUser = nguoiDung;
		
		// Setup FlatLaf
		try {
			javax.swing.UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
			
			// Customize FlatLaf colors
			javax.swing.UIManager.put("Panel.background", ColorScheme.BACKGROUND);
			
			// Force Font Tiếng Việt
			java.awt.Font defaultFont = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
			javax.swing.UIManager.put("defaultFont", defaultFont);
			javax.swing.UIManager.put("OptionPane.messageFont", defaultFont);
			javax.swing.UIManager.put("Table.font", defaultFont);
			
			javax.swing.UIManager.put("Button.arc", 12);
			javax.swing.UIManager.put("TextComponent.arc", 8);
			javax.swing.UIManager.put("Component.focusWidth", 2);
			javax.swing.UIManager.put("Component.focusColor", ColorScheme.BORDER_FOCUS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setTitle("Cửa hàng thuốc - " + currentUser.getHoTen() + " (" + currentUser.getVaiTro() + ")");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);

		// Đóng connection pool khi đóng frame
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int option = javax.swing.JOptionPane.showConfirmDialog(
					MainFrame.this,
					"Bạn có chắc chắn muốn thoát?",
					"Xác nhận",
					javax.swing.JOptionPane.YES_NO_OPTION
				);
				if (option == javax.swing.JOptionPane.YES_OPTION) {
					ConnectDB.closeConnectionPool();
					System.exit(0);
				}
			}
		});

		var contentPane = new JPanel();
		contentPane.setLayout(new java.awt.BorderLayout(0, 0));
		setContentPane(contentPane);

		// Main Content (Header + Body)
		createMainContentContainer();

		// Load dashboard by default và highlight menu
		if (currentUser.getVaiTro().equalsIgnoreCase("Nhân viên")) {
			handleMenuClick("banhang");
		} else {
			handleMenuClick("dashboard");
		}
	}

	/**
	 * Create Main Content Container (Header + Content)
	 */
	private void createMainContentContainer() {
		var container = new JPanel(new BorderLayout());
		container.setBackground(ColorScheme.BACKGROUND);
		
		// Header ở trên cùng của Main Content
		var headerPanel = createHeaderPanel();
		container.add(headerPanel, BorderLayout.NORTH);

		// Main Content
		mainContentPanel = new JPanel();
		cardLayout = new java.awt.CardLayout();
		mainContentPanel.setLayout(cardLayout);
		mainContentPanel.setBackground(ColorScheme.BACKGROUND);
		mainContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		
		// Khởi tạo các panel 1 lần duy nhất để giữ trạng thái (UI State Persistence)
		sanPhamPanel = new SanPhamPanel(currentUser);
		loHangPanel = new LoHangPanel(currentUser);

		mainContentPanel.add(new DashboardPanel(currentUser), "dashboard");
		mainContentPanel.add(new BanHangPanel(currentUser), "banhang");
		mainContentPanel.add(new NhapHangPanel(currentUser), "nhaphang");
		mainContentPanel.add(sanPhamPanel, "sanpham");
		mainContentPanel.add(loHangPanel, "lohang");
		mainContentPanel.add(new KhachHangPanel(currentUser), "khachhang");
		mainContentPanel.add(new NhaCungCapPanel(currentUser), "nhacungcap");
		mainContentPanel.add(new BaoCaoPanel(currentUser), "baocao");
		mainContentPanel.add(new NguoiDungPanel(currentUser), "nguoidung");

		container.add(mainContentPanel, BorderLayout.CENTER);

		sidebarPanel = new SidebarPanel(currentUser, this::handleMenuClick);
		getContentPane().add(sidebarPanel, BorderLayout.WEST);
		getContentPane().add(container, BorderLayout.CENTER);
	}

	/**
	 * Tạo header bar (Updated)
	 */
	private JPanel createHeaderPanel() {
		var headerPanel = new JPanel();
		headerPanel.setBackground(ColorScheme.PANEL_BG);
		headerPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER),
			BorderFactory.createEmptyBorder(0, 24, 0, 24)
		));
		headerPanel.setPreferredSize(new Dimension(0, 52));
		headerPanel.setLayout(new BorderLayout());

		// Left: App name
		var lblPageTitle = new JLabel("MEPHAR QUẢN LÝ");
		lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblPageTitle.setForeground(ColorScheme.PRIMARY);
		headerPanel.add(lblPageTitle, BorderLayout.WEST);

		// Right: User badge + Logout
		var userPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 8));
		userPanel.setOpaque(false);

		// User info badge
		var lblUser = new JLabel("👤 " + currentUser.getHoTen());
		lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblUser.setForeground(ColorScheme.TEXT_PRIMARY);
		lblUser.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
			new javax.swing.border.EmptyBorder(6, 12, 6, 12)
		));
		lblUser.setOpaque(true);
		lblUser.setBackground(ColorScheme.BACKGROUND);
		userPanel.add(lblUser);

		// Outline logout button
		var btnLogout = common.UIHelper.createOutlineButton("Đăng xuất", ColorScheme.DANGER);
		btnLogout.addActionListener(e -> {
			int option = javax.swing.JOptionPane.showConfirmDialog(
				this, "Bạn có muốn đăng xuất không?", "Đăng xuất", javax.swing.JOptionPane.YES_NO_OPTION
			);
			if (option == javax.swing.JOptionPane.YES_OPTION) {
				this.dispose();
				new LoginFrame().setVisible(true);
			}
		});
		userPanel.add(btnLogout);

		headerPanel.add(userPanel, BorderLayout.EAST);
		return headerPanel;
	}

	/**
	 * Xử lý click menu
	 */
	private void handleMenuClick(String action) {
		// Update active state in sidebar
		if (sidebarPanel != null) {
			sidebarPanel.updateActiveState(action);
		}
		
		// Show panel using CardLayout
		if (cardLayout != null && mainContentPanel != null) {
			cardLayout.show(mainContentPanel, action);
		}
	}

	/**
	 * Làm mới toàn bộ dữ liệu quan trọng sau khi nhập hàng (Requirement: AUTO_REFRESH_DATA_AFTER_IMPORT)
	 */
	public void refreshAllData() {
		if (sanPhamPanel != null) {
			sanPhamPanel.loadData();
		}
		if (loHangPanel != null) {
			loHangPanel.loadData();
		}
	}
}
