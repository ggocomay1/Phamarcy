package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.ConnectDB;
import entity.PageResult;
import entity.SanPham;

/**
 * DAO class cho SanPham
 * 
 * @author Generated
 * @version 1.0
 */
public class SanPhamDao {

	/**
	 * Lấy tất cả sản phẩm chưa xóa
	 */
	public List<SanPham> getAll() {
		List<SanPham> list = new ArrayList<>();
		String sql = "SELECT sp.*, "
				+ "(SELECT ISNULL(SUM(SoLuongTon), 0) FROM LoHang lh WHERE lh.MaSanPham = sp.MaSanPham) as TongTon, "
				+ "(SELECT MIN(HanSuDung) FROM LoHang lh2 WHERE lh2.MaSanPham = sp.MaSanPham AND lh2.SoLuongTon > 0) as HanSuDungGanNhat "
				+ "FROM SanPham sp WHERE sp.TrangThai = 1 ORDER BY sp.TenSanPham";
		try (
				var con = ConnectDB.getCon();
				var stmt = con.createStatement();
				var rs = stmt.executeQuery(sql);) {
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Lấy sản phẩm theo trang (overload đơn giản)
	 */
	public PageResult<SanPham> getByPage(int page, int size) {
		return getByPage(page, size, null, "MaSanPham", "ASC", "Tất cả", "Tất cả ĐVT");
	}

	/**
	 * Lấy sản phẩm theo trang - gọi sp_SanPham_GetPage (v2)
	 * Hỗ trợ tìm kiếm + sắp xếp động
	 * 
	 * @param page       số trang (bắt đầu từ 1)
	 * @param size       số dòng mỗi trang
	 * @param keyword    từ khóa tìm kiếm (null = không lọc)
	 * @param sortColumn tên cột sắp xếp
	 * @param sortOrder  ASC hoặc DESC
	 * @return PageResult chứa danh sách sản phẩm + metadata phân trang
	 */
	public PageResult<SanPham> getByPage(int page, int size, String keyword,
			String sortColumn, String sortOrder, String loaiHinhBan, String dvt) {
		List<SanPham> list = new ArrayList<>();
		int totalRows = 0;
		int totalPages = 0;

		String whereClause = "WHERE sp.TrangThai = 1 ";
		if (keyword != null && !keyword.trim().isEmpty()) {
			whereClause += " AND sp.TenSanPham LIKE ? ";
		}
		if (loaiHinhBan != null && !loaiHinhBan.trim().isEmpty() && !loaiHinhBan.equals("Tất cả")) {
			whereClause += " AND EXISTS (SELECT 1 FROM LoHang lh WHERE lh.MaSanPham = sp.MaSanPham AND lh.LoaiHinhBan = ? AND lh.SoLuongTon > 0) ";
		}
		if (dvt != null && !dvt.trim().isEmpty() && !dvt.equals("Tất cả ĐVT")) {
			whereClause += " AND sp.DonViTinh = ? ";
		}

		String countQuery = "SELECT COUNT(*) FROM SanPham sp " + whereClause;

		String sortExp = "sp.MaSanPham";
		if ("TenSanPham".equals(sortColumn)) sortExp = "sp.TenSanPham";
		else if ("GiaBanDeXuat".equals(sortColumn)) sortExp = "sp.GiaBanDeXuat";
		else if ("MoTa".equals(sortColumn)) sortExp = "sp.MoTa";
		else if ("DonViTinh".equals(sortColumn)) sortExp = "sp.DonViTinh";
		else if ("MucTonToiThieu".equals(sortColumn)) sortExp = "sp.MucTonToiThieu";

		String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.GiaBanDeXuat, sp.DonViTinh, "
				+ "sp.MoTa, sp.MucTonToiThieu, "
				+ "ISNULL((SELECT SUM(SoLuongTon) FROM LoHang tk WHERE tk.MaSanPham = sp.MaSanPham), 0) AS TongTon, "
				+ "(SELECT MIN(HanSuDung) FROM LoHang tk2 WHERE tk2.MaSanPham = sp.MaSanPham AND tk2.SoLuongTon > 0) AS HanSuDungGanNhat "
				+ " FROM SanPham sp "
				+ whereClause
				+ " ORDER BY " + sortExp + " " + (sortOrder != null && sortOrder.equalsIgnoreCase("DESC") ? "DESC" : "ASC")
				+ " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

		try (var con = ConnectDB.getCon()) {
			// Count total rows
			try (var psCount = con.prepareStatement(countQuery)) {
				int paramIndex = 1;
				if (keyword != null && !keyword.trim().isEmpty()) {
					psCount.setString(paramIndex++, "%" + keyword.trim() + "%");
				}
				if (loaiHinhBan != null && !loaiHinhBan.trim().isEmpty() && !loaiHinhBan.equals("Tất cả")) {
					psCount.setString(paramIndex++, loaiHinhBan);
				}
				if (dvt != null && !dvt.trim().isEmpty() && !dvt.equals("Tất cả ĐVT")) {
					psCount.setString(paramIndex++, dvt);
				}
				var rsCount = psCount.executeQuery();
				if (rsCount.next()) totalRows = rsCount.getInt(1);
			}

			// Fetch items
			try (var ps = con.prepareStatement(sql)) {
				int paramIndex = 1;
				if (keyword != null && !keyword.trim().isEmpty()) {
					ps.setString(paramIndex++, "%" + keyword.trim() + "%");
				}
				if (loaiHinhBan != null && !loaiHinhBan.trim().isEmpty() && !loaiHinhBan.equals("Tất cả")) {
					ps.setString(paramIndex++, loaiHinhBan);
				}
				if (dvt != null && !dvt.trim().isEmpty() && !dvt.equals("Tất cả ĐVT")) {
					ps.setString(paramIndex++, dvt);
				}
				ps.setInt(paramIndex++, (page - 1) * size);
				ps.setInt(paramIndex++, size);

				var rs = ps.executeQuery();
				while (rs.next()) {
					var sp = new SanPham();
					sp.setMaSanPham(rs.getInt("MaSanPham"));
					sp.setTenSanPham(rs.getString("TenSanPham"));
					sp.setDonViTinh(rs.getString("DonViTinh"));
					sp.setGiaBanDeXuat(rs.getBigDecimal("GiaBanDeXuat"));
					sp.setMoTa(rs.getString("MoTa"));
					sp.setMucTonToiThieu(rs.getInt("MucTonToiThieu"));
					sp.setTongTon(rs.getInt("TongTon")); 
					if (rs.getDate("HanSuDungGanNhat") != null) {
						sp.setHanSuDungGanNhat(rs.getDate("HanSuDungGanNhat").toLocalDate());
					}
					list.add(sp);
				}
			}
			totalPages = (int) Math.ceil((double) totalRows / size);
			if (totalPages == 0) totalPages = 1;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new PageResult<>(list, totalRows, totalPages, page, size);
	}

	/**
	 * Tìm sản phẩm theo ID
	 */
	public SanPham findById(int maSanPham) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("SELECT * FROM SanPham WHERE MaSanPham = ? AND DaXoa = 0");) {
			ps.setInt(1, maSanPham);
			var rs = ps.executeQuery();
			if (rs.next()) {
				var sp = mapResultSet(rs);
				// Lấy thêm tổng tồn do hàm findById select thẳng từ SanPham không qua view
				try (var psTon = con.prepareStatement("SELECT ISNULL(SUM(SoLuongTon), 0) FROM LoHang WHERE MaSanPham = ?")) {
					psTon.setInt(1, maSanPham);
					var rsTon = psTon.executeQuery();
					if (rsTon.next()) {
						sp.setTongTon(rsTon.getInt(1));
					}
				}
				return sp;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Lấy thông tin chi tiết đầy đủ mở rộng (Dùng cho xem chi tiết)
	 */
	public SanPham getFullDetailByMaSP(int maSanPham) {
		String sql = "SELECT sp.*, "
				+ "ISNULL((SELECT SUM(SoLuongTon) FROM LoHang tk WHERE tk.MaSanPham = sp.MaSanPham), 0) AS TongTon, "
				+ "(SELECT MIN(HanSuDung) FROM LoHang tk2 WHERE tk2.MaSanPham = sp.MaSanPham AND tk2.SoLuongTon > 0) AS HanSuDungGanNhat "
				+ "FROM SanPham sp "
				+ "WHERE sp.MaSanPham = ?";
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(sql)) {
			ps.setInt(1, maSanPham);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tìm sản phẩm theo tên (tìm kiếm)
	 */
	public List<SanPham> searchByName(String keyword) {
		List<SanPham> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT * FROM SanPham WHERE TrangThai = 1 AND TenSanPham LIKE ? ORDER BY TenSanPham ASC");) {
			ps.setString(1, "%" + keyword + "%");
			var rs = ps.executeQuery();
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Tìm sản phẩm khớp chính xác theo tên (Dùng cho NhapHangPanel)
	 */
	public SanPham findByNameExact(String exactName) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("SELECT * FROM SanPham WHERE TrangThai = 1 AND TenSanPham = ? ORDER BY TenSanPham ASC")) {
			ps.setNString(1, exactName);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tìm sản phẩm theo tên chính xác (kể cả đã xóa) - Dùng để Phục hồi
	 */
	public SanPham findByNameIncludingDeleted(String exactName) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("SELECT * FROM SanPham WHERE TenSanPham = ?")) {
			ps.setNString(1, exactName);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Phục hồi sản phẩm đã xóa
	 */
	public boolean resurrect(int maSanPham) {
		try (var con = ConnectDB.getCon();
			 var ps = con.prepareStatement("UPDATE SanPham SET TrangThai = 1, DaXoa = 0 WHERE MaSanPham = ?")) {
			ps.setInt(1, maSanPham);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Thêm sản phẩm mới
	 */
	public boolean insert(SanPham sp) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"INSERT INTO SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, MoTa, MucTonToiThieu) " +
								"VALUES (?, ?, ?, ?, ?)");) {
			ps.setNString(1, sp.getTenSanPham());
			ps.setNString(2, sp.getDonViTinh());
			ps.setBigDecimal(3, sp.getGiaBanDeXuat());
			ps.setNString(4, sp.getMoTa());
			ps.setInt(5, sp.getMucTonToiThieu());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Thêm sản phẩm mới và trả về MaSanPham (Generated Key)
	 * Dùng cho flow Nhập hàng khi tạo SP mới inline
	 */
	public int insertAndGetId(SanPham sp) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"INSERT INTO SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, MoTa, MucTonToiThieu) " +
								"VALUES (?, ?, ?, ?, ?)",
						java.sql.Statement.RETURN_GENERATED_KEYS);) {
			ps.setNString(1, sp.getTenSanPham());
			ps.setNString(2, sp.getDonViTinh());
			ps.setBigDecimal(3, sp.getGiaBanDeXuat());
			ps.setNString(4, sp.getMoTa());
			ps.setInt(5, sp.getMucTonToiThieu());
			ps.executeUpdate();
			var rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Cập nhật sản phẩm
	 */
	public boolean update(SanPham sp) {
		String sql = "UPDATE SanPham SET TenSanPham=?, GiaBanDeXuat=?, DonViTinh=?, " +
				"MoTa=?, MucTonToiThieu=? WHERE MaSanPham=?";
		try (var con = ConnectDB.getCon();
			 var ps = con.prepareStatement(sql);) {
			ps.setString(1, sp.getTenSanPham());
			ps.setBigDecimal(2, sp.getGiaBanDeXuat());
			ps.setString(3, sp.getDonViTinh());
			ps.setString(4, sp.getMoTa());
			ps.setInt(5, sp.getMucTonToiThieu());
			ps.setInt(6, sp.getMaSanPham());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * [Requirement: SYNC_PRODUCT_QUANTITY]
	 * Tính lại tổng tồn kho của 1 sản phẩm từ bảng LoHang và cập nhập vào bảng SanPham
	 */
	public boolean updateTotalQuantity(int maSanPham) {
		String sql = "UPDATE SanPham SET SoLuongTon = (" +
				"SELECT ISNULL(SUM(SoLuongTon), 0) FROM LoHang " +
				"WHERE MaSanPham = ? AND TrangThai <> N'Ngưng bán'" +
				") WHERE MaSanPham = ?";
		try (var con = ConnectDB.getCon();
			 var ps = con.prepareStatement(sql)) {
			ps.setInt(1, maSanPham);
			ps.setInt(2, maSanPham);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Xóa mềm sản phẩm
	 */
	public boolean delete(int maSanPham) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("UPDATE SanPham SET TrangThai = 0, DaXoa = 1 WHERE MaSanPham = ?");) {
			ps.setInt(1, maSanPham);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Lấy danh sách các đơn vị tính duy nhất
	 */
	public List<String> getDistinctUnits() {
		List<String> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var stmt = con.createStatement();
				var rs = stmt.executeQuery("SELECT DISTINCT DonViTinh FROM SanPham WHERE TrangThai = 1 AND DonViTinh IS NOT NULL AND DonViTinh <> '' ORDER BY DonViTinh");) {
			while (rs.next()) {
				list.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Map ResultSet to SanPham entity
	 */
	private SanPham mapResultSet(ResultSet rs) throws Exception {
		var sp = new SanPham();
		sp.setMaSanPham(rs.getInt("MaSanPham"));
		sp.setTenSanPham(rs.getString("TenSanPham"));
		sp.setDonViTinh(rs.getString("DonViTinh"));
		sp.setGiaBanDeXuat(rs.getBigDecimal("GiaBanDeXuat"));
		sp.setMoTa(rs.getString("MoTa"));
		sp.setMucTonToiThieu(rs.getInt("MucTonToiThieu"));

		sp.setDaXoa(rs.getBoolean("DaXoa"));
		if (rs.getTimestamp("NgayTao") != null) {
			sp.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
		}
		
		// Map thêm các trường ảo (nếu có trong câu query)
		try {
			sp.setTongTon(rs.getInt("TongTon"));
		} catch (Exception ignore) {}

		// Try both alias names for HSD column
		try {
			java.sql.Date hsd = rs.getDate("HanSuDungGanNhat");
			if (hsd != null) sp.setHanSuDungGanNhat(hsd.toLocalDate());
		} catch (Exception e1) {
			try {
				java.sql.Date hsd = rs.getDate("HanSDGanNhat");
				if (hsd != null) sp.setHanSuDungGanNhat(hsd.toLocalDate());
			} catch (Exception ignore) {}
		}
		
		return sp;
	}
}
