package components;

import javax.swing.table.DefaultTableModel;

/**
 * Table model cho LoHang
 * 
 * @author Generated
 * @version 1.0
 */
public class LoHangTableModel extends DefaultTableModel {

	public LoHangTableModel() {
		addColumn("STT");
		addColumn("Tên Sản Phẩm");
		addColumn("Số lô");
		addColumn("Hạn sử dụng");
		addColumn("Số lượng tồn");
		addColumn("Giá nhập");
		addColumn("Nhà cung cấp");
		addColumn("Ngày giờ nhập");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
			case 0 -> Integer.class;  // STT
			case 1 -> String.class;   // Tên Sản Phẩm
			case 2 -> String.class;   // Số lô
			case 3 -> java.time.LocalDate.class; // Hạn sử dụng
			case 4 -> Integer.class;  // Số lượng tồn
			case 5 -> java.math.BigDecimal.class; // Giá nhập
			case 6 -> String.class;   // Nhà cung cấp
			case 7 -> java.time.LocalDateTime.class; // Ngày giờ nhập
			default -> String.class;
		};
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
