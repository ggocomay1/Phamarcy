package panels;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import common.ColorScheme;
import common.UIHelper;
import entity.NguoiDung;
import service.HoaDonService;
import service.HoaDonService.*;

/**
 * HoaDonHistoryPanel - Panel lịch sử hóa đơn bán hàng chuẩn POS
 *
 * @version 1.0
 */
public class HoaDonHistoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private NguoiDung currentUser;
    private DefaultTableModel tableModel;
    private JTable table;

    // Filter components
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<String> cbNhanVien;
    private JComboBox<String> cbTrangThai;
    private JTextField txtTimKiem;

    // Formatters
    private static final DecimalFormat VND = new DecimalFormat("#,###");
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DF_SQL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Service & data cache
    private final HoaDonService hoaDonService = new HoaDonService();
    private List<InvoiceRow> dataCache = new ArrayList<>();

    public HoaDonHistoryPanel(NguoiDung currentUser) {
        this.currentUser = currentUser;
        initialize();
        loadData();
    }

    // ── Filter UI constants ──
    private static final Color FILTER_BG = Color.WHITE;
    private static final Color FILTER_BRD = new Color(227, 234, 242);
    private static final Color TEAL = new Color(20, 184, 166);
    private static final Color TXT_PRI = new Color(31, 45, 61);
    private static final Color TXT_SEC = new Color(107, 122, 140);
    private static final int FIELD_H = 38;

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 248, 251));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ── TOP: Title + Filter ──
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        top.add(initTitleSection());
        top.add(Box.createVerticalStrut(14));
        top.add(initFilterCard());
        top.add(Box.createVerticalStrut(16));

        add(top, BorderLayout.NORTH);

        // ── TABLE ──
        add(createTablePanel(), BorderLayout.CENTER);

        // ── BOTTOM ──
        add(createBottomToolbar(), BorderLayout.SOUTH);
    }

    // ================================================================
    //  TITLE SECTION
    // ================================================================
    private JPanel initTitleSection() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblTitle = new JLabel("Lịch sử hóa đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TXT_PRI);
        p.add(lblTitle, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Xem và quản lý lịch sử giao dịch bán hàng");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TXT_SEC);
        lblSub.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        p.add(lblSub, BorderLayout.SOUTH);

        return p;
    }

    // ================================================================
    //  FILTER CARD
    // ================================================================
    private JPanel initFilterCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(FILTER_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FILTER_BRD, 1),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 14);
        gbc.gridy = 0;

        int col = 0;

        // ── Từ ngày ──
        txtTuNgay = mkFilterField(10);
        txtTuNgay.setText(LocalDate.now().minusMonths(1).format(DF));
        txtTuNgay.setToolTipText("dd/MM/yyyy");
        gbc.gridx = col++; gbc.weightx = 0;
        card.add(mkFilterGroup("Từ ngày", txtTuNgay), gbc);

        // ── Đến ngày ──
        txtDenNgay = mkFilterField(10);
        txtDenNgay.setText(LocalDate.now().format(DF));
        txtDenNgay.setToolTipText("dd/MM/yyyy");
        gbc.gridx = col++; gbc.weightx = 0;
        card.add(mkFilterGroup("Đến ngày", txtDenNgay), gbc);

        // ── Nhân viên ──
        cbNhanVien = new JComboBox<>(new String[]{"Tất cả"});
        styleCombo(cbNhanVien, 140);
        loadNhanVienCombo();
        gbc.gridx = col++; gbc.weightx = 0;
        card.add(mkFilterGroup("Nhân viên", cbNhanVien), gbc);

        // ── Trạng thái ──
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoàn thành", "Đã hủy"});
        styleCombo(cbTrangThai, 130);
        gbc.gridx = col++; gbc.weightx = 0;
        card.add(mkFilterGroup("Trạng thái", cbTrangThai), gbc);

        // ── Tìm kiếm (chiếm phần co giãn) ──
        txtTimKiem = mkFilterField(16);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Mã HĐ, tên KH, SĐT...");
        txtTimKiem.addActionListener(e -> handleFilter());
        gbc.gridx = col++; gbc.weightx = 1.0;
        card.add(mkFilterGroup("Tìm kiếm", txtTimKiem), gbc);

        // ── Nút Lọc ──
        JButton btnLoc = mkTealButton("Lọc");
        btnLoc.addActionListener(e -> handleFilter());
        gbc.gridx = col++; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 8);
        card.add(mkFilterGroup(" ", btnLoc), gbc);  // label trống để canh hàng

        // ── Nút Làm mới ──
        JButton btnLamMoi = mkOutlineButton("Làm mới");
        btnLamMoi.addActionListener(e -> handleLamMoi());
        gbc.gridx = col++; gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(mkFilterGroup(" ", btnLamMoi), gbc);

        return card;
    }

    // ================================================================
    //  FILTER HELPERS
    // ================================================================

    /** Tạo nhóm Label + Component xếp dọc */
    private JPanel mkFilterGroup(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TXT_SEC);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        p.add(lbl);

        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(field);
        return p;
    }

    /** Tạo textfield filter chuẩn */
    private JTextField mkFilterField(int columns) {
        JTextField txt = new JTextField(columns);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setPreferredSize(new Dimension(0, FIELD_H));
        txt.setMinimumSize(new Dimension(60, FIELD_H));
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
        return txt;
    }

    /** Style cho JComboBox */
    private void styleCombo(JComboBox<String> cb, int width) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setPreferredSize(new Dimension(width, FIELD_H));
        cb.setMinimumSize(new Dimension(width, FIELD_H));
        cb.setMaximumSize(new Dimension(width, FIELD_H));
    }

    /** Nút primary teal */
    private JButton mkTealButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(TEAL);
        btn.setPreferredSize(new Dimension(80, FIELD_H));
        btn.setMinimumSize(new Dimension(80, FIELD_H));
        btn.setMaximumSize(new Dimension(80, FIELD_H));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(TEAL.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(TEAL); }
        });
        return btn;
    }

    /** Nút outline/ghost */
    private JButton mkOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(TXT_SEC);
        btn.setBackground(FILTER_BG);
        btn.setPreferredSize(new Dimension(100, FIELD_H));
        btn.setMinimumSize(new Dimension(100, FIELD_H));
        btn.setMaximumSize(new Dimension(100, FIELD_H));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(FILTER_BRD, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(245, 248, 251)); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(FILTER_BG); }
        });
        return btn;
    }


    /**
     * Tạo table panel
     */
    private JPanel createTablePanel() {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.PANEL_BG);
        panel.setBorder(new TitledBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER, 1),
            "Danh sách hóa đơn",
            TitledBorder.LEADING,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            ColorScheme.TEXT_PRIMARY
        ));

        // Table Model
        String[] columns = {"STT", "Mã HĐ", "Thời gian", "Nhân viên", "Khách hàng",
                            "Số mặt hàng", "Tổng tiền", "PTTT", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(235, 238, 242));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setRowSorter(new TableRowSorter<>(tableModel));

        // Header styling
        var headerStyle = table.getTableHeader();
        headerStyle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerStyle.setBackground(new Color(243, 246, 250)); // #F3F6FA
        headerStyle.setForeground(ColorScheme.TEXT_PRIMARY);
        headerStyle.setPreferredSize(new Dimension(0, 36));

        // Column widths
        var cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(45);   // STT
        cm.getColumn(0).setMaxWidth(55);
        cm.getColumn(1).setPreferredWidth(85);   // Mã HĐ
        cm.getColumn(1).setMaxWidth(100);
        cm.getColumn(2).setPreferredWidth(140);  // Thời gian
        cm.getColumn(3).setPreferredWidth(120);  // Nhân viên
        cm.getColumn(4).setPreferredWidth(130);  // Khách hàng
        cm.getColumn(5).setPreferredWidth(90);   // Số mặt hàng
        cm.getColumn(5).setMaxWidth(100);
        cm.getColumn(6).setPreferredWidth(120);  // Tổng tiền
        cm.getColumn(7).setPreferredWidth(120);  // PTTT
        cm.getColumn(8).setPreferredWidth(110);  // Trạng thái

        // CENTER renderers
        var centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        cm.getColumn(0).setCellRenderer(centerRenderer); // STT
        cm.getColumn(5).setCellRenderer(centerRenderer); // Số mặt hàng

        // RIGHT renderer for Tổng tiền
        var rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        cm.getColumn(6).setCellRenderer(rightRenderer);

        // Status badge renderer
        cm.getColumn(8).setCellRenderer(new StatusBadgeRenderer());

        // Double-click to view detail
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showInvoiceDetail();
                }
            }
        });

        // Row hover effect
        table.addMouseMotionListener(new MouseMotionAdapter() {
            int lastRow = -1;
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    table.repaint();
                }
            }
        });

        var scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER));
        scrollPane.getViewport().setBackground(ColorScheme.PANEL_BG);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tạo toolbar phía dưới
     */
    private JPanel createBottomToolbar() {
        var panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorScheme.PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, ColorScheme.BORDER),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Left: Total info
        var lblTotal = new JLabel("Tổng: 0 hóa đơn");
        lblTotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTotal.setForeground(ColorScheme.TEXT_SECONDARY);
        panel.add(lblTotal, BorderLayout.WEST);

        // Update total label reference
        // We'll update this in loadData
        panel.putClientProperty("lblTotal", lblTotal);

        // Right: Action buttons
        var actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        var btnXemChiTiet = UIHelper.createPrimaryButton("Xem chi tiết");
        btnXemChiTiet.setPreferredSize(new Dimension(130, 36));
        btnXemChiTiet.addActionListener(e -> showInvoiceDetail());
        actionPanel.add(btnXemChiTiet);

        var btnInHoaDon = UIHelper.createOutlineButton("In hóa đơn", ColorScheme.PRIMARY);
        btnInHoaDon.setPreferredSize(new Dimension(120, 36));
        btnInHoaDon.addActionListener(e -> handlePrintInvoice());
        actionPanel.add(btnInHoaDon);

        var btnXuatExcel = UIHelper.createSuccessButton("Xuất Excel");
        btnXuatExcel.setPreferredSize(new Dimension(120, 36));
        btnXuatExcel.addActionListener(e -> handleExportExcel());
        actionPanel.add(btnXuatExcel);

        panel.add(actionPanel, BorderLayout.EAST);

        return panel;
    }

    // ===== DATA METHODS =====

    /**
     * Load danh sách nhân viên vào combo
     */
    private void loadNhanVienCombo() {
        for (String name : hoaDonService.getActiveStaffNames()) {
            cbNhanVien.addItem(name);
        }
    }

    /**
     * Load dữ liệu hóa đơn
     */
    public void loadData() {
        loadDataWithFilter(null, null, null, null, null);
    }

    private void loadDataWithFilter(String tuNgay, String denNgay, String nhanVien,
                                     String trangThai, String keyword) {
        tableModel.setRowCount(0);
        dataCache.clear();

        // Tạo filter DTO
        var filter = new InvoiceFilter();
        filter.tuNgaySql = tuNgay;
        filter.denNgaySql = denNgay;
        filter.nhanVien = nhanVien;
        filter.trangThai = trangThai;
        filter.keyword = keyword;

        // Gọi service
        var invoices = hoaDonService.searchInvoices(filter);
        dataCache.addAll(invoices);

        int stt = 1;
        for (InvoiceRow inv : invoices) {
            String thoiGian = inv.ngayBan != null ? inv.ngayBan.format(DTF) : "";
            String tongTienStr = inv.tongTien != null ? VND.format(inv.tongTien) + "đ" : "0đ";

            tableModel.addRow(new Object[]{
                stt++, inv.getMaHDDisplay(), thoiGian, inv.tenNhanVien,
                inv.tenKhachHang, inv.soMatHang, tongTienStr,
                inv.phuongThucTT, inv.trangThai
            });
        }

        updateTotalLabel();

        // Client-side filter trạng thái
        if (trangThai != null && !"Tất cả".equals(trangThai)) {
            if (table.getRowSorter() instanceof TableRowSorter<?>) {
                var sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
                sorter.setRowFilter(javax.swing.RowFilter.regexFilter(trangThai, 8));
            }
        } else {
            if (table.getRowSorter() instanceof TableRowSorter<?>) {
                var sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
                sorter.setRowFilter(null);
            }
        }
    }

    private void updateTotalLabel() {
        // Find the bottom toolbar and update label
        for (var comp : getComponents()) {
            if (comp instanceof JPanel p) {
                var obj = ((JPanel) comp).getClientProperty("lblTotal");
                if (obj instanceof JLabel lbl) {
                    int count = tableModel.getRowCount();
                    lbl.setText("Tổng: " + count + " hóa đơn");
                }
            }
        }
    }

    // ===== FILTER HANDLERS =====

    private void handleFilter() {
        String tuNgay = null, denNgay = null;

        // Parse dates
        try {
            String tuNgayText = txtTuNgay.getText().trim();
            if (!tuNgayText.isEmpty()) {
                LocalDate d = LocalDate.parse(tuNgayText, DF);
                tuNgay = d.format(DF_SQL);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không hợp lệ! (dd/MM/yyyy)",
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String denNgayText = txtDenNgay.getText().trim();
            if (!denNgayText.isEmpty()) {
                LocalDate d = LocalDate.parse(denNgayText, DF);
                denNgay = d.format(DF_SQL);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không hợp lệ! (dd/MM/yyyy)",
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nhanVien = (String) cbNhanVien.getSelectedItem();
        String trangThai = (String) cbTrangThai.getSelectedItem();
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) keyword = null;

        loadDataWithFilter(tuNgay, denNgay, nhanVien, trangThai, keyword);
    }

    private void handleLamMoi() {
        txtTuNgay.setText(LocalDate.now().minusMonths(1).format(DF));
        txtDenNgay.setText(LocalDate.now().format(DF));
        cbNhanVien.setSelectedIndex(0);
        cbTrangThai.setSelectedIndex(0);
        txtTimKiem.setText("");
        table.clearSelection();
        loadData();
    }

    // ===== DETAIL DIALOG =====

    private void showInvoiceDetail() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow >= dataCache.size()) return;

        InvoiceRow cached = dataCache.get(modelRow);
        int maHD = cached.maHoaDon;
        LocalDateTime ngayBan = cached.ngayBan;
        String tenNV = cached.tenNhanVien;
        String tenKH = cached.tenKhachHang;
        BigDecimal tongTien = cached.tongTien;
        String pttt = cached.phuongThucTT;
        String maHDStr = cached.getMaHDDisplay();

        // Create detail dialog
        var dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
            "Chi tiết hóa đơn - " + maHDStr, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(750, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(ColorScheme.BACKGROUND);

        // Header
        var headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        var lblDialogTitle = new JLabel("CHI TIẾT HÓA ĐƠN " + maHDStr);
        lblDialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDialogTitle.setForeground(Color.WHITE);
        headerPanel.add(lblDialogTitle, BorderLayout.WEST);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Info section
        var infoPanel = new JPanel(new GridLayout(2, 3, 20, 8));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(15, 25, 10, 25));

        infoPanel.add(createInfoField("Mã hóa đơn:", maHDStr));
        infoPanel.add(createInfoField("Thời gian:", ngayBan != null ? ngayBan.format(DTF) : ""));
        infoPanel.add(createInfoField("Nhân viên:", tenNV));
        infoPanel.add(createInfoField("Khách hàng:", tenKH));
        infoPanel.add(createInfoField("Thanh toán:", pttt));
        infoPanel.add(createInfoField("Tổng tiền:", tongTien != null ? VND.format(tongTien) + "đ" : "0đ"));

        // Detail table
        String[] detailCols = {"STT", "Tên thuốc", "Số lô", "Đơn vị", "Số lượng", "Đơn giá", "Thành tiền"};
        var detailModel = new DefaultTableModel(detailCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        var detailTable = new JTable(detailModel);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailTable.setRowHeight(28);
        detailTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailTable.setShowGrid(true);
        detailTable.setGridColor(new Color(235, 238, 242));

        // Column widths
        var dcm = detailTable.getColumnModel();
        dcm.getColumn(0).setPreferredWidth(40);
        dcm.getColumn(0).setMaxWidth(50);
        dcm.getColumn(1).setPreferredWidth(200);
        dcm.getColumn(2).setPreferredWidth(80);
        dcm.getColumn(3).setPreferredWidth(60);
        dcm.getColumn(4).setPreferredWidth(70);
        dcm.getColumn(5).setPreferredWidth(100);
        dcm.getColumn(6).setPreferredWidth(110);

        // Right align for money columns
        var rightR = new DefaultTableCellRenderer();
        rightR.setHorizontalAlignment(JLabel.RIGHT);
        dcm.getColumn(5).setCellRenderer(rightR);
        dcm.getColumn(6).setCellRenderer(rightR);
        var centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(JLabel.CENTER);
        dcm.getColumn(0).setCellRenderer(centerR);
        dcm.getColumn(4).setCellRenderer(centerR);

        // Load detail data via service
        var details = hoaDonService.getInvoiceDetails(maHD);
        int stt = 1;
        for (InvoiceDetailRow d : details) {
            detailModel.addRow(new Object[]{
                stt++,
                d.tenSanPham,
                d.soLo,
                d.donViTinh,
                d.soLuong,
                VND.format(d.giaBan) + "đ",
                VND.format(d.thanhTien) + "đ"
            });
        }

        // Table container
        var tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(ColorScheme.PANEL_BG);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(0, 25, 10, 25),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ColorScheme.BORDER),
                "Danh sách thuốc", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ColorScheme.TEXT_PRIMARY
            )
        ));
        tableContainer.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        // Total row
        var totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        totalPanel.setOpaque(false);
        var lblTongTien = new JLabel("TỔNG TIỀN: " + (tongTien != null ? VND.format(tongTien) + "đ" : "0đ"));
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTien.setForeground(ColorScheme.PRIMARY);
        totalPanel.add(lblTongTien);
        tableContainer.add(totalPanel, BorderLayout.SOUTH);

        // Wrapper
        var wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.BACKGROUND);
        wrapper.add(infoPanel, BorderLayout.NORTH);
        wrapper.add(tableContainer, BorderLayout.CENTER);
        dialog.add(wrapper, BorderLayout.CENTER);

        // Footer
        var footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setBackground(ColorScheme.PANEL_BG);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorScheme.BORDER));

        var btnClose = UIHelper.createNeutralButton("Đóng (Esc)");
        btnClose.setPreferredSize(new Dimension(120, 36));
        btnClose.addActionListener(e -> dialog.dispose());
        footerPanel.add(btnClose);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        // Escape to close
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }

    private JPanel createInfoField(String label, String value) {
        var panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        var lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ColorScheme.TEXT_SECONDARY);
        panel.add(lbl, BorderLayout.NORTH);

        var val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        val.setForeground(ColorScheme.TEXT_PRIMARY);
        panel.add(val, BorderLayout.CENTER);

        return panel;
    }

    // ===== ACTION HANDLERS =====

    private void handlePrintInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần in!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Chức năng in hóa đơn sẽ được phát triển trong phiên bản tiếp theo.",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleExportExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Chức năng xuất Excel sẽ được phát triển trong phiên bản tiếp theo.",
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== INNER CLASSES =====

    /**
     * Status badge renderer cho cột Trạng thái
     */
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        private static final Color BG_SUCCESS = new Color(220, 252, 231);
        private static final Color FG_SUCCESS = new Color(21, 128, 61);
        private static final Color BG_DANGER = new Color(254, 226, 226);
        private static final Color FG_DANGER = new Color(185, 28, 28);
        private static final Color BG_WARNING = new Color(255, 237, 213);
        private static final Color FG_WARNING = new Color(194, 120, 3);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

            String text = value != null ? value.toString().trim() : "";
            label.setText(text);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setBorder(new EmptyBorder(4, 8, 4, 8));
            label.setOpaque(true);

            if (!isSelected) {
                switch (text) {
                    case "Hoàn thành" -> { label.setBackground(BG_SUCCESS); label.setForeground(FG_SUCCESS); }
                    case "Đã hủy" -> { label.setBackground(BG_DANGER); label.setForeground(FG_DANGER); }
                    case "Đang xử lý" -> { label.setBackground(BG_WARNING); label.setForeground(FG_WARNING); }
                    default -> { label.setBackground(new Color(243, 244, 246)); label.setForeground(new Color(75, 85, 99)); }
                }
            }
            return label;
        }
    }
}
