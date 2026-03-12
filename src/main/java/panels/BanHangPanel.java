package panels;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import common.ColorScheme;
import common.UIHelper;
import components.ChiTietHoaDonTableModel;
import dao.ChiTietHoaDonDao;
import dao.HoaDonBanDao;
import dao.KhachHangDao;
import dao.SanPhamDao;
import entity.KhachHang;
import entity.NguoiDung;

/**
 * BanHangPanel - Professional POS interface
 * Modern admin desktop design with inline customer management
 * 
 * @version 4.0
 */
public class BanHangPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// DAOs
	private HoaDonBanDao hoaDonDao;
	private ChiTietHoaDonDao chiTietDao;
	private SanPhamDao sanPhamDao;
	private KhachHangDao khachHangDao;

	// Table
	private ChiTietHoaDonTableModel tableModel;
	private JTable table;
	private JPanel emptyStatePanel;
	private JScrollPane tableScrollPane;

	// Invoice fields
	private JTextField txtMaHoaDon;
	private JComboBox<String> comboSanPham;
	private JTextField txtSoLuong;
	private JTextField txtGiaBan;
	private JTextArea txtGhiChu;
	private JLabel lblTongTien;

	// Customer fields
	private JTextField txtTimKhachHang;
	private JList<String> listSuggestions;
	private DefaultListModel<String> suggestionModel;
	private JPopupMenu popupSuggestions;
	private JTextField txtKhachHangTen;
	private JTextField txtKhachHangSDT;
	private JTextField txtKhachHangDiaChi;
	private JLabel lblSelectedCustomer;
	private JButton btnXoaKhachHang;

	// Buttons
	private JButton btnTaoHoaDon;
	private JButton btnThemSanPham;
	private JButton btnThanhToan;
	private JButton btnHuy;
	private JButton btnSuaSoLuong;
	private JButton btnXoaSanPham;

	// State
	private Integer currentMaHoaDon;
	private Integer selectedMaKhachHang;
	private NguoiDung currentUser;
	private List<KhachHang> cachedCustomers;
	private javax.swing.Timer searchTimer;

	private static final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

	public BanHangPanel(NguoiDung currentUser) {
		this.currentUser = currentUser;
		hoaDonDao = new HoaDonBanDao();
		chiTietDao = new ChiTietHoaDonDao();
		sanPhamDao = new SanPhamDao();
		khachHangDao = new KhachHangDao();
		initialize();
		resetForm();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(ColorScheme.BACKGROUND);
		setBorder(new EmptyBorder(20, 24, 20, 24));

		// === HEADER ===
		add(createPageHeader(), BorderLayout.NORTH);

		// === MAIN 2-COLUMN LAYOUT ===
		var mainPanel = new JPanel(new BorderLayout(20, 0));
		mainPanel.setOpaque(false);
		mainPanel.add(createLeftColumn(), BorderLayout.WEST);
		mainPanel.add(createRightColumn(), BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);

		// F9 shortcut
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke("F9"), "thanhtoan");
		getActionMap().put("thanhtoan", new AbstractAction() {
			@Override public void actionPerformed(java.awt.event.ActionEvent e) {
				handleThanhToan();
			}
		});
	}

	// ===================== PAGE HEADER =====================

	private JPanel createPageHeader() {
		var header = new JPanel(new BorderLayout(0, 10));
		header.setOpaque(false);
		header.setBorder(new EmptyBorder(0, 0, 16, 0));

		var lblTitle = new JLabel("Bán hàng");
		lblTitle.setFont(UIHelper.FONT_TITLE);
		lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
		header.add(lblTitle, BorderLayout.WEST);

		var banner = UIHelper.createInfoBanner(
			"<html>ℹ️ <b>Quy trình:</b> Tạo hóa đơn → Chọn sản phẩm → Thanh toán (F9). " +
			"Hệ thống xuất kho tự động theo <b>FEFO</b>.</html>"
		);
		header.add(banner, BorderLayout.SOUTH);

		return header;
	}

	// ===================== LEFT COLUMN (36%) =====================

	private JPanel createLeftColumn() {
		var column = new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.setOpaque(false);
		column.setPreferredSize(new Dimension(400, 0));

		// Wraps in scrollpane for small screens
		var scrollContent = new JPanel();
		scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
		scrollContent.setOpaque(false);

		scrollContent.add(createInvoiceCard());
		scrollContent.add(Box.createVerticalStrut(12));
		scrollContent.add(createCustomerCard());
		scrollContent.add(Box.createVerticalStrut(12));
		scrollContent.add(createProductCard());
		scrollContent.add(Box.createVerticalStrut(12));
		scrollContent.add(createNoteCard());
		scrollContent.add(Box.createVerticalStrut(12));
		scrollContent.add(createActionCard());

		var scroll = new JScrollPane(scrollContent);
		scroll.setBorder(null);
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(20);

		column.add(scroll);
		return column;
	}

	// ----- Card 1: Invoice Info -----
	private JPanel createInvoiceCard() {
		var card = createCard("📋 Thông tin hóa đơn");

		addFormField(card, "Mã hóa đơn", null);
		txtMaHoaDon = UIHelper.createStyledTextFieldDisabled();
		txtMaHoaDon.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtMaHoaDon);

		card.add(Box.createVerticalStrut(12));

		btnTaoHoaDon = UIHelper.createPrimaryButton("🧾 Tạo hóa đơn mới");
		btnTaoHoaDon.setFont(UIHelper.FONT_BUTTON);
		btnTaoHoaDon.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.BUTTON_HEIGHT));
		btnTaoHoaDon.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnTaoHoaDon.addActionListener(e -> handleTaoHoaDon());
		card.add(btnTaoHoaDon);

		return card;
	}

	// ----- Card 2: Customer -----
	private JPanel createCustomerCard() {
		var card = createCard("👤 Khách hàng");

		// Selected customer display
		var selectedPanel = new JPanel(new BorderLayout(8, 0));
		selectedPanel.setOpaque(false);
		selectedPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		selectedPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		selectedPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER),
			new EmptyBorder(6, 10, 6, 6)
		));
		selectedPanel.setBackground(ColorScheme.INPUT_DISABLED);
		selectedPanel.setOpaque(true);

		lblSelectedCustomer = new JLabel("Khách lẻ (chưa chọn)");
		lblSelectedCustomer.setFont(UIHelper.FONT_LABEL);
		lblSelectedCustomer.setForeground(ColorScheme.TEXT_MUTED);
		selectedPanel.add(lblSelectedCustomer, BorderLayout.CENTER);

		btnXoaKhachHang = new JButton("✕");
		btnXoaKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 11));
		btnXoaKhachHang.setForeground(ColorScheme.DANGER);
		btnXoaKhachHang.setContentAreaFilled(false);
		btnXoaKhachHang.setBorderPainted(false);
		btnXoaKhachHang.setFocusPainted(false);
		btnXoaKhachHang.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnXoaKhachHang.setPreferredSize(new Dimension(28, 28));
		btnXoaKhachHang.setVisible(false);
		btnXoaKhachHang.addActionListener(e -> clearSelectedCustomer());
		selectedPanel.add(btnXoaKhachHang, BorderLayout.EAST);
		card.add(selectedPanel);

		card.add(Box.createVerticalStrut(10));

		// Search field
		addFormField(card, "🔍 Tìm khách hàng", null);
		txtTimKhachHang = UIHelper.createStyledTextField();
		txtTimKhachHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		txtTimKhachHang.setToolTipText("Nhập tên hoặc SĐT để tìm kiếm");
		card.add(txtTimKhachHang);
		setupAutocomplete();

		// Divider
		card.add(Box.createVerticalStrut(14));
		var divider = new JSeparator(SwingConstants.HORIZONTAL);
		divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		divider.setForeground(ColorScheme.BORDER);
		card.add(divider);
		card.add(Box.createVerticalStrut(14));

		// New customer form
		var lblNewCust = UIHelper.createSectionLabel("➕ Thêm khách hàng mới");
		lblNewCust.setFont(UIHelper.FONT_LABEL_BOLD);
		lblNewCust.setForeground(ColorScheme.PRIMARY);
		lblNewCust.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblNewCust);
		card.add(Box.createVerticalStrut(10));

		addFormField(card, "Họ tên *", null);
		txtKhachHangTen = UIHelper.createStyledTextField();
		txtKhachHangTen.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtKhachHangTen);
		card.add(Box.createVerticalStrut(8));

		addFormField(card, "Số điện thoại *", null);
		txtKhachHangSDT = UIHelper.createStyledTextField();
		txtKhachHangSDT.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtKhachHangSDT);
		card.add(Box.createVerticalStrut(8));

		addFormField(card, "Địa chỉ", null);
		txtKhachHangDiaChi = UIHelper.createStyledTextField();
		txtKhachHangDiaChi.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtKhachHangDiaChi);
		card.add(Box.createVerticalStrut(10));

		var btnSaveCustomer = UIHelper.createPrimaryButton("💾 Lưu khách hàng mới");
		btnSaveCustomer.setFont(UIHelper.FONT_BUTTON_SM);
		btnSaveCustomer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
		btnSaveCustomer.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnSaveCustomer.addActionListener(e -> handleThemKhachHangMoi());
		card.add(btnSaveCustomer);

		return card;
	}

	// ----- Card 3: Product -----
	private JPanel createProductCard() {
		var card = createCard("💊 Thêm sản phẩm");

		addFormField(card, "Sản phẩm *", null);
		comboSanPham = new JComboBox<>();
		comboSanPham.setFont(UIHelper.FONT_INPUT);
		comboSanPham.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		loadSanPham();
		card.add(comboSanPham);
		card.add(Box.createVerticalStrut(8));

		addFormField(card, "Số lượng *", null);
		txtSoLuong = UIHelper.createStyledTextField();
		txtSoLuong.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtSoLuong);
		card.add(Box.createVerticalStrut(8));

		addFormField(card, "Giá bán (để trống = mặc định)", null);
		txtGiaBan = UIHelper.createStyledTextField();
		txtGiaBan.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.INPUT_HEIGHT));
		card.add(txtGiaBan);
		card.add(Box.createVerticalStrut(12));

		btnThemSanPham = UIHelper.createSuccessButton("➕ Thêm vào hóa đơn");
		btnThemSanPham.setFont(UIHelper.FONT_BUTTON);
		btnThemSanPham.setMaximumSize(new Dimension(Integer.MAX_VALUE, ColorScheme.BUTTON_HEIGHT));
		btnThemSanPham.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnThemSanPham.addActionListener(e -> handleThemSanPham());
		card.add(btnThemSanPham);

		return card;
	}

	// ----- Card 4: Note -----
	private JPanel createNoteCard() {
		var card = createCard("📝 Ghi chú");

		txtGhiChu = new JTextArea(2, 20);
		txtGhiChu.setFont(UIHelper.FONT_INPUT);
		txtGhiChu.setLineWrap(true);
		txtGhiChu.setWrapStyleWord(true);
		txtGhiChu.setBorder(new EmptyBorder(8, 10, 8, 10));
		var scrollGhiChu = new JScrollPane(txtGhiChu);
		scrollGhiChu.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
		scrollGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
		card.add(scrollGhiChu);

		return card;
	}

	// ----- Card 5: Actions -----
	private JPanel createActionCard() {
		var card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setOpaque(false);
		card.setAlignmentX(Component.LEFT_ALIGNMENT);
		// No card border - just a transparent action area
		return card;
	}

	// ===================== RIGHT COLUMN (64%) =====================

	private JPanel createRightColumn() {
		var card = new JPanel(new BorderLayout(0, 0));
		card.setBackground(ColorScheme.PANEL_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
			new EmptyBorder(20, 20, 20, 20)
		));

		// Header with title
		var headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new EmptyBorder(0, 0, 14, 0));

		var lblTitle = new JLabel("📄 Chi tiết hóa đơn");
		lblTitle.setFont(UIHelper.FONT_SECTION);
		lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
		headerPanel.add(lblTitle, BorderLayout.WEST);
		card.add(headerPanel, BorderLayout.NORTH);

		// Table + empty state wrapper
		var tableWrapper = new JPanel(new BorderLayout(0, 10));
		tableWrapper.setOpaque(false);

		// Table
		tableModel = new ChiTietHoaDonTableModel();
		table = new JTable(tableModel);
		table.setFont(UIHelper.FONT_TABLE);
		table.setRowHeight(36);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setGridColor(new Color(243, 244, 246));
		table.setSelectionBackground(ColorScheme.PRIMARY_LIGHT);
		table.setSelectionForeground(ColorScheme.TEXT_PRIMARY);
		table.setIntercellSpacing(new Dimension(0, 0));

		// Hide MaCTHD column (index 0)
		table.getColumnModel().getColumn(0).setMinWidth(0);
		table.getColumnModel().getColumn(0).setMaxWidth(0);
		table.getColumnModel().getColumn(0).setPreferredWidth(0);

		// Header style
		JTableHeader header = table.getTableHeader();
		header.setFont(UIHelper.FONT_TABLE_HEADER);
		header.setBackground(ColorScheme.TABLE_HEADER_BG);
		header.setForeground(ColorScheme.TEXT_PRIMARY);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ColorScheme.BORDER));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

		// Alternating rows + alignment
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (!isSelected) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : ColorScheme.TABLE_ALT_ROW);
				}
				setBorder(new EmptyBorder(0, 12, 0, 12));

				// Right-align price columns (5=Giá bán, 6=Thành tiền)
				int modelCol = table.convertColumnIndexToModel(column);
				if (modelCol == 5 || modelCol == 6) {
					setHorizontalAlignment(SwingConstants.RIGHT);
				} else if (modelCol == 4) { // Số lượng = center
					setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					setHorizontalAlignment(SwingConstants.LEFT);
				}
				return c;
			}
		});

		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));

		// Empty state
		emptyStatePanel = UIHelper.createEmptyState(
			"🛒",
			"Chưa có sản phẩm trong hóa đơn",
			"Chọn sản phẩm ở khung bên trái để thêm vào đơn"
		);

		// CardLayout to switch between table and empty state
		var tableArea = new JPanel(new CardLayout());
		tableArea.setOpaque(false);
		tableArea.add(emptyStatePanel, "empty");
		tableArea.add(tableScrollPane, "table");
		tableWrapper.add(tableArea, BorderLayout.CENTER);

		// Edit/Delete toolbar
		var editToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		editToolbar.setOpaque(false);
		editToolbar.setBorder(new EmptyBorder(4, 0, 0, 0));

		btnSuaSoLuong = UIHelper.createOutlineButton("✏️ Sửa số lượng", ColorScheme.WARNING);
		btnSuaSoLuong.setEnabled(false);
		btnSuaSoLuong.addActionListener(e -> handleSuaSoLuong());
		editToolbar.add(btnSuaSoLuong);

		btnXoaSanPham = UIHelper.createOutlineButton("🗑️ Xóa sản phẩm", ColorScheme.DANGER);
		btnXoaSanPham.setEnabled(false);
		btnXoaSanPham.addActionListener(e -> handleXoaSanPham());
		editToolbar.add(btnXoaSanPham);

		var lblHint = UIHelper.createMutedLabel("← Chọn một dòng để thao tác");
		editToolbar.add(lblHint);

		tableWrapper.add(editToolbar, BorderLayout.SOUTH);

		// Enable/disable edit buttons based on selection
		table.getSelectionModel().addListSelectionListener(e -> {
			boolean hasSelection = table.getSelectedRow() >= 0;
			btnSuaSoLuong.setEnabled(hasSelection);
			btnXoaSanPham.setEnabled(hasSelection);
		});

		card.add(tableWrapper, BorderLayout.CENTER);

		// ===== BOTTOM: Total + Actions =====
		var bottomPanel = new JPanel(new BorderLayout(0, 14));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

		// Total panel with highlight background
		var totalCard = new JPanel(new BorderLayout());
		totalCard.setBackground(ColorScheme.BACKGROUND);
		totalCard.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER),
			new EmptyBorder(14, 20, 14, 20)
		));

		var lblTotal = new JLabel("Tổng tiền:");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTotal.setForeground(ColorScheme.TEXT_PRIMARY);
		totalCard.add(lblTotal, BorderLayout.WEST);

		lblTongTien = new JLabel("0 đ");
		lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 26));
		lblTongTien.setForeground(ColorScheme.DANGER);
		totalCard.add(lblTongTien, BorderLayout.EAST);

		bottomPanel.add(totalCard, BorderLayout.NORTH);

		// Action buttons
		var actionPanel = new JPanel(new GridLayout(1, 2, 14, 0));
		actionPanel.setOpaque(false);

		btnHuy = UIHelper.createOutlineButton("Hủy bỏ", ColorScheme.DANGER);
		btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnHuy.setPreferredSize(new Dimension(0, ColorScheme.BUTTON_HEIGHT_LG));
		btnHuy.addActionListener(e -> handleHuy());

		btnThanhToan = UIHelper.createSuccessButton("✅ Thanh toán (F9)");
		btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 15));
		btnThanhToan.setPreferredSize(new Dimension(0, ColorScheme.BUTTON_HEIGHT_LG));
		btnThanhToan.addActionListener(e -> handleThanhToan());

		actionPanel.add(btnHuy);
		actionPanel.add(btnThanhToan);

		bottomPanel.add(actionPanel, BorderLayout.CENTER);
		card.add(bottomPanel, BorderLayout.SOUTH);

		return card;
	}

	// ===================== HELPER METHODS =====================

	private JPanel createCard(String title) {
		var card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(ColorScheme.PANEL_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
			new EmptyBorder(16, 16, 16, 16)
		));
		card.setAlignmentX(Component.LEFT_ALIGNMENT);

		if (title != null) {
			var lblTitle = new JLabel(title);
			lblTitle.setFont(UIHelper.FONT_SUBSECTION);
			lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
			lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
			lblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
			card.add(lblTitle);
		}

		return card;
	}

	private void addFormField(JPanel card, String labelText, String hint) {
		var lbl = new JLabel(labelText);
		lbl.setFont(UIHelper.FONT_LABEL);
		lbl.setForeground(ColorScheme.TEXT_PRIMARY);
		lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
		lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
		card.add(lbl);
	}

	private void updateTableVisibility() {
		// Switch between empty state and table based on data
		var tableArea = (JPanel) tableScrollPane.getParent();
		var cl = (CardLayout) tableArea.getLayout();
		if (tableModel.getRowCount() == 0) {
			cl.show(tableArea, "empty");
		} else {
			cl.show(tableArea, "table");
		}
	}

	// ===================== AUTOCOMPLETE =====================

	private void setupAutocomplete() {
		suggestionModel = new DefaultListModel<>();
		listSuggestions = new JList<>(suggestionModel);
		listSuggestions.setFont(UIHelper.FONT_INPUT);
		listSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSuggestions.setFixedCellHeight(34);
		listSuggestions.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				setBorder(new EmptyBorder(4, 12, 4, 12));
				if (isSelected) {
					setBackground(ColorScheme.PRIMARY_LIGHT);
					setForeground(ColorScheme.TEXT_PRIMARY);
				}
				return this;
			}
		});

		popupSuggestions = new JPopupMenu();
		popupSuggestions.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
		var scrollSuggestions = new JScrollPane(listSuggestions);
		scrollSuggestions.setBorder(null);
		scrollSuggestions.setPreferredSize(new Dimension(350, 180));
		popupSuggestions.add(scrollSuggestions);

		listSuggestions.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting() && listSuggestions.getSelectedIndex() >= 0) {
				int idx = listSuggestions.getSelectedIndex();
				if (cachedCustomers != null && idx >= 0 && idx < cachedCustomers.size()) {
					selectCustomer(cachedCustomers.get(idx));
					popupSuggestions.setVisible(false);
				}
			}
		});

		searchTimer = new javax.swing.Timer(300, e -> performSearch());
		searchTimer.setRepeats(false);

		txtTimKhachHang.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
			@Override public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
			@Override public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
		});
	}

	private void performSearch() {
		String keyword = txtTimKhachHang.getText().trim();
		if (keyword.length() < 1) {
			popupSuggestions.setVisible(false);
			return;
		}

		cachedCustomers = khachHangDao.searchByNameOrPhone(keyword);
		suggestionModel.clear();

		if (cachedCustomers.isEmpty()) {
			suggestionModel.addElement("Không tìm thấy \"" + keyword + "\"");
		} else {
			for (var kh : cachedCustomers) {
				suggestionModel.addElement(kh.getHoTen() + "  |  " +
					(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "--") +
					"  (Mã: " + kh.getMaKhachHang() + ")");
			}
		}

		if (!popupSuggestions.isVisible()) {
			popupSuggestions.show(txtTimKhachHang, 0, txtTimKhachHang.getHeight());
		}
		popupSuggestions.setPopupSize(txtTimKhachHang.getWidth(),
			Math.min(cachedCustomers.size() * 36 + 10, 200));
	}

	private void selectCustomer(KhachHang kh) {
		selectedMaKhachHang = kh.getMaKhachHang();
		lblSelectedCustomer.setText("✅ " + kh.getHoTen() +
			"  |  SĐT: " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "--") +
			"  (Mã: " + kh.getMaKhachHang() + ")");
		lblSelectedCustomer.setForeground(ColorScheme.SUCCESS);
		btnXoaKhachHang.setVisible(true);
		txtTimKhachHang.setText("");
	}

	private void clearSelectedCustomer() {
		selectedMaKhachHang = null;
		lblSelectedCustomer.setText("Khách lẻ (chưa chọn)");
		lblSelectedCustomer.setForeground(ColorScheme.TEXT_MUTED);
		btnXoaKhachHang.setVisible(false);
	}

	// ===================== BUSINESS LOGIC =====================

	private void loadSanPham() {
		comboSanPham.removeAllItems();
		var list = sanPhamDao.getAll();
		for (var sp : list) {
			comboSanPham.addItem(sp.getMaSanPham() + " - " + sp.getTenSanPham());
		}
	}

	private void handleThemKhachHangMoi() {
		String hoTen = txtKhachHangTen.getText().trim();
		String sdt = txtKhachHangSDT.getText().trim();
		String diaChi = txtKhachHangDiaChi.getText().trim();

		if (hoTen.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!",
				"Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
			txtKhachHangTen.requestFocus();
			return;
		}
		if (sdt.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!",
				"Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
			txtKhachHangSDT.requestFocus();
			return;
		}

		var existing = khachHangDao.searchByNameOrPhone(sdt);
		for (var kh : existing) {
			if (sdt.equals(kh.getSoDienThoai())) {
				int option = JOptionPane.showConfirmDialog(this,
					"SĐT \"" + sdt + "\" đã có: \"" + kh.getHoTen() + "\".\nChọn khách hàng này?",
					"Khách hàng đã tồn tại", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					selectCustomer(kh);
					clearNewCustomerForm();
				}
				return;
			}
		}

		var newKH = new KhachHang();
		newKH.setHoTen(hoTen);
		newKH.setSoDienThoai(sdt);
		newKH.setDiaChi(diaChi);
		Integer newId = khachHangDao.insertAndGetId(newKH);
		if (newId != null) {
			newKH.setMaKhachHang(newId);
			selectCustomer(newKH);
			clearNewCustomerForm();
			JOptionPane.showMessageDialog(this,
				"Thêm khách hàng thành công! (Mã: " + newId + ")",
				"Thành công", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this,
				"Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearNewCustomerForm() {
		txtKhachHangTen.setText("");
		txtKhachHangSDT.setText("");
		txtKhachHangDiaChi.setText("");
	}

	private void handleTaoHoaDon() {
		String ghiChu = txtGhiChu.getText().trim();
		Integer maHD = hoaDonDao.createHoaDon(currentUser.getMaNguoiDung(), selectedMaKhachHang, ghiChu);
		if (maHD != null) {
			currentMaHoaDon = maHD;
			txtMaHoaDon.setText(String.valueOf(maHD));
			JOptionPane.showMessageDialog(this,
				"Tạo hóa đơn thành công! Mã: " + maHD,
				"Thành công", JOptionPane.INFORMATION_MESSAGE);
			tableModel.setRowCount(0);
			updateTongTien();
			updateTableVisibility();
		} else {
			JOptionPane.showMessageDialog(this,
				"Tạo hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleThemSanPham() {
		if (currentMaHoaDon == null) {
			JOptionPane.showMessageDialog(this,
				"Vui lòng tạo hóa đơn trước!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (comboSanPham.getSelectedIndex() < 0) {
			JOptionPane.showMessageDialog(this,
				"Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String selected = (String) comboSanPham.getSelectedItem();
		int maSP = Integer.parseInt(selected.split(" - ")[0]);

		if (txtSoLuong.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this,
				"Vui lòng nhập số lượng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			txtSoLuong.requestFocus();
			return;
		}

		int soLuong;
		try {
			soLuong = Integer.parseInt(txtSoLuong.getText().trim());
			if (soLuong <= 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
				"Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		BigDecimal giaBan = null;
		if (!txtGiaBan.getText().trim().isEmpty()) {
			try {
				giaBan = new BigDecimal(txtGiaBan.getText().trim());
				if (giaBan.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this,
					"Giá bán không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (hoaDonDao.sellProductFEFO(currentMaHoaDon, maSP, soLuong, giaBan)) {
			loadChiTietHoaDon();
			txtSoLuong.setText("");
			txtGiaBan.setText("");
			txtSoLuong.requestFocus();
		} else {
			JOptionPane.showMessageDialog(this,
				"Thêm sản phẩm thất bại!\nKiểm tra tồn kho hoặc số lượng.",
				"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadChiTietHoaDon() {
		if (currentMaHoaDon == null) return;
		tableModel.setRowCount(0);
		var list = chiTietDao.getDetailForDisplay(currentMaHoaDon);
		for (var row : list) {
			tableModel.addRow(row);
		}
		updateTongTien();
		updateTableVisibility();
	}

	private void updateTongTien() {
		BigDecimal total = BigDecimal.ZERO;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			BigDecimal thanhTien = (BigDecimal) tableModel.getValueAt(i, 6);
			total = total.add(thanhTien);
		}
		lblTongTien.setText(VND.format(total));
	}

	private void handleSuaSoLuong() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this,
				"Vui lòng chọn sản phẩm để sửa!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int maCTHD = (int) tableModel.getValueAt(selectedRow, 0);
		String tenSP = (String) tableModel.getValueAt(selectedRow, 2);
		int soLuongCu = (int) tableModel.getValueAt(selectedRow, 4);

		String input = JOptionPane.showInputDialog(this,
			"Sản phẩm: " + tenSP + "\nSố lượng hiện tại: " + soLuongCu + "\n\nNhập số lượng mới:",
			"Sửa số lượng", JOptionPane.PLAIN_MESSAGE);
		if (input == null || input.trim().isEmpty()) return;

		int soLuongMoi;
		try {
			soLuongMoi = Integer.parseInt(input.trim());
			if (soLuongMoi <= 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this,
				"Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (soLuongMoi == soLuongCu) return;

		if (chiTietDao.updateSoLuong(maCTHD, soLuongMoi)) {
			loadChiTietHoaDon();
		} else {
			JOptionPane.showMessageDialog(this,
				"Cập nhật thất bại! Có thể không đủ tồn kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleXoaSanPham() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this,
				"Vui lòng chọn sản phẩm để xóa!", "Chưa chọn", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int maCTHD = (int) tableModel.getValueAt(selectedRow, 0);
		String tenSP = (String) tableModel.getValueAt(selectedRow, 2);
		int soLuong = (int) tableModel.getValueAt(selectedRow, 4);

		int option = JOptionPane.showConfirmDialog(this,
			"Xóa: " + tenSP + " (SL: " + soLuong + ")?\nTồn kho sẽ được hoàn trả.",
			"Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			if (chiTietDao.deleteChiTiet(maCTHD)) {
				loadChiTietHoaDon();
			} else {
				JOptionPane.showMessageDialog(this,
					"Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void handleThanhToan() {
		if (currentMaHoaDon == null) {
			JOptionPane.showMessageDialog(this,
				"Chưa có hóa đơn nào!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (tableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this,
				"Hóa đơn chưa có sản phẩm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int option = JOptionPane.showConfirmDialog(this,
			"Xác nhận thanh toán hóa đơn #" + currentMaHoaDon + "?\n\nTổng tiền: " + lblTongTien.getText(),
			"Xác nhận thanh toán", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(this,
				"✅ Thanh toán thành công!\nMã HĐ: " + currentMaHoaDon + "\nTổng: " + lblTongTien.getText(),
				"Thành công", JOptionPane.INFORMATION_MESSAGE);
			resetForm();
		}
	}

	private void handleHuy() {
		if (currentMaHoaDon == null) return;
		int option = JOptionPane.showConfirmDialog(this,
			"Hủy hóa đơn #" + currentMaHoaDon + "?",
			"Xác nhận", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			resetForm();
		}
	}

	private void resetForm() {
		currentMaHoaDon = null;
		selectedMaKhachHang = null;
		txtMaHoaDon.setText("");
		clearSelectedCustomer();
		txtTimKhachHang.setText("");
		clearNewCustomerForm();
		if (comboSanPham.getItemCount() > 0) {
			comboSanPham.setSelectedIndex(0);
		}
		txtSoLuong.setText("");
		txtGiaBan.setText("");
		txtGhiChu.setText("");
		tableModel.setRowCount(0);
		updateTongTien();
		updateTableVisibility();
	}
}
