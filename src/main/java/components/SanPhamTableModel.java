package components;

import javax.swing.table.DefaultTableModel;

/**
 * Table model cho SanPham - đồng bộ DB CuaHangThuoc_Batch
 * Cột: Mã SP | Tên sản phẩm | Giá bán (VND) | Đơn vị tính | Tổng tồn | Hạn SD gần nhất
 * 
 * @author Generated
 * @version 2.0
 */
public class SanPhamTableModel extends DefaultTableModel {

	public SanPhamTableModel() {
		addColumn("STT");
		addColumn("Mã SP");
		addColumn("Tên sản phẩm");
		addColumn("Giá bán");
		addColumn("Đơn vị tính");
		addColumn("Tổng tồn");
		addColumn("Hạn SD gần nhất");
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return switch (columnIndex) {
			case 0 -> Integer.class;              // STT
			case 1 -> Integer.class;              // Mã SP
			case 2 -> String.class;               // Tên sản phẩm
			case 3 -> String.class;               // Giá bán (format VND)
			case 4 -> String.class;               // Đơn vị tính
			case 5 -> Integer.class;              // Tổng tồn
			case 6 -> String.class;               // Hạn SD gần nhất
			default -> String.class;
		};
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false; // Không cho edit trực tiếp trên table
	}

	/**
	 * Đổi tên cột động (dùng cho hiển thị mũi tên sắp xếp ▲ ▼)
	 */
	@SuppressWarnings("unchecked")
	public void setColumnName(int columnIndex, String name) {
		if (columnIndex >= 0 && columnIndex < columnIdentifiers.size()) {
			((java.util.Vector<Object>)columnIdentifiers).set(columnIndex, name);
		}
	}
}
