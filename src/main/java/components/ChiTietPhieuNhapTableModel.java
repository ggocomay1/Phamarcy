package components;

import javax.swing.table.DefaultTableModel;

/**
 * Table model cho ChiTietPhieuNhap
 * 
 * @author Generated
 * @version 1.0
 */
public class ChiTietPhieuNhapTableModel extends DefaultTableModel {

	public ChiTietPhieuNhapTableModel() {
		addColumn("STT");
		addColumn("Mã SP");
		addColumn("Tên sản phẩm");
		addColumn("Số lô");
		addColumn("Hạn sử dụng");
		addColumn("Giá nhập");
		addColumn("Số lượng");
		addColumn("Thành tiền");
		addColumn("ĐVT");
		addColumn("Loại hình");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
			case 0 -> Integer.class;  // STT
			case 1 -> Integer.class;  // Mã SP
			case 2 -> String.class;   // Tên sản phẩm
			case 3 -> String.class;   // Số lô
			case 4 -> java.time.LocalDate.class; // Hạn sử dụng
			case 5 -> java.math.BigDecimal.class; // Giá nhập
			case 6 -> Integer.class;  // Số lượng
			case 7 -> java.math.BigDecimal.class; // Thành tiền
			case 8 -> String.class;   // ĐVT
			case 9 -> String.class;   // Loại hình
			default -> String.class;
		};
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
