package panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.MaskFormatter;

import common.ColorScheme;
import common.UIHelper;
import components.ChiTietPhieuNhapTableModel;
import dao.PhieuNhapDao;
import dao.SanPhamDao;
import entity.NguoiDung;
import entity.SanPham;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class NhapHangPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private PhieuNhapDao phieuNhapDao;
	private SanPhamDao sanPhamDao;
	private dao.LoHangDao loHangDao;
	private dao.NhaCungCapDao nccDao;
	private ChiTietPhieuNhapTableModel tableModel;
	private JTable table;
	
	// Thông tin lô hàng (Batch Info)
	private JTextField txtSoLo;
	private JFormattedTextField txtHanSuDung;
	private JTextField txtGiaNhap;
	private JTextField txtSoLuong;
	
	private JButton btnTaoPhieuNhap;
	private JButton btnThemSanPham;
	private JButton btnHuy;
	private JLabel lblTongTien;
	private NguoiDung currentUser;

	// Supplier
	private JComboBox<Object> comboNhaCungCap;

	// Thông tin sản phẩm (Product Info)
	private JComboBox<Object> comboSanPham;
	private JTextField txtTenSanPham;
	private JTextField txtDonViTinhNhap;
	private JTextField txtGiaBanDeXuatNhap;
	
	// Labels for visibility control
	private JLabel lblTenSPMoi, lblDonViTinhNhap, lblGiaBanDeXuatNhap;

	// Flag to suppress ActionListener events during bulk data loading
	private boolean isLoadingData = false;

	// Fields required for compilation/consistency (hidden)
	// private JTextField txtTongSanPham;
	// private JTextArea txtGhiChu;
	
	// Autocomplete (Integrated or internal)
	// private javax.swing.JPopupMenu autocompleteMenu;
	// private List<entity.SanPham> autocompleteList;

	public NhapHangPanel(NguoiDung currentUser) {
		this.currentUser = currentUser;
		phieuNhapDao = new PhieuNhapDao();
		sanPhamDao = new SanPhamDao();
		loHangDao = new dao.LoHangDao();
		nccDao = new dao.NhaCungCapDao();
		initialize();
		resetForm();
		
		// [Requirement: TRIGGER_ON_SHOW] Refresh NCC list when tab is shown
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				loadNhaCungCapToComboBox();
			}
		});
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(ColorScheme.BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		var titlePanel = new JPanel(new BorderLayout(0, 10));
		titlePanel.setOpaque(false);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		
		var lblTitle = new JLabel("Nhập hàng");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
		titlePanel.add(lblTitle, BorderLayout.WEST);
		
		add(titlePanel, BorderLayout.NORTH);

		var mainPanel = new JPanel(new BorderLayout(15, 0));
		mainPanel.setOpaque(false);

		mainPanel.add(createLeftPanel(), BorderLayout.WEST);
		mainPanel.add(createRightPanel(), BorderLayout.CENTER);

		add(mainPanel, BorderLayout.CENTER);
	}

	private JPanel createLeftPanel() {
		var wrapperPanel = new JPanel();
		wrapperPanel.setLayout(new javax.swing.BoxLayout(wrapperPanel, javax.swing.BoxLayout.Y_AXIS));
		wrapperPanel.setBackground(ColorScheme.PANEL_BG);

		int fieldWidth = 200;
		int labelWidth = 120;
		int fieldHeight = 30;
		int spacing = 40;

		// ================= 1. Thông tin sản phẩm =================
		var pnlSanPham = new JPanel(null);
		pnlSanPham.setBackground(ColorScheme.PANEL_BG);
		pnlSanPham.setBorder(new TitledBorder(
				BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
				"Thông tin sản phẩm",
				TitledBorder.LEADING,
				TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 14),
				ColorScheme.TEXT_PRIMARY));

		int y1 = 30;
		// Initialize fields
		comboSanPham = new JComboBox<>();
		txtTenSanPham = new JTextField();
		txtDonViTinhNhap = new JTextField("Hộp");
		txtGiaBanDeXuatNhap = new JTextField();
		
		txtSoLo = new JTextField();
		txtGiaNhap = new JTextField();
		txtSoLuong = new JTextField();
		comboNhaCungCap = new JComboBox<>();
		loadNhaCungCapToComboBox();

		var lblChonSP = new JLabel("Chọn sản phẩm:*");
		lblChonSP.setBounds(20, y1, labelWidth, 25);
		pnlSanPham.add(lblChonSP);
		
		comboSanPham.setBounds(140, y1, fieldWidth, fieldHeight);
		loadSanPham();
		comboSanPham.addActionListener(e -> {
			if (isLoadingData) return; // Suppress events during bulk loading
			Object selected = comboSanPham.getSelectedItem();
			if (selected instanceof String && selected.toString().startsWith("[ + ]")) {
				setProductFieldsState(true); // Open for entry
				txtTenSanPham.requestFocusInWindow();
			} else if (selected instanceof entity.SanPham sp) {
				txtTenSanPham.setText(sp.getTenSanPham());
				txtDonViTinhNhap.setText(sp.getDonViTinh());
				txtGiaBanDeXuatNhap.setText(utils.FormatUtils.formatCurrency(sp.getGiaBanDeXuat()));
				setProductFieldsState(false); // Lock for existing
				txtSoLo.requestFocusInWindow();
			}
		});
		
		setupCurrencyFormatting(txtGiaBanDeXuatNhap);
		pnlSanPham.add(comboSanPham);

		y1 += spacing;
		lblTenSPMoi = new JLabel("Tên SP mới:*");
		lblTenSPMoi.setBounds(20, y1, labelWidth, 25);
		pnlSanPham.add(lblTenSPMoi);
		txtTenSanPham.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtTenSanPham.setBounds(140, y1, fieldWidth, fieldHeight);
		pnlSanPham.add(txtTenSanPham);

		y1 += spacing;
		lblDonViTinhNhap = new JLabel("Đơn vị tính:*");
		lblDonViTinhNhap.setBounds(20, y1, labelWidth, 25);
		pnlSanPham.add(lblDonViTinhNhap);
		txtDonViTinhNhap.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtDonViTinhNhap.setBounds(140, y1, fieldWidth, fieldHeight);
		pnlSanPham.add(txtDonViTinhNhap);

		y1 += spacing;
		lblGiaBanDeXuatNhap = new JLabel("Giá bán đề xuất:");
		lblGiaBanDeXuatNhap.setBounds(20, y1, labelWidth, 25);
		pnlSanPham.add(lblGiaBanDeXuatNhap);
		txtGiaBanDeXuatNhap.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtGiaBanDeXuatNhap.setBounds(140, y1, fieldWidth, fieldHeight);
		pnlSanPham.add(txtGiaBanDeXuatNhap);

		pnlSanPham.setPreferredSize(new java.awt.Dimension(360, 240));
		pnlSanPham.setMinimumSize(new java.awt.Dimension(360, 240));
		pnlSanPham.setMaximumSize(new java.awt.Dimension(360, 240));

		setProductFieldsState(false); // Show simplified by default

		wrapperPanel.add(pnlSanPham);
		wrapperPanel.add(javax.swing.Box.createVerticalStrut(15));
		
		// ================= 2. Thông tin lô hàng =================
		var pnlLoHang = new JPanel(null);
		pnlLoHang.setBackground(ColorScheme.PANEL_BG);
		pnlLoHang.setBorder(new TitledBorder(
				BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
				"Thông tin lô hàng",
				TitledBorder.LEADING,
				TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 14),
				ColorScheme.TEXT_PRIMARY));
		pnlLoHang.setPreferredSize(new java.awt.Dimension(360, 320));
		pnlLoHang.setMinimumSize(new java.awt.Dimension(360, 320));
		pnlLoHang.setMaximumSize(new java.awt.Dimension(360, 320));

		int y = 30;

		var lblSoLo = new JLabel("Số lô:*");
		lblSoLo.setBounds(20, y, labelWidth, 25);
		pnlLoHang.add(lblSoLo);

		txtSoLo.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtSoLo.setBounds(140, y, fieldWidth, fieldHeight);
		pnlLoHang.add(txtSoLo);

		y += spacing;

		var lblHanSuDung = new JLabel("Hạn sử dụng:*");
		lblHanSuDung.setBounds(20, y, labelWidth, 25);
		pnlLoHang.add(lblHanSuDung);

		try {
			MaskFormatter dateMask = new MaskFormatter("##/##/####");
			dateMask.setPlaceholderCharacter('_');
			txtHanSuDung = new JFormattedTextField(dateMask);
		} catch (java.text.ParseException e) {
			txtHanSuDung = new JFormattedTextField();
		}
		
		txtHanSuDung.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtHanSuDung.setBounds(140, y, fieldWidth, fieldHeight);
		txtHanSuDung.setToolTipText("Định dạng: dd/MM/yyyy (ví dụ: 31/12/2024)");
		txtHanSuDung.putClientProperty("JTextField.placeholderText", "dd/mm/yyyy");
		pnlLoHang.add(txtHanSuDung);

		y += spacing;

		var lblGiaNhap = new JLabel("Giá nhập:*");
		lblGiaNhap.setBounds(20, y, labelWidth, 25);
		pnlLoHang.add(lblGiaNhap);


		txtGiaNhap.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtGiaNhap.setBounds(140, y, fieldWidth, fieldHeight);
		setupCurrencyFormatting(txtGiaNhap);
		pnlLoHang.add(txtGiaNhap);

		y += spacing;

		var lblSoLuong = new JLabel("Số lượng:*");
		lblSoLuong.setBounds(20, y, labelWidth, 25);
		pnlLoHang.add(lblSoLuong);

		txtSoLuong.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER, 1));
		txtSoLuong.setBounds(140, y, fieldWidth, fieldHeight);
		pnlLoHang.add(txtSoLuong);

		y += spacing;
		var lblNCC = new JLabel("Nhà cung cấp:*");
		lblNCC.setBounds(20, y, labelWidth, 25);
		pnlLoHang.add(lblNCC);
		comboNhaCungCap.setBounds(140, y, fieldWidth, fieldHeight);
		pnlLoHang.add(comboNhaCungCap);
		
		wrapperPanel.add(pnlLoHang);
		wrapperPanel.add(javax.swing.Box.createVerticalStrut(15));
		
		// ================= 3. Buttons =================
		var pnlButtons = new JPanel(null);
		pnlButtons.setBackground(ColorScheme.PANEL_BG);
		pnlButtons.setPreferredSize(new java.awt.Dimension(360, 200));
		pnlButtons.setMinimumSize(new java.awt.Dimension(360, 200));
		pnlButtons.setMaximumSize(new java.awt.Dimension(360, 200));
		
		int y3 = 0;

		btnThemSanPham = UIHelper.createSuccessButton("Thêm vào danh sách chờ");
		btnThemSanPham.setBounds(20, y3, 320, 40);
		btnThemSanPham.addActionListener(e -> handleThemSanPham());
		pnlButtons.add(btnThemSanPham);

		y3 += 50;

		btnTaoPhieuNhap = UIHelper.createPrimaryButton("Xác nhận & Nhập kho");
		btnTaoPhieuNhap.setBounds(20, y3, 320, 40);
		btnTaoPhieuNhap.addActionListener(e -> handleTaoPhieuNhap());
		pnlButtons.add(btnTaoPhieuNhap);

		y3 += 50;

		btnHuy = UIHelper.createDangerButton("Xóa sản phẩm đã chọn");
		btnHuy.setBounds(20, y3, 320, 40);
		btnHuy.addActionListener(e -> handleXoaItemTable());
		pnlButtons.add(btnHuy);
		
		wrapperPanel.add(pnlButtons);

		var mainScroll = new JScrollPane(wrapperPanel);
		mainScroll.setBorder(null);
		mainScroll.getVerticalScrollBar().setUnitIncrement(16);
		mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		var wrapperOuter = new JPanel(new BorderLayout());
		wrapperOuter.setOpaque(false);
		wrapperOuter.setPreferredSize(new java.awt.Dimension(380, 0));
		wrapperOuter.add(mainScroll, BorderLayout.CENTER);

		return wrapperOuter;
	}

	private JPanel createRightPanel() {
		var panel = new JPanel(new BorderLayout());
		panel.setBackground(ColorScheme.PANEL_BG);
		panel.setBorder(new TitledBorder(
				BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
				"Danh sách hàng chờ kiểm nhập",
				TitledBorder.LEADING,
				TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 14),
				ColorScheme.TEXT_PRIMARY));

		tableModel = new ChiTietPhieuNhapTableModel();
		table = new JTable(tableModel);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setRowHeight(28);
		table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(true);
		table.setGridColor(new Color(235, 238, 242));
		table.setIntercellSpacing(new java.awt.Dimension(1, 1));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		// TableRowSorter for waiting list
		table.setRowSorter(new javax.swing.table.TableRowSorter<>(tableModel));
		
		// Custom Cell Renderer
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				if (value instanceof java.math.BigDecimal bd) {
					setText(utils.FormatUtils.formatCurrency(bd));
				} else if (value instanceof java.time.LocalDate ld) {
					setText(utils.FormatUtils.formatDate(ld));
				} else {
					super.setValue(value);
				}
				setHorizontalAlignment(JLabel.RIGHT);
			}
		};
		table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Giá nhập
		
		// Set column widths
		var columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(40);  // STT
		columnModel.getColumn(0).setMaxWidth(50);
		columnModel.getColumn(1).setPreferredWidth(60);  // Mã SP
		columnModel.getColumn(1).setMaxWidth(80);
		columnModel.getColumn(2).setPreferredWidth(180); // Tên SP
		columnModel.getColumn(3).setPreferredWidth(80);  // Số lô
		columnModel.getColumn(4).setPreferredWidth(100); // Hạn dùng
		columnModel.getColumn(5).setPreferredWidth(110); // Giá nhập
		columnModel.getColumn(6).setPreferredWidth(70);  // Số lượng
		
		var scrollPane = new JScrollPane(table);
		scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
		scrollPane.getViewport().setBackground(ColorScheme.PANEL_BG);
		panel.add(scrollPane, BorderLayout.CENTER);

		var totalPanel = new JPanel(new BorderLayout());
		totalPanel.setOpaque(false);
		totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		var lblTotal = new JLabel("Tổng giá trị đơn nhập:");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTotal.setForeground(ColorScheme.TEXT_PRIMARY);
		totalPanel.add(lblTotal, BorderLayout.WEST);

		lblTongTien = new JLabel("0 VND");
		lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTongTien.setForeground(ColorScheme.DANGER);
		totalPanel.add(lblTongTien, BorderLayout.EAST);

		panel.add(totalPanel, BorderLayout.SOUTH);

		return panel;
	}

	private void loadNhaCungCapToComboBox() {
		System.out.println("[NhapHangPanel] Loading suppliers to ComboBox...");
		isLoadingData = true;
		try {
			comboNhaCungCap.removeAllItems();
			comboNhaCungCap.addItem("-- Chọn nhà cung cấp --");
			List<entity.NhaCungCap> list = nccDao.getAll();
			System.out.println("[NhapHangPanel] Found " + list.size() + " suppliers.");
			for (var ncc : list) {
				comboNhaCungCap.addItem(ncc);
			}
		} finally {
			isLoadingData = false;
		}
	}
	private void loadSanPham() {
		isLoadingData = true;
		try {
			comboSanPham.removeAllItems();
			comboSanPham.addItem("[ + ] Thêm sản phẩm mới");
			List<SanPham> list = sanPhamDao.getAll();
			// Ensure list is sorted if DAO didn't
			list.sort((a,b) -> a.getTenSanPham().compareToIgnoreCase(b.getTenSanPham()));
			for (SanPham sp : list) {
				comboSanPham.addItem(sp);
			}
		} finally {
			isLoadingData = false;
		}
	}

	private void handleThemSanPham() {
		Object selected = comboSanPham.getSelectedItem();
		entity.SanPham sp = null;
		String tenSPNhap = "";
		
		if (selected instanceof String && selected.toString().startsWith("[ + ]")) {
			tenSPNhap = txtTenSanPham.getText().trim();
		} else if (selected instanceof entity.SanPham s) {
			sp = s;
			tenSPNhap = s.getTenSanPham();
		}

		if (tenSPNhap.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập tên sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String soLo = txtSoLo.getText().trim();
		if (soLo.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số lô!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String strHSD = txtHanSuDung.getText().trim();
		if (strHSD.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập hạn sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		LocalDate hanSuDung;
		try {
			hanSuDung = LocalDate.parse(strHSD, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (DateTimeParseException e) {
			JOptionPane.showMessageDialog(this, "Hạn sử dụng không đúng định dạng! Vui lòng nhập dd/MM/yyyy", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (hanSuDung.isBefore(LocalDate.now())) {
			JOptionPane.showMessageDialog(this, "Không thể nhập lô đã quá hạn sử dụng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		BigDecimal giaNhap = utils.FormatUtils.parseCurrency(txtGiaNhap.getText());
		if (giaNhap.compareTo(BigDecimal.ZERO) <= 0) {
			JOptionPane.showMessageDialog(this, "Giá nhập phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int soLuong;
		try {
			String qtyStr = txtSoLuong.getText().replaceAll("[^0-9]", "");
			soLuong = Integer.parseInt(qtyStr);
			if (soLuong <= 0) throw new NumberFormatException();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String donViNhap = txtDonViTinhNhap.getText().trim();
		if (donViNhap.isEmpty()) donViNhap = "Hộp";


		// Nếu là SP mới (không tìm thấy) -> Insert SP vào DB trước
		int maSPFinal = sp != null ? sp.getMaSanPham() : 0;
		String tenSPFinal = tenSPNhap;
		if (maSPFinal == 0) {
			// [Requirement: FIX_SOFT_DELETE_RESURRECTION] Kiểm tra sản phẩm cũ (kể cả đã xóa)
			SanPham existing = sanPhamDao.findByNameIncludingDeleted(tenSPFinal);
			if (existing != null) {
				if (existing.isDaXoa()) {
					sanPhamDao.resurrect(existing.getMaSanPham());
					System.out.println("[NhapHangPanel] Resurrected product: " + tenSPFinal);
				}
				maSPFinal = existing.getMaSanPham();
				sp = existing; // Cập nhật sp để có đủ info
			} else {
				// Tạo mới hoàn toàn
				String donViTinhMoi = txtDonViTinhNhap.getText().trim();
				if (donViTinhMoi.isEmpty()) donViTinhMoi = "Hộp";
				
				BigDecimal giaBanDeXuat = utils.FormatUtils.parseCurrency(txtGiaBanDeXuatNhap.getText());
				
				SanPham spMoi = new SanPham();
				spMoi.setTenSanPham(tenSPFinal);
				spMoi.setDonViTinh(donViTinhMoi);
				spMoi.setGiaBanDeXuat(giaBanDeXuat);
				spMoi.setMoTa("");
				spMoi.setMucTonToiThieu(10);
				
				int newId = sanPhamDao.insertAndGetId(spMoi);
				if (newId <= 0) {
					JOptionPane.showMessageDialog(this, "Không thể tạo sản phẩm mới! Kiểm tra lại kết nối DB.", "Lỗi", JOptionPane.ERROR_MESSAGE);
					return;
				}
				maSPFinal = newId;
			}
			loadSanPham(); // Reload list
		}

		Object[] rowData = {
			tableModel.getRowCount() + 1, // STT
			maSPFinal,
			tenSPFinal,
			soLo,
			hanSuDung,
			giaNhap,
			soLuong,
			donViNhap,
			"Bán lẻ" // Default since radio is gone
		};
		tableModel.addRow(rowData);
		
		updateTongTien();
		clearProductForm();
		// Focus back to product selection
		comboSanPham.requestFocusInWindow();
	}

	private void handleXoaItemTable() {
		int row = table.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng muốn xóa khỏi bảng", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		tableModel.removeRow(row);
		// Re-index STT
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			tableModel.setValueAt(i + 1, i, 0);
		}
		updateTongTien();
	}

	private void updateTongTien() {
		java.math.BigDecimal total = java.math.BigDecimal.ZERO;
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			java.math.BigDecimal giaNhap = (java.math.BigDecimal) tableModel.getValueAt(i, 5);
			int soLuong = ((Number) tableModel.getValueAt(i, 6)).intValue();
			total = total.add(giaNhap.multiply(new java.math.BigDecimal(soLuong)));
		}
		lblTongTien.setText(utils.FormatUtils.formatCurrency(total));
	}

	private void handleTaoPhieuNhap() {
		if (tableModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm nào! Hãy thêm sản phẩm trước", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		System.out.println("[NhapHangPanel] === CHUẨN BỊ GỬI YÊU CẦU TRANSACTION ===");
		List<entity.ChiTietPhieuNhap> chiTietList = new ArrayList<>();
		if (comboNhaCungCap.getSelectedIndex() <= 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Integer maNCC = ((entity.NhaCungCap) comboNhaCungCap.getSelectedItem()).getMaNCC();

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			int maSanPham = ((Number) tableModel.getValueAt(i, 1)).intValue();
			String soLo = (String) tableModel.getValueAt(i, 3);
			java.time.LocalDate hanSuDung = (java.time.LocalDate) tableModel.getValueAt(i, 4);
			java.math.BigDecimal giaNhap = (java.math.BigDecimal) tableModel.getValueAt(i, 5);
			int soLuong = ((Number) tableModel.getValueAt(i, 6)).intValue();
			String donViNhap = (String) tableModel.getValueAt(i, 7);
			String loaiHinhBan = (String) tableModel.getValueAt(i, 8);
			
			// [Requirement: SMART_ACCUMULATION_LOGIC] Tự động cộng dồn nếu trùng bộ ba (MaSP, SoLo, HSD)
			entity.LoHang existing = loHangDao.findByMaSPSoLoHSD(maSanPham, soLo, hanSuDung);
			boolean isMerge = (existing != null);

			entity.ChiTietPhieuNhap ct = new entity.ChiTietPhieuNhap();
			ct.setMaSanPham(maSanPham);
			ct.setSoLo(soLo);
			ct.setHanSuDung(hanSuDung);
			ct.setGiaNhap(giaNhap);
			ct.setSoLuong(soLuong);
			ct.setDonViNhap(donViNhap);
			ct.setSoViTrenHop(0); 
			ct.setSoVienTrenVi(0); 
			ct.setTongSoVien(soLuong); // Simplified
			ct.setLoaiHinhBan(loaiHinhBan);
			ct.setMergeBatch(isMerge);
			chiTietList.add(ct);
		}

		Integer maPN = phieuNhapDao.savePhieuNhapTransaction(currentUser.getMaNguoiDung(), maNCC, "", chiTietList);
		
		if (maPN != null && maPN > 0) {
			System.out.println("[NhapHangPanel] Giao dịch lưu Phiếu Nhập Hàng thành công!");
			JOptionPane.showMessageDialog(this,
					"Tạo Phiếu Nhập Hàng thành công!\n"
					+ tableModel.getRowCount() + " sản phẩm đã được tự động nhập Lô hàng.",
					"Thành công", JOptionPane.INFORMATION_MESSAGE);
			
			// [Requirement: SYNC_PRODUCT_QUANTITY] Cập nhật tổng tồn cho từng SP vừa nhập
			for (entity.ChiTietPhieuNhap item : chiTietList) {
				sanPhamDao.updateTotalQuantity(item.getMaSanPham());
			}

			// Auto refresh data across panels (Requirement: AUTO_REFRESH_DATA_AFTER_IMPORT)
			var top = javax.swing.SwingUtilities.getWindowAncestor(this);
			if (top instanceof app.MainFrame) {
				((app.MainFrame) top).refreshAllData();
			}
			
			resetForm();
		} else {
			JOptionPane.showMessageDialog(this,
					"Tạo Phiếu Nhập Hàng gặp lỗi!\n(Đã tự động Rollback - Dữ liệu không bị rác)\nXem chi tiết lỗi trong console/terminal.",
					"Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void clearProductForm() {
		txtSoLo.setText("");
		txtHanSuDung.setText("");
		txtGiaNhap.setText("");
		txtSoLuong.setText("");
		
		if (txtTenSanPham != null) {
			txtTenSanPham.setText(""); txtTenSanPham.setVisible(false);
		}
		if (comboSanPham != null && comboSanPham.getItemCount() > 0) {
			comboSanPham.setSelectedIndex(0);
		}
		if (txtDonViTinhNhap != null) txtDonViTinhNhap.setText("Hộp");
		if (txtGiaBanDeXuatNhap != null) txtGiaBanDeXuatNhap.setText("");
	}

	private void setProductFieldsState(boolean isNew) {
		lblTenSPMoi.setVisible(isNew);
		txtTenSanPham.setVisible(isNew);
		
		// Always visible, but editable state changes
		lblDonViTinhNhap.setVisible(true);
		txtDonViTinhNhap.setVisible(true);
		lblGiaBanDeXuatNhap.setVisible(true);
		txtGiaBanDeXuatNhap.setVisible(true);
		
		txtDonViTinhNhap.setEditable(isNew);
		txtGiaBanDeXuatNhap.setEditable(isNew);
		
		if (isNew) {
			txtTenSanPham.setText("");
			txtDonViTinhNhap.setText("Hộp");
			txtGiaBanDeXuatNhap.setText("");
		}
	}

	private void resetForm() {
		clearProductForm();
		tableModel.setRowCount(0);
		updateTongTien();
	}

	private void setupCurrencyFormatting(javax.swing.JTextField field) {
		field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			private boolean isUpdating = false;

			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }

			private void update() {
				if (isUpdating) return;
				isUpdating = true;
				javax.swing.SwingUtilities.invokeLater(() -> {
					String original = field.getText();
					java.math.BigDecimal val = utils.FormatUtils.parseCurrency(original);
					String formatted = utils.FormatUtils.formatCurrency(val);
					
					// Keep cursor position
					int pos = field.getCaretPosition();
					field.setText(formatted);
					int newPos = Math.min(pos, formatted.length() - 4);
					if (newPos < 0) newPos = 0;
					try { field.setCaretPosition(newPos); } catch (Exception ignored) {}
					
					isUpdating = false;
				});
			}
		});
	}
}
