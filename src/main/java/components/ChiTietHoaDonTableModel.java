package components;

import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;

/**
 * Table model cho ChiTietHoaDon – BanHangPanel v6
 *
 * Cột:
 *   0  MaCTHD       (ẩn)
 *   1  MaLoHang     (ẩn)
 *   2  STT
 *   3  Mã SP
 *   4  Tên thuốc
 *   5  Đơn vị
 *   6  Số lô
 *   7  HSD
 *   8  Số lượng     ← editable
 *   9  Đơn giá
 *  10  Thành tiền
 *  11  Tồn lô
 *
 * @version 6.0
 */
public class ChiTietHoaDonTableModel extends DefaultTableModel {

    public ChiTietHoaDonTableModel() {
        addColumn("MaCTHD");       // 0 – hidden
        addColumn("MaLoHang");     // 1 – hidden
        addColumn("STT");          // 2
        addColumn("Mã SP");       // 3
        addColumn("Tên thuốc");   // 4
        addColumn("Đơn vị");      // 5
        addColumn("Số lô");       // 6
        addColumn("HSD");          // 7
        addColumn("Số lượng");    // 8
        addColumn("Đơn giá");     // 9
        addColumn("Thành tiền");  // 10
        addColumn("Tồn lô");     // 11
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0  -> Integer.class;       // MaCTHD
            case 1  -> Integer.class;       // MaLoHang
            case 2  -> Integer.class;       // STT
            case 3  -> Integer.class;       // Mã SP
            case 4  -> String.class;        // Tên thuốc
            case 5  -> String.class;        // Đơn vị
            case 6  -> String.class;        // Số lô
            case 7  -> String.class;        // HSD
            case 8  -> Integer.class;       // Số lượng
            case 9  -> BigDecimal.class;    // Đơn giá
            case 10 -> BigDecimal.class;    // Thành tiền
            case 11 -> Integer.class;       // Tồn lô
            default -> String.class;
        };
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 8; // Chỉ cho sửa Số lượng
    }
}
