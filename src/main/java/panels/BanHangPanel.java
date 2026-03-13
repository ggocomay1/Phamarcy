package panels;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import common.ColorScheme;
import components.ChiTietHoaDonTableModel;
import dao.*;
import entity.*;

/**
 * BanHangPanel – POS Nhà thuốc MEPHAR
 * Layout 2 cột: Trái=Tìm thuốc | Phải=Khách+Giỏ+Thanh toán+Tổng kết
 * @version 7.0 – Pharmacy POS Refactor
 */
public class BanHangPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    enum CustomerMode { RETAIL, MEMBER }
    enum PaymentMethod { CASH, TRANSFER }

    // ── PALETTE ──
    private static final Color BG       = new Color(245,248,251);
    private static final Color CARD     = Color.WHITE;
    private static final Color BRD      = new Color(227,234,242);
    private static final Color DIV      = new Color(241,245,249);
    private static final Color ACCENT   = new Color(20,184,166);  // teal dược
    private static final Color BLUE     = new Color(46,139,255);
    private static final Color GREEN    = new Color(34,160,107);
    private static final Color ORANGE   = new Color(245,158,11);
    private static final Color DANGER   = new Color(229,72,77);
    private static final Color TXT1     = new Color(31,45,61);
    private static final Color TXT2     = new Color(107,122,140);
    private static final Color TXT_M    = new Color(148,163,184);
    private static final Color HDR_BG   = new Color(243,246,250);
    private static final Color SEL_BG   = new Color(219,234,254);
    private static final Color HOVER_BG = new Color(241,245,249);
    private static final Color ALT_BG   = new Color(249,250,252);

    // ── FONTS ──
    private static final Font F_TITLE  = new Font("Segoe UI",Font.BOLD,20);
    private static final Font F_SEC    = new Font("Segoe UI",Font.BOLD,14);
    private static final Font F_LBL    = new Font("Segoe UI",Font.PLAIN,13);
    private static final Font F_LBL_B  = new Font("Segoe UI",Font.BOLD,13);
    private static final Font F_INPUT  = new Font("Segoe UI",Font.PLAIN,14);
    private static final Font F_TABLE  = new Font("Segoe UI",Font.PLAIN,13);
    private static final Font F_TH     = new Font("Segoe UI",Font.BOLD,12);
    private static final Font F_BTN    = new Font("Segoe UI",Font.BOLD,13);
    private static final Font F_TOTAL  = new Font("Segoe UI",Font.BOLD,20);
    private static final Font F_PAY    = new Font("Segoe UI",Font.BOLD,16);
    private static final Font F_HINT   = new Font("Segoe UI",Font.ITALIC,12);
    private static final Font F_MONEY  = new Font("Segoe UI",Font.BOLD,15);
    private static final Font F_BADGE  = new Font("Segoe UI",Font.BOLD,11);
    private static final Font F_SM     = new Font("Segoe UI",Font.PLAIN,12);
    private static final Font F_SM_B   = new Font("Segoe UI",Font.BOLD,11);
    private static final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi","VN"));

    // ── SERVICE (primary) + DAO (legacy – sẽ loại bỏ dần) ──
    private final service.BanHangService banHangService = new service.BanHangService();
    private final HoaDonBanDao hoaDonDao = new HoaDonBanDao();
    private final ChiTietHoaDonDao cthdDao = new ChiTietHoaDonDao();
    private final SanPhamDao spDao = new SanPhamDao();
    private final KhachHangDao khDao = new KhachHangDao();
    private final NguoiDung currentUser;

    // ── STATE ──
    private Integer currentMaHoaDon, selectedMaKH;
    private List<SanPham> productCache;
    private CustomerMode customerMode = CustomerMode.RETAIL;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;
    private boolean updatingInvoice = false;

    // ── UI COMPONENTS ──
    private JTextField txtSearch, txtTimKH, txtTienKhach, txtTransferRef;
    private DefaultTableModel mdlProduct;
    private JTable tblProduct, tblInvoice;
    private JLabel lblProductCount, lblMaHoaDon, lblTongTien;
    private JLabel lblKHName, lblKHPhone, lblKHPoints;
    private JLabel lblSubtotal, lblItems, lblSummaryTotal, lblTienThua;
    private JPanel emptyState;
    private JScrollPane invoiceScroll;
    private ChiTietHoaDonTableModel mdlInvoice;
    private JRadioButton rbRetail, rbMember, rbCash, rbTransfer;
    private JButton btnXoaKH;
    private CardLayout payCardLayout;
    private JCheckBox chkTransferConfirm;
    private List<KhachHang> khCache;
    private JPopupMenu popupKH;
    private DefaultListModel<String> khModel;
    private JList<String> khList;
    private javax.swing.Timer searchKHTimer;

    public BanHangPanel(NguoiDung currentUser) {
        this.currentUser = currentUser;
        buildUI();
        loadProducts(null);
        resetSaleForm();
        SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
    }

    // ================================================================
    //  BUILD UI
    // ================================================================
    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(14,16,14,16));

        // Header - chỉ tiêu đề, không breadcrumb
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.setBorder(new EmptyBorder(0,0,10,0));
        hdr.add(mkLabel("Bán hàng", F_TITLE, TXT1), BorderLayout.WEST);
        add(hdr, BorderLayout.NORTH);

        // Body: 2 columns via JSplitPane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, initSearchSection(), initRightPanel());
        split.setResizeWeight(0.35);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setOpaque(false);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);

        // F9 shortcut
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F9"), "pay");
        getActionMap().put("pay", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { handleThanhToan(); }
        });
    }

    // ================================================================
    //  LEFT: SEARCH SECTION
    // ================================================================
    private JPanel initSearchSection() {
        JPanel p = mkCard();
        p.setLayout(new BorderLayout(0, 8));
        p.setMinimumSize(new Dimension(360, 0));

        // Top area
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = mkLabel("Danh sách thuốc", F_SEC, TXT1);
        title.setAlignmentX(LEFT_ALIGNMENT);
        top.add(title);
        top.add(Box.createVerticalStrut(8));

        // Search field - larger
        txtSearch = mkField("Tìm theo mã, tên thuốc, hoạt chất...");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        javax.swing.Timer st = new javax.swing.Timer(250, e -> loadProducts(txtSearch.getText().trim()));
        st.setRepeats(false);
        txtSearch.getDocument().addDocumentListener(mkDL(st));
        txtSearch.addActionListener(e -> addFirstProduct());
        top.add(txtSearch);
        top.add(Box.createVerticalStrut(6));

        // Product count + filter chips
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        filterRow.setOpaque(false);
        filterRow.setAlignmentX(LEFT_ALIGNMENT);
        filterRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        lblProductCount = mkLabel("0 sản phẩm khả dụng", F_SM, TXT_M);
        filterRow.add(lblProductCount);
        top.add(filterRow);

        p.add(top, BorderLayout.NORTH);

        // Product table
        mdlProduct = new DefaultTableModel(new String[]{"Mã","Tên thuốc","ĐVT","Giá bán","Tồn","Trạng thái"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblProduct = mkStyledTable(mdlProduct);
        tblProduct.getColumnModel().getColumn(5).setCellRenderer(new StockBadgeRenderer());
        setColWidths(tblProduct, 42, 999, 42, 78, 40, 72);
        // Double-click / Enter to add
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { if (e.getClickCount() == 2) addSelectedProduct(); }
        });
        tblProduct.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "addP");
        tblProduct.getActionMap().put("addP", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { addSelectedProduct(); }
        });

        JScrollPane sp = new JScrollPane(tblProduct, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BRD));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    // ================================================================
    //  RIGHT PANEL – dùng BoxLayout Y để các card xếp dọc cân đối
    // ================================================================
    private JPanel initRightPanel() {
        // Outer container: customer NORTH, cart CENTER, payment+summary SOUTH
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.setMinimumSize(new Dimension(500, 0));

        p.add(initCustomerSection(), BorderLayout.NORTH);
        p.add(initCartSection(), BorderLayout.CENTER);

        // Bottom: Payment + Summary
        JPanel bot = new JPanel();
        bot.setOpaque(false);
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        bot.add(initPaymentSection());
        bot.add(Box.createVerticalStrut(8));
        bot.add(initOrderSummarySection());
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    // ================================================================
    //  CUSTOMER SECTION
    // ================================================================
    private JPanel initCustomerSection() {
        JPanel c = mkCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

        c.add(mkLabel("Thông tin khách hàng", F_SEC, TXT1));
        c.add(Box.createVerticalStrut(8));

        // Radio
        rbRetail = new JRadioButton("Khách lẻ", true);
        rbMember = new JRadioButton("Khách thành viên");
        rbRetail.setFont(F_LBL); rbMember.setFont(F_LBL);
        rbRetail.setOpaque(false); rbMember.setOpaque(false);
        rbRetail.setFocusPainted(false); rbMember.setFocusPainted(false);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbRetail); bg.add(rbMember);
        JPanel rr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rr.setOpaque(false); rr.setAlignmentX(LEFT_ALIGNMENT);
        rr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        rr.add(rbRetail); rr.add(rbMember);
        c.add(rr);
        c.add(Box.createVerticalStrut(6));

        // Search KH
        JPanel spKH = new JPanel(new BorderLayout(6, 0));
        spKH.setOpaque(false); spKH.setAlignmentX(LEFT_ALIGNMENT);
        spKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtTimKH = mkField("Tìm tên / SĐT / mã khách hàng...");
        txtTimKH.setEnabled(false);
        spKH.add(txtTimKH, BorderLayout.CENTER);
        btnXoaKH = new JButton("Đổi KH");
        btnXoaKH.setFont(F_SM_B);
        btnXoaKH.setForeground(DANGER);
        btnXoaKH.setContentAreaFilled(false);
        btnXoaKH.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(DANGER), new EmptyBorder(2, 8, 2, 8)));
        btnXoaKH.setFocusPainted(false);
        btnXoaKH.setVisible(false);
        btnXoaKH.addActionListener(e -> clearKH());
        spKH.add(btnXoaKH, BorderLayout.EAST);
        c.add(spKH);
        c.add(Box.createVerticalStrut(6));
        setupKHAutocomplete();

        // Customer summary row
        JPanel sum = new JPanel(new GridLayout(1, 3, 12, 0));
        sum.setOpaque(false); sum.setAlignmentX(LEFT_ALIGNMENT);
        sum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        lblKHName = mkLabel("Khách lẻ", F_LBL_B, TXT1);
        lblKHPhone = mkLabel("SĐT: ---", F_LBL, TXT2);
        lblKHPoints = mkLabel("Điểm: 0", F_LBL, TXT2);
        sum.add(lblKHName); sum.add(lblKHPhone); sum.add(lblKHPoints);
        c.add(sum);

        rbRetail.addActionListener(e -> updateCustomerModeUI());
        rbMember.addActionListener(e -> updateCustomerModeUI());
        return c;
    }

    // ================================================================
    //  CART SECTION – 3 phần: HEADER / BODY / FOOTER
    // ================================================================
    private JPanel initCartSection() {
        // Outer card – tự tạo, không dùng mkCard() để kiểm soát layout
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createLineBorder(BRD));

        // ── HEADER: tiêu đề + nút ──
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(CARD);
        hdr.setBorder(new EmptyBorder(12, 16, 10, 16));

        lblMaHoaDon = mkLabel("Giỏ hàng", F_SEC, TXT1);
        hdr.add(lblMaHoaDon, BorderLayout.WEST);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnBar.setOpaque(false);
        btnBar.add(mkOutlineBtn("Tạo mới", BLUE, e -> handleTaoMoi()));
        btnBar.add(mkOutlineBtn("Xóa giỏ", DANGER, e -> handleXoaTatCa()));
        hdr.add(btnBar, BorderLayout.EAST);
        card.add(hdr, BorderLayout.NORTH);

        // ── BODY: bảng hoặc empty state ──
        mdlInvoice = new ChiTietHoaDonTableModel();
        tblInvoice = mkStyledTable(mdlInvoice);
        hideCol(tblInvoice, 0);
        hideCol(tblInvoice, 1);
        setColWidths(tblInvoice, 0, 0, 30, 40, 160, 42, 52, 65, 48, 80, 90, 40);

        DefaultTableCellRenderer rightR = new DefaultTableCellRenderer();
        rightR.setHorizontalAlignment(SwingConstants.RIGHT);
        tblInvoice.getColumnModel().getColumn(9).setCellRenderer(rightR);
        tblInvoice.getColumnModel().getColumn(10).setCellRenderer(rightR);
        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(SwingConstants.CENTER);
        tblInvoice.getColumnModel().getColumn(2).setCellRenderer(centerR);
        tblInvoice.getColumnModel().getColumn(8).setCellRenderer(centerR);
        tblInvoice.getColumnModel().getColumn(11).setCellRenderer(centerR);

        // Qty edit listener
        mdlInvoice.addTableModelListener(e -> {
            if (!updatingInvoice && e.getType() == TableModelEvent.UPDATE && e.getColumn() == 8) {
                int row = e.getFirstRow();
                if (row >= 0 && row < mdlInvoice.getRowCount()) updateCartQty(row);
            }
        });

        // Delete key + context menu
        tblInvoice.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DELETE"), "delRow");
        tblInvoice.getActionMap().put("delRow", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { removeCartRow(); }
        });
        JPopupMenu pm = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Xóa sản phẩm khỏi giỏ");
        mi.setForeground(DANGER);
        mi.addActionListener(e -> removeCartRow());
        pm.add(mi);
        tblInvoice.setComponentPopupMenu(pm);

        invoiceScroll = new JScrollPane(tblInvoice,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        invoiceScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BRD));
        invoiceScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Empty state
        emptyState = new JPanel(new GridBagLayout());
        emptyState.setBackground(CARD);
        JPanel emptyContent = new JPanel();
        emptyContent.setOpaque(false);
        emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
        JLabel lblEmptyTitle = mkLabel("Chưa có sản phẩm trong giỏ", F_LBL_B, TXT_M);
        lblEmptyTitle.setAlignmentX(CENTER_ALIGNMENT);
        emptyContent.add(lblEmptyTitle);
        emptyContent.add(Box.createVerticalStrut(6));
        JLabel lblEmptySub = mkLabel("Chọn thuốc từ danh sách bên trái để thêm vào đơn", F_SM, TXT_M);
        lblEmptySub.setAlignmentX(CENTER_ALIGNMENT);
        emptyContent.add(lblEmptySub);
        emptyState.add(emptyContent);

        // CardLayout wrapper – sử dụng panel bọc ngoài để đảm bảo chiều cao
        JPanel cartBody = new JPanel(new CardLayout()) {
            @Override
            public Dimension getPreferredSize() {
                // Đảm bảo luôn có chiều cao tối thiểu
                Dimension d = super.getPreferredSize();
                return new Dimension(d.width, Math.max(d.height, 180));
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, 150);
            }
        };
        cartBody.setBackground(CARD);
        cartBody.add(emptyState, "empty");
        cartBody.add(invoiceScroll, "table");
        card.add(cartBody, BorderLayout.CENTER);

        // ── FOOTER: tổng tiền ──
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 249, 245));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(187, 247, 208)),
            new EmptyBorder(12, 16, 12, 16)
        ));

        lblTongTien = new JLabel("0 \u20AB");
        lblTongTien.setFont(F_TOTAL);
        lblTongTien.setForeground(ACCENT);

        footer.add(mkLabel("TỔNG TIỀN:", new Font("Segoe UI", Font.BOLD, 16), ACCENT), BorderLayout.WEST);
        footer.add(lblTongTien, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    // ================================================================
    //  PAYMENT SECTION
    // ================================================================
    private JPanel initPaymentSection() {
        JPanel c = mkCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setAlignmentX(LEFT_ALIGNMENT);

        c.add(mkLabel("Thanh toán", F_SEC, TXT1));
        c.add(Box.createVerticalStrut(8));

        // Radio
        rbCash = new JRadioButton("Tiền mặt", true);
        rbTransfer = new JRadioButton("Chuyển khoản");
        rbCash.setFont(F_LBL); rbTransfer.setFont(F_LBL);
        rbCash.setOpaque(false); rbTransfer.setOpaque(false);
        rbCash.setFocusPainted(false); rbTransfer.setFocusPainted(false);
        ButtonGroup bgP = new ButtonGroup(); bgP.add(rbCash); bgP.add(rbTransfer);
        JPanel pr = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        pr.setOpaque(false); pr.setAlignmentX(LEFT_ALIGNMENT);
        pr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        pr.add(rbCash); pr.add(rbTransfer);
        c.add(pr);
        c.add(Box.createVerticalStrut(8));

        // Card layout for cash/transfer
        JPanel pc = new JPanel();
        payCardLayout = new CardLayout();
        pc.setLayout(payCardLayout);
        pc.setOpaque(false);
        pc.setAlignmentX(LEFT_ALIGNMENT);

        // ── Cash panel ──
        JPanel cash = new JPanel();
        cash.setOpaque(false);
        cash.setLayout(new BoxLayout(cash, BoxLayout.Y_AXIS));
        JLabel lk = mkLabel("Tiền khách đưa:", F_LBL_B, TXT1);
        lk.setAlignmentX(LEFT_ALIGNMENT);
        cash.add(lk);
        cash.add(Box.createVerticalStrut(4));
        txtTienKhach = mkField("Nhập số tiền...");
        txtTienKhach.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtTienKhach.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calcChange(); }
            public void removeUpdate(DocumentEvent e) { calcChange(); }
            public void changedUpdate(DocumentEvent e) { calcChange(); }
        });
        cash.add(txtTienKhach);
        cash.add(Box.createVerticalStrut(6));

        // Quick amount buttons
        JPanel qb = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        qb.setOpaque(false); qb.setAlignmentX(LEFT_ALIGNMENT);
        qb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        for (String[] q : new String[][]{{"+10K","10000"},{"+20K","20000"},{"+50K","50000"},{"+100K","100000"}}) {
            JButton b = mkSmallBtn(q[0], BLUE);
            b.addActionListener(ev -> addAmt(Integer.parseInt(q[1])));
            qb.add(b);
        }
        JButton bf = mkSmallBtn("Đủ tiền", GREEN);
        bf.addActionListener(ev -> txtTienKhach.setText(String.valueOf(total().intValue())));
        qb.add(bf);
        cash.add(qb);
        cash.add(Box.createVerticalStrut(6));

        lblTienThua = new JLabel("0 \u20AB");
        cash.add(mkSumRow("Tiền thừa:", lblTienThua, F_MONEY, GREEN));

        // ── Transfer panel ──
        JPanel trans = new JPanel();
        trans.setOpaque(false);
        trans.setLayout(new BoxLayout(trans, BoxLayout.Y_AXIS));
        trans.add(mkLabel("Khách thanh toán bằng chuyển khoản", F_LBL, BLUE));
        trans.add(Box.createVerticalStrut(6));
        JLabel lr = mkLabel("Mã tham chiếu / nội dung CK:", F_LBL_B, TXT1);
        lr.setAlignmentX(LEFT_ALIGNMENT);
        trans.add(lr);
        trans.add(Box.createVerticalStrut(4));
        txtTransferRef = mkField("Nội dung CK...");
        trans.add(txtTransferRef);
        trans.add(Box.createVerticalStrut(8));
        chkTransferConfirm = new JCheckBox("Đã xác nhận thanh toán");
        chkTransferConfirm.setFont(F_LBL_B);
        chkTransferConfirm.setForeground(GREEN);
        chkTransferConfirm.setOpaque(false);
        chkTransferConfirm.setFocusPainted(false);
        chkTransferConfirm.setAlignmentX(LEFT_ALIGNMENT);
        trans.add(chkTransferConfirm);

        pc.add(cash, "cash");
        pc.add(trans, "transfer");
        c.add(pc);

        rbCash.addActionListener(e -> updatePaymentMethodUI());
        rbTransfer.addActionListener(e -> updatePaymentMethodUI());
        return c;
    }

    // ================================================================
    //  ORDER SUMMARY SECTION
    // ================================================================
    private JPanel initOrderSummarySection() {
        JPanel c = mkCard();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setAlignmentX(LEFT_ALIGNMENT);

        c.add(mkLabel("Tổng kết đơn hàng", F_SEC, TXT1));
        c.add(Box.createVerticalStrut(6));

        lblSubtotal = new JLabel("0 \u20AB");
        c.add(mkSumRow("Tạm tính:", lblSubtotal, F_LBL, TXT1));
        c.add(Box.createVerticalStrut(3));
        lblItems = new JLabel("0");
        c.add(mkSumRow("Số mặt hàng:", lblItems, F_LBL, TXT1));
        c.add(Box.createVerticalStrut(3));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(BRD);
        c.add(sep);
        c.add(Box.createVerticalStrut(4));

        lblSummaryTotal = new JLabel("0 \u20AB");
        c.add(mkSumRow("Tổng thanh toán:", lblSummaryTotal, F_MONEY, ACCENT));
        c.add(Box.createVerticalStrut(10));

        // CTA Button
        JButton bp = new JButton("THANH TOÁN  (F9)");
        bp.setFont(F_PAY);
        bp.setBackground(ACCENT);
        bp.setForeground(Color.WHITE);
        bp.setFocusPainted(false);
        bp.setBorderPainted(false);
        bp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        bp.setAlignmentX(LEFT_ALIGNMENT);
        bp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bp.putClientProperty("JButton.buttonType", "roundRect");
        bp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { bp.setBackground(ACCENT.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e) { bp.setBackground(ACCENT); }
        });
        bp.addActionListener(e -> handleThanhToan());
        c.add(bp);
        c.add(Box.createVerticalStrut(8));

        // Secondary buttons
        JPanel sb = new JPanel(new GridLayout(1, 2, 6, 0));
        sb.setOpaque(false);
        sb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sb.setAlignmentX(LEFT_ALIGNMENT);
        sb.add(mkOutlineBtn("Làm mới đơn", BLUE, e -> handleTaoMoi()));
        sb.add(mkOutlineBtn("Hủy hóa đơn", DANGER, e -> handleHuy()));
        c.add(sb);
        return c;
    }

    // ================================================================
    //  KH AUTOCOMPLETE
    // ================================================================
    private void setupKHAutocomplete() {
        khModel = new DefaultListModel<>();
        khList = new JList<>(khModel);
        khList.setFont(F_INPUT);
        khList.setFixedCellHeight(34);
        khList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        popupKH = new JPopupMenu();
        popupKH.setBorder(BorderFactory.createLineBorder(BRD));
        JScrollPane sp = new JScrollPane(khList);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(280, 170));
        popupKH.add(sp);
        khList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && khList.getSelectedIndex() >= 0) {
                int idx = khList.getSelectedIndex();
                if (khCache != null && idx < khCache.size()) {
                    selectKH(khCache.get(idx));
                    popupKH.setVisible(false);
                }
            }
        });
        searchKHTimer = new javax.swing.Timer(300, e -> doSearchKH());
        searchKHTimer.setRepeats(false);
        txtTimKH.getDocument().addDocumentListener(mkDL(searchKHTimer));
    }

    private void doSearchKH() {
        String kw = txtTimKH.getText().trim();
        if (kw.length() < 1) { popupKH.setVisible(false); return; }
        khCache = khDao.searchByNameOrPhone(kw);
        khModel.clear();
        if (khCache.isEmpty()) khModel.addElement("Không tìm thấy \"" + kw + "\"");
        else for (var kh : khCache)
            khModel.addElement(kh.getHoTen() + " | " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "--") + " (Mã:" + kh.getMaKhachHang() + ")");
        if (!popupKH.isVisible()) popupKH.show(txtTimKH, 0, txtTimKH.getHeight());
        popupKH.setPopupSize(txtTimKH.getWidth(), Math.min(khCache.size() * 36 + 10, 200));
    }

    private void selectKH(KhachHang kh) {
        selectedMaKH = kh.getMaKhachHang();
        lblKHName.setText(kh.getHoTen());
        lblKHName.setForeground(ACCENT);
        lblKHPhone.setText("SĐT: " + (kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "---"));
        lblKHPoints.setText("Điểm: 0");
        btnXoaKH.setVisible(true);
        txtTimKH.setText("");
        txtTimKH.setEnabled(false);
    }

    private void clearKH() {
        selectedMaKH = null;
        lblKHName.setText("Khách lẻ");
        lblKHName.setForeground(TXT1);
        lblKHPhone.setText("SĐT: ---");
        lblKHPoints.setText("Điểm: 0");
        btnXoaKH.setVisible(false);
        if (customerMode == CustomerMode.MEMBER) { txtTimKH.setEnabled(true); txtTimKH.setText(""); }
    }

    private void updateCustomerModeUI() {
        customerMode = rbRetail.isSelected() ? CustomerMode.RETAIL : CustomerMode.MEMBER;
        if (customerMode == CustomerMode.RETAIL) { clearKH(); txtTimKH.setEnabled(false); txtTimKH.setText(""); }
        else { txtTimKH.setEnabled(true); txtTimKH.requestFocusInWindow(); }
    }

    private void updatePaymentMethodUI() {
        paymentMethod = rbCash.isSelected() ? PaymentMethod.CASH : PaymentMethod.TRANSFER;
        payCardLayout.show(((JPanel) txtTienKhach.getParent()).getParent(), paymentMethod == PaymentMethod.CASH ? "cash" : "transfer");
    }

    private void addAmt(int a) {
        String c = txtTienKhach.getText().trim().replaceAll("[^\\d]", "");
        long v = c.isEmpty() ? 0 : Long.parseLong(c);
        txtTienKhach.setText(String.valueOf(v + a));
    }

    // ================================================================
    //  PRODUCTS
    // ================================================================
    private void loadProducts(String kw) {
        SwingUtilities.invokeLater(() -> {
            productCache = banHangService.searchProducts(kw);
            mdlProduct.setRowCount(0);
            for (SanPham sp : productCache) {
                String st = banHangService.getStockStatus(sp);
                mdlProduct.addRow(new Object[]{sp.getMaSanPham(), sp.getTenSanPham(), sp.getDonViTinh(),
                    VND.format(sp.getGiaBanDeXuat() != null ? sp.getGiaBanDeXuat() : BigDecimal.ZERO), sp.getTongTon(), st});
            }
            lblProductCount.setText(productCache.size() + " sản phẩm khả dụng");
        });
    }

    private void addFirstProduct() {
        if (mdlProduct.getRowCount() > 0) { tblProduct.setRowSelectionInterval(0, 0); addSelectedProduct(); }
    }

    private void addSelectedProduct() {
        int row = tblProduct.getSelectedRow(); if (row < 0) return;
        int maSP = (int) mdlProduct.getValueAt(row, 0);
        String ten = (String) mdlProduct.getValueAt(row, 1);
        int ton = (int) mdlProduct.getValueAt(row, 4);
        String st = (String) mdlProduct.getValueAt(row, 5);
        if ("Hết hàng".equals(st) || ton <= 0) {
            JOptionPane.showMessageDialog(this, "\"" + ten + "\" đã hết hàng!", "Không thể thêm", JOptionPane.WARNING_MESSAGE); return;
        }
        if (currentMaHoaDon == null) {
            var result = banHangService.createInvoice(currentUser.getMaNguoiDung(), selectedMaKH);
            if (result.isFail()) { JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
            currentMaHoaDon = result.getData();
            lblMaHoaDon.setText("Giỏ hàng  #HD" + String.format("%04d", currentMaHoaDon));
        }
        var addResult = banHangService.addProductToCart(currentMaHoaDon, maSP, 1);
        if (addResult.isSuccess()) {
            refreshCartTable(); loadProducts(txtSearch.getText().trim()); txtSearch.requestFocusInWindow();
        } else JOptionPane.showMessageDialog(this, addResult.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    // ================================================================
    //  INVOICE / CART
    // ================================================================
    private void refreshCartTable() {
        if (currentMaHoaDon == null) return;
        updatingInvoice = true;
        mdlInvoice.setRowCount(0);
        for (var r : banHangService.getCartItems(currentMaHoaDon)) mdlInvoice.addRow(r);
        updatingInvoice = false;
        updateTotals();
        updateCartVisibility();
    }

    private BigDecimal total() {
        BigDecimal t = BigDecimal.ZERO;
        for (int i = 0; i < mdlInvoice.getRowCount(); i++) {
            BigDecimal v = (BigDecimal) mdlInvoice.getValueAt(i, 10);
            if (v != null) t = t.add(v);
        }
        return t;
    }

    private void updateTotals() {
        BigDecimal t = total();
        lblTongTien.setText(VND.format(t));
        lblSubtotal.setText(VND.format(t));
        lblSummaryTotal.setText(VND.format(t));
        lblItems.setText(String.valueOf(mdlInvoice.getRowCount()));
        calcChange();
    }

    private void calcChange() {
        if (txtTienKhach == null || lblTienThua == null) return;
        try {
            BigDecimal t = total();
            String s = txtTienKhach.getText().trim().replaceAll("[^\\d]", "");
            if (!s.isEmpty()) {
                BigDecimal tk = new BigDecimal(s);
                BigDecimal th = tk.subtract(t);
                lblTienThua.setText(VND.format(th.max(BigDecimal.ZERO)));
                lblTienThua.setForeground(th.signum() >= 0 ? GREEN : DANGER);
            } else {
                lblTienThua.setText("0 \u20AB");
                lblTienThua.setForeground(TXT_M);
            }
        } catch (Exception ex) {
            lblTienThua.setText("\u2013");
            lblTienThua.setForeground(TXT_M);
        }
    }

    private void updateCartVisibility() {
        JPanel a = (JPanel) invoiceScroll.getParent();
        ((CardLayout) a.getLayout()).show(a, mdlInvoice.getRowCount() == 0 ? "empty" : "table");
    }

    // ================================================================
    //  CART ROW OPS
    // ================================================================
    private void updateCartQty(int row) {
        try {
            int maCTHD = (int) mdlInvoice.getValueAt(row, 0);
            Object val = mdlInvoice.getValueAt(row, 8);
            int sl = val instanceof Integer ? (Integer) val : Integer.parseInt(val.toString().trim());
            var result = banHangService.updateCartItemQuantity(maCTHD, sl);
            if (result.isFail())
                JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            refreshCartTable(); loadProducts(txtSearch.getText().trim());
        } catch (Exception e) { refreshCartTable(); }
    }

    private void removeCartRow() {
        int row = tblInvoice.getSelectedRow(); if (row < 0) return;
        int id = (int) mdlInvoice.getValueAt(row, 0);
        String ten = (String) mdlInvoice.getValueAt(row, 4);
        int sl = (int) mdlInvoice.getValueAt(row, 8);
        if (JOptionPane.showConfirmDialog(this, "Xóa: " + ten + " (SL: " + sl + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            var result = banHangService.removeCartItem(id);
            if (result.isSuccess()) { refreshCartTable(); loadProducts(txtSearch.getText().trim()); }
        }
    }

    // ================================================================
    //  HANDLERS
    // ================================================================
    private void handleTaoMoi() {
        if (currentMaHoaDon != null && mdlInvoice.getRowCount() > 0)
            if (JOptionPane.showConfirmDialog(this, "Hóa đơn hiện tại sẽ bị hủy?", "Tạo mới", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        resetSaleForm();
    }

    private void handleXoaTatCa() {
        if (mdlInvoice.getRowCount() == 0 || currentMaHoaDon == null) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa tất cả sản phẩm trong giỏ?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            banHangService.clearCart(currentMaHoaDon);
            refreshCartTable(); loadProducts(txtSearch.getText().trim());
        }
    }

    private void handleThanhToan() {
        // Validate giỏ hàng
        var cartCheck = banHangService.validateCartForCheckout(currentMaHoaDon, mdlInvoice.getRowCount());
        if (cartCheck.isFail()) {
            JOptionPane.showMessageDialog(this, cartCheck.getMessage(), "Thông báo", JOptionPane.WARNING_MESSAGE); return;
        }
        BigDecimal t = total();
        // Validate thanh toán
        if (paymentMethod == PaymentMethod.CASH) {
            var cashCheck = banHangService.validateCashPayment(t, txtTienKhach.getText());
            if (cashCheck.isFail()) {
                JOptionPane.showMessageDialog(this, cashCheck.getMessage(), "Thiếu tiền", JOptionPane.WARNING_MESSAGE);
                txtTienKhach.requestFocus(); return;
            }
        } else {
            var transferCheck = banHangService.validateTransferPayment(chkTransferConfirm.isSelected());
            if (transferCheck.isFail()) {
                JOptionPane.showMessageDialog(this, transferCheck.getMessage(), "Chưa xác nhận", JOptionPane.WARNING_MESSAGE); return;
            }
        }
        String pt = paymentMethod == PaymentMethod.CASH ? "Tiền mặt" : "Chuyển khoản";
        if (JOptionPane.showConfirmDialog(this, "Thanh toán #HD" + String.format("%04d", currentMaHoaDon) + "?\nTổng: " + VND.format(t) + "\nPTTT: " + pt, "Thanh toán", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            var result = banHangService.checkout(currentMaHoaDon, t, pt);
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            resetSaleForm();
        }
    }

    private void handleHuy() {
        if (currentMaHoaDon == null) return;
        if (JOptionPane.showConfirmDialog(this, "Hủy hóa đơn #HD" + String.format("%04d", currentMaHoaDon) + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            banHangService.clearCart(currentMaHoaDon);
            resetSaleForm();
        }
    }

    private void resetSaleForm() {
        currentMaHoaDon = null; selectedMaKH = null;
        lblMaHoaDon.setText("Giỏ hàng");
        rbRetail.setSelected(true); updateCustomerModeUI();
        rbCash.setSelected(true); updatePaymentMethodUI();
        txtTienKhach.setText("");
        if (txtTransferRef != null) txtTransferRef.setText("");
        if (chkTransferConfirm != null) chkTransferConfirm.setSelected(false);
        if (lblTienThua != null) lblTienThua.setText("0 \u20AB");
        updatingInvoice = true; mdlInvoice.setRowCount(0); updatingInvoice = false;
        updateTotals(); updateCartVisibility(); loadProducts(null);
        SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
    }

    // ================================================================
    //  FACTORY HELPERS
    // ================================================================
    private JLabel mkLabel(String t, Font f, Color c) { JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); return l; }

    private JPanel mkCard() {
        JPanel p = new JPanel(); p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BRD), new EmptyBorder(14, 16, 14, 16)));
        return p;
    }

    private JTextField mkField(String ph) {
        JTextField f = new JTextField(); f.setFont(F_INPUT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); f.setAlignmentX(LEFT_ALIGNMENT);
        f.putClientProperty("JTextField.placeholderText", ph);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BRD), new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JPanel mkSumRow(String l, JLabel v, Font vf, Color vc) {
        JPanel r = new JPanel(new BorderLayout()); r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26)); r.setAlignmentX(LEFT_ALIGNMENT);
        r.add(mkLabel(l, F_LBL, TXT2), BorderLayout.WEST);
        v.setFont(vf); v.setForeground(vc); r.add(v, BorderLayout.EAST);
        return r;
    }

    private JButton mkOutlineBtn(String t, Color c, java.awt.event.ActionListener a) {
        JButton b = new JButton(t); b.setFont(F_BTN);
        b.setForeground(c); b.setBackground(CARD);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(c), new EmptyBorder(5, 12, 5, 12)));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(a);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 20)); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setBackground(CARD); }
        });
        return b;
    }

    private JButton mkSmallBtn(String t, Color fg) {
        JButton b = new JButton(t); b.setFont(F_SM_B);
        b.setForeground(fg); b.setBackground(CARD);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BRD), new EmptyBorder(3, 7, 3, 7)));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void hideCol(JTable t, int i) { var c = t.getColumnModel().getColumn(i); c.setMinWidth(0); c.setMaxWidth(0); c.setPreferredWidth(0); }

    private void setColWidths(JTable t, int... w) { var cm = t.getColumnModel(); for (int i = 0; i < w.length && i < cm.getColumnCount(); i++) cm.getColumn(i).setPreferredWidth(w[i]); }

    private DocumentListener mkDL(javax.swing.Timer timer) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { timer.restart(); }
            public void removeUpdate(DocumentEvent e) { timer.restart(); }
            public void changedUpdate(DocumentEvent e) { timer.restart(); }
        };
    }

    private JTable mkStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(34); t.setFont(F_TABLE); t.setShowGrid(false);
        t.setShowHorizontalLines(true); t.setGridColor(DIV);
        t.setSelectionBackground(SEL_BG); t.setSelectionForeground(TXT1);
        t.setIntercellSpacing(new Dimension(0, 0)); t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        JTableHeader h = t.getTableHeader(); h.setFont(F_TH); h.setBackground(HDR_BG);
        h.setForeground(TXT2); h.setBorder(new MatteBorder(0, 0, 2, 0, BRD));
        h.setPreferredSize(new Dimension(0, 36));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private int hRow = -1; private boolean attached;
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                if (!attached) {
                    attached = true;
                    tb.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                        public void mouseMoved(java.awt.event.MouseEvent e) { int nr = tb.rowAtPoint(e.getPoint()); if (nr != hRow) { hRow = nr; tb.repaint(); } }
                    });
                    tb.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseExited(java.awt.event.MouseEvent e) { hRow = -1; tb.repaint(); }
                    });
                }
                Component cp = super.getTableCellRendererComponent(tb, v, s, f, r, c);
                if (!s) cp.setBackground(r == hRow ? HOVER_BG : (r % 2 == 0 ? CARD : ALT_BG));
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return cp;
            }
        });
        return t;
    }

    // ================================================================
    //  STOCK BADGE RENDERER (Soft badges)
    // ================================================================
    private static class StockBadgeRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String txt = v != null ? v.toString() : "";
            l.setText(txt); l.setHorizontalAlignment(CENTER);
            l.setFont(F_BADGE); l.setOpaque(true);
            l.setBorder(new EmptyBorder(4, 6, 4, 6));
            if (!sel) {
                switch (txt) {
                    case "Còn hàng" -> { l.setBackground(new Color(220,252,231)); l.setForeground(new Color(21,128,61)); }
                    case "Sắp hết"  -> { l.setBackground(new Color(255,237,213)); l.setForeground(new Color(194,120,3)); }
                    case "Hết hàng" -> { l.setBackground(new Color(254,226,226)); l.setForeground(new Color(185,28,28)); }
                    default         -> { l.setBackground(new Color(243,244,246)); l.setForeground(new Color(75,85,99)); }
                }
            }
            return l;
        }
    }
}
