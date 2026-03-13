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
 * SidebarPanel - Clean pharmacy sidebar (no emoji icons)
 * @version 5.0 – Pharmacy Theme
 */
public class SidebarPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Consumer<String> onMenuSelection;
    private String currentAction = "dashboard";
    private Map<String, JButton> menuItems = new HashMap<>();

    public SidebarPanel(NguoiDung currentUser, Consumer<String> onMenuSelection) {
        this.onMenuSelection = onMenuSelection;
        initialize(currentUser);
    }

    private void initialize(NguoiDung currentUser) {
        setBackground(ColorScheme.SIDEBAR_BG);
        setPreferredSize(new Dimension(230, 0));
        setLayout(new BorderLayout());

        // === Logo ===
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(230, 72));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.SIDEBAR_DIVIDER),
            new EmptyBorder(0, 20, 0, 20)
        ));
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoRow.setOpaque(false);
        // Teal dot instead of emoji
        JLabel dot = new JLabel("\u25CF ");
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        dot.setForeground(ColorScheme.SIDEBAR_ACTIVE);
        logoRow.add(dot);
        JLabel lbl = new JLabel("MEPHAR");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);
        logoRow.add(lbl);
        header.add(logoRow, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // === Menu ===
        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(14, 10, 14, 10));

        String role = currentUser.getVaiTro();
        addIf(menu, "Tong quan", "dashboard", role);
        addIf(menu, "Ban hang", "banhang", role);
        addIf(menu, "Nhap hang", "nhaphang", role);
        addIf(menu, "San pham", "sanpham", role);
        addIf(menu, "Lo hang", "lohang", role);
        addIf(menu, "Khach hang", "khachhang", role);
        addIf(menu, "Nha cung cap", "nhacungcap", role);
        addIf(menu, "Lich su hoa don", "lichsuhoadon", role);
        addIf(menu, "Nguoi dung", "nguoidung", role);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(menu, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(wrap);
        sp.setBorder(null); sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        // === Footer ===
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 0, 10, 0));
        JLabel ver = new JLabel("v5.0 Pharmacy");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(ColorScheme.withAlpha(Color.WHITE, 50));
        footer.add(ver);
        add(footer, BorderLayout.SOUTH);
    }

    // Menu labels without diacritics to avoid font issues
    private static final Map<String, String> LABELS = Map.of(
        "dashboard", "Tổng quan",
        "banhang", "Bán hàng",
        "nhaphang", "Nhập hàng",
        "sanpham", "Sản phẩm",
        "lohang", "Lô hàng",
        "khachhang", "Khách hàng",
        "nhacungcap", "Nhà cung cấp",
        "lichsuhoadon", "Lịch sử hóa đơn",
        "nguoidung", "Người dùng"
    );

    private void addIf(JPanel parent, String text, String action, String role) {
        if (utils.PermissionManager.hasAccess(role, action)) {
            addItem(parent, LABELS.getOrDefault(action, text), action);
        }
    }

    private void addItem(JPanel parent, String text, String action) {
        JButton btn = new JButton("   " + text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(ColorScheme.SIDEBAR_TEXT);
        btn.setBackground(ColorScheme.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.putClientProperty("Component.arc", 10);
        btn.setBorder(new EmptyBorder(10, 16, 10, 14));

        btn.addActionListener(e -> {
            updateActiveState(action);
            if (onMenuSelection != null) onMenuSelection.accept(action);
        });
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled() && !action.equals(currentAction)) {
                    btn.setBackground(ColorScheme.SIDEBAR_HOVER);
                    btn.setForeground(new Color(220, 230, 240));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.isEnabled() && !action.equals(currentAction)) {
                    btn.setBackground(ColorScheme.SIDEBAR_BG);
                    btn.setForeground(ColorScheme.SIDEBAR_TEXT);
                }
            }
        });

        parent.add(Box.createRigidArea(new Dimension(0, 2)));
        parent.add(btn);
        menuItems.put(action, btn);
    }

    public void updateActiveState(String action) {
        if (currentAction != null && menuItems.containsKey(currentAction)) {
            JButton old = menuItems.get(currentAction);
            old.setBackground(ColorScheme.SIDEBAR_BG);
            old.setForeground(old.isEnabled() ? ColorScheme.SIDEBAR_TEXT : new Color(75, 85, 99));
            old.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
        currentAction = action;
        if (menuItems.containsKey(action)) {
            JButton btn = menuItems.get(action);
            if (btn.isEnabled()) {
                btn.setBackground(ColorScheme.SIDEBAR_ACTIVE);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
        }
    }
}
