package panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import components.KhachHangTableModel;
import dao.KhachHangDao;
import entity.KhachHang;
import entity.NguoiDung;

/**
 * KhachHangPanel v5.0 - GridBagLayout form, ti\u1EBFng Vi\u1EC7t c\u00F3 d\u1EA5u
 */
public class KhachHangPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Palette
    private static final Color BG_PAGE    = new Color(0xF5, 0xF8, 0xFB);
    private static final Color BG_CARD    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xE3, 0xEA, 0xF2);
    private static final Color TXT_PRI    = new Color(0x1F, 0x2D, 0x3D);
    private static final Color TXT_SEC    = new Color(0x6B, 0x7A, 0x8C);
    private static final Color GREEN      = new Color(0x2F, 0xA3, 0x6B);
    private static final Color RED        = new Color(0xE5, 0x48, 0x4D);
    private static final Color GRAY       = new Color(0xC9, 0xCE, 0xD6);
    private static final Color INPUT_DIS  = new Color(0xF0, 0xF2, 0xF5);
    private static final Color TBL_HDR    = new Color(0xF8, 0xF9, 0xFA);
    private static final Color TBL_SEL    = new Color(0xE8, 0xF5, 0xEE);

    // Fonts
    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font F_SEC   = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BTN   = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_TBL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_TBL_H = new Font("Segoe UI", Font.BOLD, 13);

    // Sizes
    private static final int FIELD_H    = 36;
    private static final int BTN_H      = 40;
    private static final int TEXTAREA_H = 100;

    private KhachHangDao dao;
    private KhachHangTableModel tableModel;
    private JTable table;
    private JTextField txtMaKH, txtHoTen, txtSoDienThoai, txtEmail, txtDiaChi;
    private JTextArea txtHoSoBenhAn;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    public KhachHangPanel(NguoiDung currentUser) {
        dao = new KhachHangDao();
        buildUI();
        loadData();
    }

    // ================================================================
    //  MAIN LAYOUT
    // ================================================================

    private void buildUI() {
        setLayout(new BorderLayout());
        setBackground(BG_PAGE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        var titleLbl = new JLabel("Qu\u1EA3n l\u00FD kh\u00E1ch h\u00E0ng");
        titleLbl.setFont(F_TITLE);
        titleLbl.setForeground(TXT_PRI);
        titleLbl.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(titleLbl, BorderLayout.NORTH);

        // Main split
        var main = new JPanel(new BorderLayout(16, 0));
        main.setOpaque(false);
        main.add(buildFormCard(), BorderLayout.WEST);
        main.add(buildTableCard(), BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);
    }

    // ================================================================
    //  FORM CARD (GridBagLayout)
    // ================================================================

    private JPanel buildFormCard() {
        var card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(18, 18, 18, 18)
        ));
        card.setPreferredSize(new Dimension(370, 0));

        // Section title
        var secLabel = new JLabel("Th\u00F4ng tin kh\u00E1ch h\u00E0ng");
        secLabel.setFont(F_SEC);
        secLabel.setForeground(TXT_PRI);
        secLabel.setBorder(new EmptyBorder(0, 0, 14, 0));
        card.add(secLabel, BorderLayout.NORTH);

        // Form fields
        card.add(buildFormFields(), BorderLayout.CENTER);

        // Buttons
        card.add(buildButtonGrid(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildFormFields() {
        var form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);
        var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        // M\u00E3 KH
        txtMaKH = makeField(FIELD_H);
        txtMaKH.setEditable(false);
        txtMaKH.setBackground(INPUT_DIS);
        addRow(form, gbc, row++, "M\u00E3 KH:", txtMaKH, FIELD_H);

        // H\u1ECD t\u00EAn
        txtHoTen = makeField(FIELD_H);
        addRow(form, gbc, row++, "H\u1ECD t\u00EAn: *", txtHoTen, FIELD_H);

        // S\u1ED1 \u0111i\u1EC7n tho\u1EA1i
        txtSoDienThoai = makeField(FIELD_H);
        addRow(form, gbc, row++, "S\u1ED1 \u0111i\u1EC7n tho\u1EA1i:", txtSoDienThoai, FIELD_H);

        // Email
        txtEmail = makeField(FIELD_H);
        addRow(form, gbc, row++, "Email:", txtEmail, FIELD_H);

        // \u0110\u1ECBa ch\u1EC9
        txtDiaChi = makeField(FIELD_H);
        addRow(form, gbc, row++, "\u0110\u1ECBa ch\u1EC9:", txtDiaChi, FIELD_H);

        // H\u1ED3 s\u01A1 b\u1EC7nh \u00E1n (textarea)
        txtHoSoBenhAn = new JTextArea();
        txtHoSoBenhAn.setFont(F_INPUT);
        txtHoSoBenhAn.setLineWrap(true);
        txtHoSoBenhAn.setWrapStyleWord(true);
        var scrollTA = new JScrollPane(txtHoSoBenhAn);
        scrollTA.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        addRow(form, gbc, row++, "H\u1ED3 s\u01A1 b\u1EC7nh \u00E1n:", scrollTA, TEXTAREA_H);

        // Spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        form.add(Box.createVerticalGlue(), gbc);

        return form;
    }

    /** Add label + component row to GridBagLayout form */
    private void addRow(JPanel form, GridBagConstraints gbc, int row, String labelText, JComponent comp, int compH) {
        gbc.gridy = row;
        gbc.weighty = 0;
        gbc.insets = new Insets(row == 0 ? 0 : 5, 0, 5, 10);

        // Label
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        var lbl = new JLabel(labelText);
        lbl.setFont(F_LABEL);
        lbl.setForeground(TXT_SEC);
        lbl.setPreferredSize(new Dimension(110, FIELD_H));
        form.add(lbl, gbc);

        // Field
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        comp.setPreferredSize(new Dimension(200, compH));
        comp.setMinimumSize(new Dimension(100, compH));
        form.add(comp, gbc);
    }

    private JPanel buildButtonGrid() {
        var bp = new JPanel(new GridLayout(2, 2, 10, 10));
        bp.setBackground(BG_CARD);
        bp.setBorder(new EmptyBorder(16, 0, 0, 0));
        bp.setPreferredSize(new Dimension(0, BTN_H * 2 + 10));

        btnThem   = makeBtn("Th\u00EAm m\u1EDBi", GREEN, Color.WHITE);
        btnSua    = makeBtn("C\u1EADp nh\u1EADt", GREEN, Color.WHITE);
        btnXoa    = makeBtn("X\u00F3a", RED, Color.WHITE);
        btnLamMoi = makeBtn("L\u00E0m m\u1EDBi", GRAY, TXT_PRI);

        btnThem.addActionListener(e -> handleThem());
        btnSua.addActionListener(e -> handleSua());
        btnXoa.addActionListener(e -> handleXoa());
        btnLamMoi.addActionListener(e -> handleLamMoi());

        bp.add(btnThem);
        bp.add(btnSua);
        bp.add(btnXoa);
        bp.add(btnLamMoi);

        return bp;
    }

    // ================================================================
    //  TABLE CARD
    // ================================================================

    private JPanel buildTableCard() {
        var card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(18, 18, 18, 18)
        ));

        var sec = new JLabel("Danh s\u00E1ch kh\u00E1ch h\u00E0ng");
        sec.setFont(F_SEC);
        sec.setForeground(TXT_PRI);
        card.add(sec, BorderLayout.NORTH);

        tableModel = new KhachHangTableModel();
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) handleTableSelection();
        });

        var sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        sp.getViewport().setBackground(BG_CARD);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }

    private void styleTable(JTable t) {
        t.setFont(F_TBL); t.setRowHeight(34);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
        t.setGridColor(BORDER_CLR);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(TBL_SEL);
        t.setSelectionForeground(TXT_PRI);

        JTableHeader h = t.getTableHeader();
        h.setFont(F_TBL_H); h.setBackground(TBL_HDR);
        h.setForeground(TXT_SEC);
        h.setPreferredSize(new Dimension(0, 40));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_CLR));

        var ctr = new DefaultTableCellRenderer();
        ctr.setHorizontalAlignment(SwingConstants.CENTER);
        if (t.getColumnCount() > 0) {
            t.getColumnModel().getColumn(0).setCellRenderer(ctr);
            t.getColumnModel().getColumn(0).setPreferredWidth(60);
        }
    }

    // ================================================================
    //  COMPONENT HELPERS
    // ================================================================

    private JTextField makeField(int h) {
        var tf = new JTextField();
        tf.setFont(F_INPUT);
        tf.setBackground(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setPreferredSize(new Dimension(200, h));
        return tf;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        var b = new JButton(text);
        b.setFont(F_BTN); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("JButton.buttonType", "roundRect");
        Color hov = bg.darker();
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { if (b.isEnabled()) b.setBackground(hov); }
            public void mouseExited(java.awt.event.MouseEvent e)  { if (b.isEnabled()) b.setBackground(bg); }
        });
        return b;
    }

    // ================================================================
    //  DATA + CRUD
    // ================================================================

    private void loadData() {
        tableModel.setRowCount(0);
        for (var kh : dao.getAll()) {
            tableModel.addRow(new Object[]{
                kh.getMaKhachHang(), kh.getHoTen(),
                kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "",
                kh.getEmail() != null ? kh.getEmail() : "",
                kh.getDiaChi() != null ? kh.getDiaChi() : ""
            });
        }
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            var kh = dao.findById((Integer) tableModel.getValueAt(row, 0));
            if (kh != null) fillForm(kh);
        }
    }

    private void fillForm(KhachHang kh) {
        txtMaKH.setText(String.valueOf(kh.getMaKhachHang()));
        txtHoTen.setText(kh.getHoTen());
        txtSoDienThoai.setText(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
        txtEmail.setText(kh.getEmail() != null ? kh.getEmail() : "");
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
        txtHoSoBenhAn.setText(kh.getHoSoBenhAn() != null ? kh.getHoSoBenhAn() : "");
    }

    private void handleThem() {
        if (!validateForm()) return;
        var kh = new KhachHang();
        kh.setHoTen(txtHoTen.getText().trim());
        kh.setSoDienThoai(txtSoDienThoai.getText().trim().isEmpty() ? null : txtSoDienThoai.getText().trim());
        kh.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        kh.setDiaChi(txtDiaChi.getText().trim().isEmpty() ? null : txtDiaChi.getText().trim());
        kh.setHoSoBenhAn(txtHoSoBenhAn.getText().trim().isEmpty() ? null : txtHoSoBenhAn.getText().trim());
        if (dao.insert(kh)) {
            JOptionPane.showMessageDialog(this, "Th\u00EAm kh\u00E1ch h\u00E0ng th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
            handleLamMoi(); loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Th\u00EAm kh\u00E1ch h\u00E0ng th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSua() {
        if (txtMaKH.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn kh\u00E1ch h\u00E0ng c\u1EA7n s\u1EEDa!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        var kh = new KhachHang();
        kh.setMaKhachHang(Integer.parseInt(txtMaKH.getText()));
        kh.setHoTen(txtHoTen.getText().trim());
        kh.setSoDienThoai(txtSoDienThoai.getText().trim().isEmpty() ? null : txtSoDienThoai.getText().trim());
        kh.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        kh.setDiaChi(txtDiaChi.getText().trim().isEmpty() ? null : txtDiaChi.getText().trim());
        kh.setHoSoBenhAn(txtHoSoBenhAn.getText().trim().isEmpty() ? null : txtHoSoBenhAn.getText().trim());
        if (dao.update(kh)) {
            JOptionPane.showMessageDialog(this, "C\u1EADp nh\u1EADt th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
            handleLamMoi(); loadData();
        } else {
            JOptionPane.showMessageDialog(this, "C\u1EADp nh\u1EADt th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXoa() {
        if (txtMaKH.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn kh\u00E1ch h\u00E0ng c\u1EA7n x\u00F3a!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "X\u00E1c nh\u1EADn x\u00F3a kh\u00E1ch h\u00E0ng n\u00E0y?", "X\u00E1c nh\u1EADn", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(txtMaKH.getText()))) {
                JOptionPane.showMessageDialog(this, "X\u00F3a th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
                handleLamMoi(); loadData();
            } else {
                JOptionPane.showMessageDialog(this, "X\u00F3a th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLamMoi() {
        txtMaKH.setText(""); txtHoTen.setText(""); txtSoDienThoai.setText("");
        txtEmail.setText(""); txtDiaChi.setText(""); txtHoSoBenhAn.setText("");
        table.clearSelection();
    }

    private boolean validateForm() {
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng nh\u1EADp h\u1ECD t\u00EAn!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            txtHoTen.requestFocus();
            return false;
        }
        return true;
    }
}
