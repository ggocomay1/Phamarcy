package panels;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import components.NhaCungCapTableModel;
import dao.NhaCungCapDao;
import entity.NguoiDung;
import entity.NhaCungCap;

/**
 * NhaCungCapPanel v5.0 - GridBagLayout form, \u0111\u1ED3ng b\u1ED9 KhachHangPanel
 */
public class NhaCungCapPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Palette (gi\u1ED1ng KhachHangPanel)
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
    private static final int FIELD_H = 36;
    private static final int BTN_H   = 40;

    private NhaCungCapDao dao;
    private NhaCungCapTableModel tableModel;
    private JTable table;
    private JTextField txtMaNCC, txtTenNCC, txtSoDienThoai, txtEmail, txtDiaChi;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    public NhaCungCapPanel(NguoiDung currentUser) {
        dao = new NhaCungCapDao();
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

        var titleLbl = new JLabel("Qu\u1EA3n l\u00FD nh\u00E0 cung c\u1EA5p");
        titleLbl.setFont(F_TITLE);
        titleLbl.setForeground(TXT_PRI);
        titleLbl.setBorder(new EmptyBorder(0, 0, 14, 0));
        add(titleLbl, BorderLayout.NORTH);

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

        var secLabel = new JLabel("Th\u00F4ng tin nh\u00E0 cung c\u1EA5p");
        secLabel.setFont(F_SEC);
        secLabel.setForeground(TXT_PRI);
        secLabel.setBorder(new EmptyBorder(0, 0, 14, 0));
        card.add(secLabel, BorderLayout.NORTH);

        card.add(buildFormFields(), BorderLayout.CENTER);
        card.add(buildButtonGrid(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildFormFields() {
        var form = new JPanel(new GridBagLayout());
        form.setBackground(BG_CARD);
        var gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        int row = 0;

        txtMaNCC = makeField(FIELD_H);
        txtMaNCC.setEditable(false);
        txtMaNCC.setBackground(INPUT_DIS);
        addRow(form, gbc, row++, "M\u00E3 NCC:", txtMaNCC, FIELD_H);

        txtTenNCC = makeField(FIELD_H);
        addRow(form, gbc, row++, "T\u00EAn NCC: *", txtTenNCC, FIELD_H);

        txtSoDienThoai = makeField(FIELD_H);
        addRow(form, gbc, row++, "S\u1ED1 \u0111i\u1EC7n tho\u1EA1i:", txtSoDienThoai, FIELD_H);

        txtEmail = makeField(FIELD_H);
        addRow(form, gbc, row++, "Email:", txtEmail, FIELD_H);

        txtDiaChi = makeField(FIELD_H);
        addRow(form, gbc, row++, "\u0110\u1ECBa ch\u1EC9:", txtDiaChi, FIELD_H);

        // Spacer
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        form.add(Box.createVerticalGlue(), gbc);

        return form;
    }

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

        bp.add(btnThem); bp.add(btnSua);
        bp.add(btnXoa);  bp.add(btnLamMoi);

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

        var sec = new JLabel("Danh s\u00E1ch nh\u00E0 cung c\u1EA5p");
        sec.setFont(F_SEC);
        sec.setForeground(TXT_PRI);
        card.add(sec, BorderLayout.NORTH);

        tableModel = new NhaCungCapTableModel();
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
        for (var ncc : dao.getAll()) {
            tableModel.addRow(new Object[]{
                ncc.getMaNCC(), ncc.getTenNCC(),
                ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "",
                ncc.getEmail() != null ? ncc.getEmail() : "",
                ncc.getDiaChi() != null ? ncc.getDiaChi() : ""
            });
        }
    }

    private void handleTableSelection() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            var ncc = dao.findById((Integer) tableModel.getValueAt(row, 0));
            if (ncc != null) fillForm(ncc);
        }
    }

    private void fillForm(NhaCungCap ncc) {
        txtMaNCC.setText(String.valueOf(ncc.getMaNCC()));
        txtTenNCC.setText(ncc.getTenNCC());
        txtSoDienThoai.setText(ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "");
        txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
        txtDiaChi.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
    }

    private void handleThem() {
        if (!validateForm()) return;
        var ncc = new NhaCungCap();
        ncc.setTenNCC(txtTenNCC.getText().trim());
        ncc.setSoDienThoai(txtSoDienThoai.getText().trim().isEmpty() ? null : txtSoDienThoai.getText().trim());
        ncc.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        ncc.setDiaChi(txtDiaChi.getText().trim().isEmpty() ? null : txtDiaChi.getText().trim());
        if (dao.insert(ncc)) {
            JOptionPane.showMessageDialog(this, "Th\u00EAm nh\u00E0 cung c\u1EA5p th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
            handleLamMoi(); loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Th\u00EAm th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSua() {
        if (txtMaNCC.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn NCC c\u1EA7n s\u1EEDa!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        var ncc = new NhaCungCap();
        ncc.setMaNCC(Integer.parseInt(txtMaNCC.getText()));
        ncc.setTenNCC(txtTenNCC.getText().trim());
        ncc.setSoDienThoai(txtSoDienThoai.getText().trim().isEmpty() ? null : txtSoDienThoai.getText().trim());
        ncc.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        ncc.setDiaChi(txtDiaChi.getText().trim().isEmpty() ? null : txtDiaChi.getText().trim());
        if (dao.update(ncc)) {
            JOptionPane.showMessageDialog(this, "C\u1EADp nh\u1EADt th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
            handleLamMoi(); loadData();
        } else {
            JOptionPane.showMessageDialog(this, "C\u1EADp nh\u1EADt th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleXoa() {
        if (txtMaNCC.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng ch\u1ECDn NCC c\u1EA7n x\u00F3a!", "Th\u00F4ng b\u00E1o", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "X\u00E1c nh\u1EADn x\u00F3a nh\u00E0 cung c\u1EA5p n\u00E0y?", "X\u00E1c nh\u1EADn", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(txtMaNCC.getText()))) {
                JOptionPane.showMessageDialog(this, "X\u00F3a th\u00E0nh c\u00F4ng!", "Th\u00F4ng b\u00E1o", JOptionPane.INFORMATION_MESSAGE);
                handleLamMoi(); loadData();
            } else {
                JOptionPane.showMessageDialog(this, "X\u00F3a th\u1EA5t b\u1EA1i!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLamMoi() {
        txtMaNCC.setText(""); txtTenNCC.setText(""); txtSoDienThoai.setText("");
        txtEmail.setText(""); txtDiaChi.setText("");
        table.clearSelection();
    }

    private boolean validateForm() {
        if (txtTenNCC.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui l\u00F2ng nh\u1EADp t\u00EAn NCC!", "L\u1ED7i", JOptionPane.ERROR_MESSAGE);
            txtTenNCC.requestFocus();
            return false;
        }
        return true;
    }
}
