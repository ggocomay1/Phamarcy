package components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;

/**
 * StatusBadgeRenderer - Renderer hiển thị trạng thái dạng badge màu trong JTable.
 * Dùng cho cột "Trạng thái" / "Mức cảnh báo" trong các bảng.
 *
 * Cách dùng:
 *   table.getColumnModel().getColumn(colIndex).setCellRenderer(new StatusBadgeRenderer());
 *
 * @version 2.0
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    private static final Color BG_DANGER    = new Color(254, 226, 226);
    private static final Color FG_DANGER    = new Color(185, 28, 28);
    private static final Color BG_WARNING   = new Color(255, 237, 213);
    private static final Color FG_WARNING   = new Color(194, 120, 3);
    private static final Color BG_SUCCESS   = new Color(220, 252, 231);
    private static final Color FG_SUCCESS   = new Color(21, 128, 61);
    private static final Color BG_INFO      = new Color(219, 234, 254);
    private static final Color FG_INFO      = new Color(30, 64, 175);
    private static final Color BG_DEFAULT   = new Color(243, 244, 246);
    private static final Color FG_DEFAULT   = new Color(75, 85, 99);

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

        if (isSelected) return label;

        switch (text.toLowerCase()) {
            case "hết hàng": case "nguy cấp": case "đã hết hạn": case "đã hủy":
                label.setBackground(BG_DANGER); label.setForeground(FG_DANGER); break;
            case "tồn thấp": case "sắp hết": case "cao": case "cảnh báo": case "đang xử lý":
                label.setBackground(BG_WARNING); label.setForeground(FG_WARNING); break;
            case "đủ hàng": case "còn hàng": case "ổn định": case "hoàn thành": case "đã thanh toán":
                label.setBackground(BG_SUCCESS); label.setForeground(FG_SUCCESS); break;
            case "mới":
                label.setBackground(BG_INFO); label.setForeground(FG_INFO); break;
            default:
                label.setBackground(BG_DEFAULT); label.setForeground(FG_DEFAULT); break;
        }
        return label;
    }
}
