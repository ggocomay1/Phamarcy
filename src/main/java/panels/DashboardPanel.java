package panels;

import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import common.ColorScheme;
import dao.ThongKeDao;
import entity.NguoiDung;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 * DashboardPanel - Panel tổng quan nâng cao cho nhà thuốc
 * Hiển thị: KPI Cards, Sản phẩm bán chạy, Thuốc sắp hết hạn,
 * Cảnh báo tồn kho, Biểu đồ doanh thu, Phân bố sản phẩm
 * 
 * @author Improved Dashboard
 * @version 3.0
 */
public class DashboardPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Stat card labels
	private JLabel lblProductCount;
	private JLabel lblBatchCount;
	private JLabel lblInvoiceCount;
	private JLabel lblRevenue;
	private JLabel lblCustomerCount;
	private JLabel lblMonthRevenue;

	// Table models
	private DefaultTableModel modelExpiring;
	private DefaultTableModel modelLowStock;

	// Chart panels
	private JPanel chartRevenueContainer;
	private JPanel chartPieContainer;

	// DAO
	private ThongKeDao thongKeDao;

	// Formatter
	private static final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
	private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
	private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
	private static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 16);
	private static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 26);
	private static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.PLAIN, 12);
	private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
	private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);

	public DashboardPanel(NguoiDung currentUser) {
		initialize();
	}

	private void initialize() {
		thongKeDao = new ThongKeDao();
		setLayout(new BorderLayout());
		setBackground(ColorScheme.BACKGROUND);

		// Main Scrollable Container
		JPanel mainContent = new JPanel();
		mainContent.setOpaque(false);
		mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
		mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

		// 1. Header
		mainContent.add(createHeader());
		mainContent.add(Box.createVerticalStrut(20));

		// 2. KPI Stat Cards (Row 1 - 3 cards)
		mainContent.add(createStatCardsRow1());
		mainContent.add(Box.createVerticalStrut(15));

		// 3. KPI Stat Cards (Row 2 - 3 cards)
		mainContent.add(createStatCardsRow2());
		mainContent.add(Box.createVerticalStrut(25));

		// 4. Alerts Row: Expiring + Low Stock (ưu tiên hiển thị trước)
		mainContent.add(createAlertsRow());
		mainContent.add(Box.createVerticalStrut(25));

		// 5. Charts Row: Revenue + Pie distribution
		mainContent.add(createChartsRow());

		// Scroll wrapper with smooth scrolling
		JScrollPane scrollPane = new JScrollPane(mainContent);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(28);
		scrollPane.getVerticalScrollBar().setBlockIncrement(100);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Smooth scroll animation using Timer
		final int[] scrollVelocity = {0};
		final javax.swing.Timer scrollTimer = new javax.swing.Timer(15, null);
		scrollTimer.addActionListener(e -> {
			JScrollBar bar = scrollPane.getVerticalScrollBar();
			int newValue = bar.getValue() + scrollVelocity[0];
			newValue = Math.max(bar.getMinimum(), Math.min(newValue, bar.getMaximum() - bar.getVisibleAmount()));
			bar.setValue(newValue);

			// Decelerate (friction)
			if (scrollVelocity[0] > 0) {
				scrollVelocity[0] = Math.max(0, scrollVelocity[0] - 2);
			} else if (scrollVelocity[0] < 0) {
				scrollVelocity[0] = Math.min(0, scrollVelocity[0] + 2);
			}

			if (scrollVelocity[0] == 0) {
				scrollTimer.stop();
			}
		});

		scrollPane.setWheelScrollingEnabled(false); // disable default wheel handling
		scrollPane.addMouseWheelListener(e -> {
			int amount = e.getWheelRotation() * 18; // scroll step per tick
			scrollVelocity[0] += amount;
			// Cap velocity to prevent excessive speed
			scrollVelocity[0] = Math.max(-80, Math.min(80, scrollVelocity[0]));
			if (!scrollTimer.isRunning()) {
				scrollTimer.start();
			}
		});

		add(scrollPane, BorderLayout.CENTER);

		// Initial Data Load
		refreshData();
	}

	// ===================== HEADER =====================

	private JPanel createHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setOpaque(false);
		header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

		JPanel titlePanel = new JPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

		JLabel lblTitle = new JLabel("📊 Tổng quan nhà thuốc");
		lblTitle.setFont(FONT_TITLE);
		lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
		titlePanel.add(lblTitle);

		JLabel lblSubtitle = new JLabel("Thống kê, cảnh báo và phân tích kinh doanh");
		lblSubtitle.setFont(FONT_SUBTITLE);
		lblSubtitle.setForeground(ColorScheme.TEXT_SECONDARY);
		lblSubtitle.setBorder(new EmptyBorder(4, 2, 0, 0));
		titlePanel.add(lblSubtitle);

		header.add(titlePanel, BorderLayout.WEST);

		// Refresh button
		JButton btnRefresh = new JButton("🔄 Làm mới dữ liệu");
		btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnRefresh.setBackground(ColorScheme.PRIMARY);
		btnRefresh.setForeground(Color.WHITE);
		btnRefresh.setFocusPainted(false);
		btnRefresh.setBorderPainted(false);
		btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnRefresh.setPreferredSize(new Dimension(170, 40));
		btnRefresh.putClientProperty("JButton.buttonType", "roundRect");
		
		// Hover effect
		Color hoverColor = ColorScheme.PRIMARY_HOVER;
		btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent e) {
				btnRefresh.setBackground(hoverColor);
			}
			public void mouseExited(java.awt.event.MouseEvent e) {
				btnRefresh.setBackground(ColorScheme.PRIMARY);
			}
		});
		btnRefresh.addActionListener(e -> refreshData());

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnPanel.setOpaque(false);
		btnPanel.add(btnRefresh);
		header.add(btnPanel, BorderLayout.EAST);

		return header;
	}

	// ===================== STAT CARDS ROW 1 =====================

	private JPanel createStatCardsRow1() {
		JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

		lblProductCount = new JLabel("0");
		lblBatchCount = new JLabel("0");
		lblInvoiceCount = new JLabel("0");

		row.add(createStatCard("📦 Tổng sản phẩm", lblProductCount, 
				new Color(59, 130, 246), new Color(239, 246, 255)));
		row.add(createStatCard("📋 Lô hàng còn tồn", lblBatchCount, 
				new Color(16, 185, 129), new Color(236, 253, 245)));
		row.add(createStatCard("🧾 Hóa đơn hôm nay", lblInvoiceCount, 
				new Color(245, 158, 11), new Color(255, 251, 235)));

		return row;
	}

	// ===================== STAT CARDS ROW 2 =====================

	private JPanel createStatCardsRow2() {
		JPanel row = new JPanel(new GridLayout(1, 3, 15, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

		lblRevenue = new JLabel("0₫");
		lblCustomerCount = new JLabel("0");
		lblMonthRevenue = new JLabel("0₫");

		row.add(createStatCard("💰 Doanh thu hôm nay", lblRevenue, 
				new Color(239, 68, 68), new Color(254, 242, 242)));
		row.add(createStatCard("👥 Khách hàng", lblCustomerCount, 
				new Color(139, 92, 246), new Color(245, 243, 255)));
		row.add(createStatCard("📈 Doanh thu tháng", lblMonthRevenue, 
				new Color(6, 182, 212), new Color(236, 254, 255)));

		return row;
	}

	/**
	 * Tạo stat card với accent color và background color
	 */
	private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor, Color bgColor) {
		JPanel card = new JPanel(new BorderLayout(0, 8));
		card.setBackground(ColorScheme.PANEL_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
			new EmptyBorder(18, 20, 18, 20)
		));

		// Left accent stripe
		JPanel accentStripe = new JPanel();
		accentStripe.setPreferredSize(new Dimension(4, 0));
		accentStripe.setBackground(accentColor);
		card.add(accentStripe, BorderLayout.WEST);

		// Content
		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(new EmptyBorder(0, 12, 0, 0));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FONT_CARD_TITLE);
		lblTitle.setForeground(ColorScheme.TEXT_SECONDARY);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		content.add(lblTitle);
		content.add(Box.createVerticalStrut(6));

		valueLabel.setFont(FONT_CARD_VALUE);
		valueLabel.setForeground(accentColor);
		valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		content.add(valueLabel);

		card.add(content, BorderLayout.CENTER);
		return card;
	}

	// ===================== CHARTS ROW =====================

	private JPanel createChartsRow() {
		JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

		// Revenue chart
		chartRevenueContainer = createCardPanel("💰 Doanh thu 7 ngày gần nhất");
		row.add(chartRevenueContainer);

		// Pie chart
		chartPieContainer = createCardPanel("🥧 Phân bố sản phẩm theo loại");
		row.add(chartPieContainer);

		return row;
	}

	private JPanel createCardPanel(String title) {
		JPanel card = new JPanel(new BorderLayout(0, 10));
		card.setBackground(ColorScheme.PANEL_BG);
		card.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
			new EmptyBorder(15, 15, 15, 15)
		));

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FONT_SECTION);
		lblTitle.setForeground(ColorScheme.TEXT_PRIMARY);
		card.add(lblTitle, BorderLayout.NORTH);

		return card;
	}


	// ===================== ALERTS ROW =====================

	private JPanel createAlertsRow() {
		JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

		// Sắp hết hạn
		modelExpiring = new DefaultTableModel(
			new Object[]{"Tên thuốc", "Số lô", "Hạn SD", "Còn lại"}, 0
		) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		row.add(createAlertTablePanel(
			"⚠️ Thuốc sắp hết hạn (≤ 90 ngày)", 
			modelExpiring,
			new Color(245, 158, 11),
			new Color(255, 251, 235)
		));

		// Tồn kho thấp
		modelLowStock = new DefaultTableModel(
			new Object[]{"Tên thuốc", "Tồn kho", "Mức tối thiểu"}, 0
		) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		row.add(createAlertTablePanel(
			"📉 Thuốc cần nhập (Tồn kho thấp)",
			modelLowStock,
			new Color(239, 68, 68),
			new Color(254, 242, 242)
		));

		return row;
	}

	private JPanel createAlertTablePanel(String title, DefaultTableModel model, Color headerColor, Color headerBg) {
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBackground(ColorScheme.PANEL_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
			new EmptyBorder(15, 15, 15, 15)
		));

		// Title with badge count
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FONT_SECTION);
		lblTitle.setForeground(headerColor);
		titlePanel.add(lblTitle, BorderLayout.WEST);

		panel.add(titlePanel, BorderLayout.NORTH);

		JTable table = createStyledTable(model);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
		forwardScrollToParent(scroll);
		panel.add(scroll, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Forward mouse wheel events from inner JScrollPane to the parent scroll pane,
	 * so scrolling the dashboard doesn't freeze when the cursor is over a table.
	 */
	private void forwardScrollToParent(JScrollPane innerScroll) {
		innerScroll.setWheelScrollingEnabled(false);
		innerScroll.addMouseWheelListener(e -> {
			// Forward the event to the parent component
			Component parent = innerScroll.getParent();
			while (parent != null && !(parent instanceof JScrollPane)) {
				parent = parent.getParent();
			}
			if (parent != null) {
				parent.dispatchEvent(javax.swing.SwingUtilities.convertMouseEvent(
					innerScroll, e, parent));
			}
		});
	}

	private JTable createStyledTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setRowHeight(32);
		table.setFont(FONT_TABLE);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setGridColor(new Color(243, 244, 246));
		table.setSelectionBackground(new Color(219, 234, 254));
		table.setSelectionForeground(ColorScheme.TEXT_PRIMARY);
		table.setIntercellSpacing(new Dimension(0, 0));

		// Header style
		JTableHeader header = table.getTableHeader();
		header.setFont(FONT_TABLE_HEADER);
		header.setBackground(new Color(249, 250, 251));
		header.setForeground(ColorScheme.TEXT_PRIMARY);
		header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

		// Alternating row colors
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (!isSelected) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
				}
				setBorder(new EmptyBorder(0, 8, 0, 8));
				return c;
			}
		});

		return table;
	}

	// ===================== REFRESH DATA =====================

	private void refreshData() {
		SwingWorker<Void, Void> worker = new SwingWorker<>() {
			// Data holders
			ThongKeDao.ThongKeNgay thongKeNgay;
			int tongSanPham, tongLoHang, tongKhachHang;
			java.math.BigDecimal doanhThuThang;

			List<ThongKeDao.DoanhThuNgay> revenueChart;
			Map<String, Integer> categoryPie;
			List<ThongKeDao.CanhBaoHetHan> listHetHan;
			List<ThongKeDao.CanhBaoTonKho> listCanNhap;

			@Override
			protected Void doInBackground() {
				try {
					thongKeNgay = thongKeDao.getThongKeNgay();
					tongSanPham = thongKeDao.getTongSanPham();
					tongLoHang = thongKeDao.getTongLoHang();
					tongKhachHang = thongKeDao.getTongKhachHang();
					doanhThuThang = thongKeDao.getDoanhThuThang();

					revenueChart = thongKeDao.getDoanhThu7NgayGanNhat();
					categoryPie = thongKeDao.getPhanBoSanPhamTheoLoai();
					listHetHan = thongKeDao.getThuocSapHetHan(90);
					listCanNhap = thongKeDao.getThuocCanNhap();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void done() {
				try {
					// 1. Update stat cards
					lblProductCount.setText(String.valueOf(tongSanPham));
					lblBatchCount.setText(String.valueOf(tongLoHang));
					lblInvoiceCount.setText(String.valueOf(thongKeNgay != null ? thongKeNgay.soHoaDon : 0));
					lblRevenue.setText(VND.format(thongKeNgay != null ? thongKeNgay.doanhThu : 0));
					lblCustomerCount.setText(String.valueOf(tongKhachHang));
					lblMonthRevenue.setText(VND.format(doanhThuThang != null ? doanhThuThang : 0));

					// 2. Revenue Chart
					updateRevenueChart(revenueChart);

					// 3. Pie Chart
					updatePieChart(categoryPie);



					// 5. Expiring
					updateExpiringTable(listHetHan);

					// 6. Low Stock
					updateLowStockTable(listCanNhap);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}

	// ===================== CHART UPDATES =====================

	private void updateRevenueChart(List<ThongKeDao.DoanhThuNgay> data) {
		// Remove old chart
		for (Component c : chartRevenueContainer.getComponents()) {
			if (c instanceof ChartPanel) chartRevenueContainer.remove(c);
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		if (data != null) {
			for (var item : data) {
				dataset.addValue(item.doanhThu, "Doanh thu", item.ngay);
			}
		}

		JFreeChart chart = ChartFactory.createBarChart(
			"", "", "VNĐ", dataset,
			PlotOrientation.VERTICAL, false, true, false
		);

		// Styling
		chart.setBackgroundPaint(Color.WHITE);
		chart.setBorderVisible(false);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinePaint(new Color(243, 244, 246));
		plot.setRangeGridlinePaint(new Color(229, 231, 235));
		plot.setOutlineVisible(false);

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setSeriesPaint(0, new Color(59, 130, 246));
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setShadowVisible(false);
		renderer.setDrawBarOutline(false);
		renderer.setMaximumBarWidth(0.08);

		plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
		plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 240));
		chartRevenueContainer.add(chartPanel, BorderLayout.CENTER);
		chartRevenueContainer.revalidate();
		chartRevenueContainer.repaint();
	}

	@SuppressWarnings("unchecked")
	private void updatePieChart(Map<String, Integer> data) {
		// Remove old chart
		for (Component c : chartPieContainer.getComponents()) {
			if (c instanceof ChartPanel) chartPieContainer.remove(c);
		}

		@SuppressWarnings("rawtypes")
		DefaultPieDataset dataset = new DefaultPieDataset();
		Color[] pieColors = {
			new Color(59, 130, 246),   // Blue
			new Color(16, 185, 129),   // Green
			new Color(245, 158, 11),   // Amber
			new Color(239, 68, 68),    // Red
			new Color(139, 92, 246)    // Violet
		};

		if (data != null) {
			for (var entry : data.entrySet()) {
				dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
			}
		}

		JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setBorderVisible(false);

		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setOutlineVisible(false);
		plot.setShadowPaint(null);
		plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
		plot.setLabelBackgroundPaint(new Color(255, 255, 255, 200));
		plot.setLabelOutlinePaint(null);
		plot.setLabelShadowPaint(null);

		// Apply colors
		int i = 0;
		if (data != null) {
			for (var key : data.keySet()) {
				String label = key + " (" + data.get(key) + ")";
				if (i < pieColors.length) {
					plot.setSectionPaint(label, pieColors[i]);
				}
				i++;
			}
		}

		chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
		chart.getLegend().setBackgroundPaint(Color.WHITE);
		chart.getLegend().setBorder(0, 0, 0, 0);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(400, 240));
		chartPieContainer.add(chartPanel, BorderLayout.CENTER);
		chartPieContainer.revalidate();
		chartPieContainer.repaint();
	}

	// ===================== TABLE UPDATES =====================


	private void updateExpiringTable(List<ThongKeDao.CanhBaoHetHan> data) {
		modelExpiring.setRowCount(0);
		if (data != null) {
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
			for (var item : data) {
				String conLai = item.soNgayConLai + " ngày";
				if (item.soNgayConLai <= 7) {
					conLai = "⛔ " + conLai;
				} else if (item.soNgayConLai <= 30) {
					conLai = "🔴 " + conLai;
				} else {
					conLai = "🟡 " + conLai;
				}
				modelExpiring.addRow(new Object[]{
					item.tenSanPham,
					item.soLo,
					sdf.format(item.hanSuDung),
					conLai
				});
			}
		}
		if (modelExpiring.getRowCount() == 0) {
			modelExpiring.addRow(new Object[]{"✅ Không có thuốc sắp hết hạn", "", "", ""});
		}
	}

	private void updateLowStockTable(List<ThongKeDao.CanhBaoTonKho> data) {
		modelLowStock.setRowCount(0);
		if (data != null) {
			for (var item : data) {
				String icon = item.tongTon == 0 ? "🔴 " : "🟡 ";
				modelLowStock.addRow(new Object[]{
					icon + item.tenSanPham,
					item.tongTon,
					item.mucTonToiThieu
				});
			}
		}
		if (modelLowStock.getRowCount() == 0) {
			modelLowStock.addRow(new Object[]{"✅ Tất cả sản phẩm đủ tồn kho", "", ""});
		}
	}
}
