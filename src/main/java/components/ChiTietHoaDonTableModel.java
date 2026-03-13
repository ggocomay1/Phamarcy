package components;

import javax.swing.table.DefaultTableModel;

/**
 * Table model cho ChiTietHoaDon
 * Cột: MaCTHD (ẩn), Mã SP, Tên sản phẩm, Số lô, Số lượng, Giá bán, Thành tiền
 * 
 * @author Improved
 * @version 2.0
 */
public class ChiTietHoaDonTableModel extends DefaultTableModel {

	public ChiTietHoaDonTableModel() {
		addColumn("MaCTHD");     // index 0 - hidden
		addColumn("Mã SP");      // index 1
		addColumn("Tên sản phẩm"); // index 2
		addColumn("Số lô");      // index 3
		addColumn("Số lượng");   // index 4
		addColumn("Giá bán");    // index 5
		addColumn("Thành tiền"); // index 6
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
			case 0 -> Integer.class;  // MaCTHD
			case 1 -> Integer.class;  // Mã SP
			case 2 -> String.class;   // Tên sản phẩm
			case 3 -> String.class;   // Số lô
			case 4 -> Integer.class;  // Số lượng
			case 5 -> java.math.BigDecimal.class; // Giá bán
			case 6 -> java.math.BigDecimal.class; // Thành tiền
			default -> String.class;
		};
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
