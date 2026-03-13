package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import common.ColorScheme;
import dao.LoHangDao;
import entity.LoHang;
import entity.SanPham;

/**
 * Dialog hiển thị toàn bộ chi tiết thông tin và trạng thái kho của Sản Phẩm (2 Cột)
 */
public class ProductDetailDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private LoHangDao batchDao = new LoHangDao();

	public ProductDetailDialog(SanPham sp, java.awt.Window owner) {
		super(owner, "Chi tiết Sản phẩm (Profile)", ModalityType.APPLICATION_MODAL);
		setSize(800, 650);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());
		getContentPane().setBackground(ColorScheme.BACKGROUND);

		// Header
		var headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(ColorScheme.PRIMARY);
		headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
		
		var lblTitle = new JLabel("HỒ SƠ TỔNG QUAN SẢN PHẨM");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTitle.setForeground(Color.WHITE);
		headerPanel.add(lblTitle, BorderLayout.WEST);
		
		add(headerPanel, BorderLayout.NORTH);

		// Content (GridBagLayout - 2 cột lớn)
		var contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setBackground(ColorScheme.PANEL_BG);
		contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 15, 10, 25);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 0.5;

		DecimalFormat df = new DecimalFormat("#,### VND");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		DateTimeFormatter dfSD = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// ===== CỘT TRÁI (THÔNG TIN CƠ BẢN) =====
		var leftPanel = createSectionPanel("Thông tin chung");
		
		addGridField(leftPanel, "Mã sản phẩm:", String.valueOf(sp.getMaSanPham()), 0, false, null);
		addGridField(leftPanel, "Tên sản phẩm:", sp.getTenSanPham(), 1, true, ColorScheme.PRIMARY);
		addGridField(leftPanel, "Loại sản phẩm:", sp.getLoaiSanPham(), 2, false, null);
		addGridField(leftPanel, "Đơn vị tính:", sp.getDonViTinh(), 3, false, null);
		addGridField(leftPanel, "Giá bán đề xuất:", sp.getGiaBanDeXuat() != null ? df.format(sp.getGiaBanDeXuat()) : "0 VND", 4, true, new Color(25, 135, 84));
		
		gbc.gridx = 0; gbc.gridy = 0;
		contentPanel.add(leftPanel, gbc);

		// ===== CỘT PHẢI (THÔNG TIN KHO & TRẠNG THÁI) =====
		var rightPanel = createSectionPanel("Tồn kho & Trạng thái");

		addGridField(rightPanel, "Mức tồn tối thiểu:", String.valueOf(sp.getMucTonToiThieu()), 0, false, null);
		
		// Tổng tồn - Highlight
		String strTongTon = sp.getTongTon() + " " + sp.getDonViTinh();
		Color cTon = sp.getTongTon() <= sp.getMucTonToiThieu() ? ColorScheme.DANGER : ColorScheme.PRIMARY;
		addGridField(rightPanel, "Tổng lượng tồn:", strTongTon, 1, true, cTon);
		
		// Hạn sử dụng - Highlight
		String strHanSD = sp.getHanSuDungGanNhat() != null ? sp.getHanSuDungGanNhat().format(dfSD) : "Không xác định";
		Color cHan = ColorScheme.TEXT_PRIMARY;
		if (sp.getHanSuDungGanNhat() != null) {
			if (sp.getHanSuDungGanNhat().isBefore(java.time.LocalDate.now())) {
				cHan = ColorScheme.DANGER; // Hết hạn
				strHanSD += " (Đã quá hạn!)";
			} else if (sp.getHanSuDungGanNhat().isBefore(java.time.LocalDate.now().plusDays(30))) {
				cHan = ColorScheme.WARNING.darker(); // Sắp hết hạn
				strHanSD += " (Sắp hết hạn)";
			} else {
				cHan = ColorScheme.SUCCESS; // An toàn
			}
		}
		addGridField(rightPanel, "Hạn SD gần nhất:", strHanSD, 2, true, cHan);

		addGridField(rightPanel, "Ngày hệ thống tạo:", sp.getNgayTao() != null ? sp.getNgayTao().format(dtf) : "Không xác định", 3, false, null);
		
		String moTaText = (sp.getMoTa() != null && !sp.getMoTa().isEmpty()) ? sp.getMoTa() : "Không có";
		addGridField(rightPanel, "Mô tả chi tiết:", moTaText, 4, false, null);

		gbc.gridx = 1; gbc.gridy = 0;
		contentPanel.add(rightPanel, gbc);

		add(contentPanel, BorderLayout.CENTER);

		// ===== BẢNG CHI TIẾT LÔ HÀNG =====
		var batchPanel = new JPanel(new BorderLayout());
		batchPanel.setBackground(ColorScheme.PANEL_BG);
		batchPanel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER), 
			"Bảng chi tiết lô hàng đang tồn", TitledBorder.LEFT, TitledBorder.TOP,
			new Font("Segoe UI", Font.BOLD, 15), ColorScheme.TEXT_PRIMARY
		));
		
		String[] columnNames = {"Số lô", "Hạn sử dụng", "Số lượng tồn", "Sỉ/Lẻ", "Trạng thái"};
		var tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		var table = new JTable(tableModel);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.setRowHeight(25);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		
		// Load Data từ DB & binding
		List<LoHang> batches = batchDao.getActiveBatchesByMaSP(sp.getMaSanPham());
		java.time.LocalDate now = java.time.LocalDate.now();
		java.time.LocalDate next30Days = now.plusDays(30);
		
		for (LoHang lh : batches) {
			String hanSD = lh.getHanSuDung() != null ? lh.getHanSuDung().format(dfSD) : "N/A";
			String loai = lh.getLoaiHinhBan() != null ? lh.getLoaiHinhBan() : "Chưa xác định";
			String trangThai = "An toàn";
			
			if (lh.getHanSuDung() != null) {
				if (lh.getHanSuDung().isBefore(now)) trangThai = "Quá hạn";
				else if (lh.getHanSuDung().isBefore(next30Days)) trangThai = "Sắp hết hạn";
			}
			
			tableModel.addRow(new Object[] {
				lh.getSoLo(), hanSD, lh.getSoLuongTon() + " " + sp.getDonViTinh(), loai, trangThai
			});
		}
		
		// Highlight Row custom Renderer
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				String status = (String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
				if (!isSelected) {
					if ("Quá hạn".equals(status)) {
						c.setBackground(new Color(255, 230, 230)); // Đỏ nhạt
						c.setForeground(ColorScheme.DANGER);
					} else if ("Sắp hết hạn".equals(status)) {
						c.setBackground(new Color(255, 250, 230)); // Vàng nhạt
						c.setForeground(ColorScheme.WARNING.darker());
					} else {
						c.setBackground(Color.WHITE);
						c.setForeground(ColorScheme.TEXT_PRIMARY);
					}
				}
				return c;
			}
		};
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}
		
		var scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new java.awt.Dimension(750, 150));
		batchPanel.add(scrollPane, BorderLayout.CENTER);
		
		var wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setBackground(ColorScheme.BACKGROUND);
		wrapperPanel.add(contentPanel, BorderLayout.NORTH);
		wrapperPanel.add(batchPanel, BorderLayout.CENTER);
		wrapperPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
		
		add(wrapperPanel, BorderLayout.CENTER);

		// Footer - Nút đóng
		var footerPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 15));
		footerPanel.setBackground(ColorScheme.PANEL_BG);
		footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorScheme.BORDER));
		
		var btnClose = new JButton("Đóng (Esc)");
		btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnClose.setBackground(ColorScheme.NEUTRAL);
		btnClose.setForeground(ColorScheme.TEXT_PRIMARY);
		btnClose.setFocusPainted(false);
		btnClose.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
		btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		btnClose.addActionListener(e -> dispose());
		
		// Phím tắt Esc
		getRootPane().registerKeyboardAction(e -> dispose(),
			javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
			javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		footerPanel.add(btnClose);
		add(footerPanel, BorderLayout.SOUTH);
	}

	private JPanel createSectionPanel(String title) {
		var panel = new JPanel(new GridBagLayout());
		panel.setBackground(ColorScheme.PANEL_BG);
		panel.setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(ColorScheme.BORDER), 
			title, TitledBorder.LEFT, TitledBorder.TOP,
			new Font("Segoe UI", Font.BOLD, 15), ColorScheme.TEXT_PRIMARY
		));
		return panel;
	}

	private void addGridField(JPanel panel, String labelText, String valueText, int gridY, boolean boldVal, Color valColor) {
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(12, 12, 12, 12);
		g.anchor = GridBagConstraints.NORTHWEST;
		g.fill = GridBagConstraints.HORIZONTAL;
		
		// Label
		var lbl = new JLabel(labelText);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lbl.setForeground(ColorScheme.TEXT_SECONDARY);
		g.gridx = 0; g.gridy = gridY;
		g.weightx = 0.3;
		panel.add(lbl, g);

		// Value dùng JTextArea để Wrap Text, ngắt dòng
		var val = new JTextArea(valueText);
		val.setFont(new Font("Segoe UI", boldVal ? Font.BOLD : Font.PLAIN, 15));
		val.setForeground(valColor != null ? valColor : ColorScheme.TEXT_PRIMARY);
		val.setLineWrap(true);
		val.setWrapStyleWord(true);
		val.setEditable(false);
		val.setOpaque(false);
		val.setBorder(null);
		
		g.gridx = 1; g.gridy = gridY;
		g.weightx = 0.7;
		panel.add(val, g);
	}
}
