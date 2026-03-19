package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.ConnectDB;
import entity.LoHang;

/**
 * DAO class cho LoHang
 * 
 * @author Generated
 * @version 1.0
 */
public class LoHangDao {

	/**
	 * Lấy tất cả lô hàng
	 */
	public List<LoHang> getAll() {
		List<LoHang> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var stmt = con.createStatement();
				var rs = stmt.executeQuery(
						"SELECT lh.*, sp.TenSanPham, ncc.TenNCC, pn.NgayNhap as NgayNhapPN " +
								"FROM LoHang lh " +
								"JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham AND sp.DaXoa = 0 " +
								"LEFT JOIN PhieuNhap pn ON lh.MaPhieuNhap = pn.MaPhieuNhap " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.TrangThai <> N'Ngưng bán' " +
								"ORDER BY lh.HanSuDung ASC, lh.ThoiGianNhap ASC");) {
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Lấy lô hàng theo mã sản phẩm
	 */
	public List<LoHang> getByMaSanPham(int maSanPham) {
		List<LoHang> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT lh.*, sp.TenSanPham, ncc.TenNCC, pn.NgayNhap as NgayNhapPN " +
								"FROM LoHang lh " +
								"JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham AND sp.DaXoa = 0 " +
								"LEFT JOIN PhieuNhap pn ON lh.MaPhieuNhap = pn.MaPhieuNhap " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.MaSanPham = ? AND lh.TrangThai <> N'Ngưng bán' " +
								"ORDER BY lh.HanSuDung ASC, lh.ThoiGianNhap ASC");) {
			ps.setInt(1, maSanPham);
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
	 * Lấy lô hàng còn tồn theo mã SP (Dùng cho bảng C.Tiết hồ sơ SP)
	 */
	public List<LoHang> getActiveBatchesByMaSP(int maSanPham) {
		List<LoHang> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT lh.*, ncc.TenNCC " +
								"FROM LoHang lh " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.MaSanPham = ? " +
								"AND lh.SoLuongTon > 0 AND lh.TrangThai <> N'Ngưng bán' " +
								"ORDER BY lh.HanSuDung ASC");) {
			ps.setInt(1, maSanPham);
			var rs = ps.executeQuery();
			while (rs.next()) {
				var lh = mapResultSet(rs);
				try { lh.setTenNhaCungCap(rs.getString("TenNCC")); } catch (Exception ignore) {}
				list.add(lh);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Lấy lô hàng còn tồn và chưa hết hạn (cho bán hàng FEFO)
	 */
	public List<LoHang> getAvailableForSale(int maSanPham) {
		List<LoHang> list = new ArrayList<>();
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT * FROM LoHang " +
								"WHERE MaSanPham = ? " +
								"AND SoLuongTon > 0 " +
								"AND TrangThai = N'Đang bán' " +
								"AND HanSuDung >= CAST(GETDATE() AS DATE) " +
								"ORDER BY HanSuDung ASC, NgayNhap ASC");) {
			ps.setInt(1, maSanPham);
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
	 * Lấy lô hàng theo ID
	 */
	public LoHang findById(int maLoHang) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT lh.*, sp.TenSanPham, ncc.TenNCC, pn.NgayNhap as NgayNhapPN " +
								"FROM LoHang lh " +
								"JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham AND sp.DaXoa = 0 " +
								"LEFT JOIN PhieuNhap pn ON lh.MaPhieuNhap = pn.MaPhieuNhap " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.MaLoHang = ? AND lh.TrangThai <> N'Ngưng bán'");) {
			ps.setInt(1, maLoHang);
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
	 * Tìm lô hàng theo Mã SP và Số lô (Dùng để kiểm tra trùng lô)
	 */
	public LoHang findByMaSPAndSoLo(int maSanPham, String soLo) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT TOP 1 lh.*, sp.TenSanPham, ncc.TenNCC, pn.NgayNhap as NgayNhapPN " +
								"FROM LoHang lh " +
								"JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham AND sp.DaXoa = 0 " +
								"LEFT JOIN PhieuNhap pn ON lh.MaPhieuNhap = pn.MaPhieuNhap " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.MaSanPham = ? AND lh.SoLo = ? AND lh.TrangThai <> N'Ngưng bán' " +
								"ORDER BY lh.HanSuDung ASC");) {
			ps.setInt(1, maSanPham);
			ps.setString(2, soLo);
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
	 * [Requirement: SMART_ACCUMULATION_LOGIC]
	 * Tìm lô hàng theo bộ ba: Mã SP, Số lô, Hạn sử dụng
	 */
	public LoHang findByMaSPSoLoHSD(int maSanPham, String soLo, java.time.LocalDate hanSuDung) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement(
						"SELECT TOP 1 lh.*, sp.TenSanPham, ncc.TenNCC, pn.NgayNhap as NgayNhapPN " +
								"FROM LoHang lh " +
								"JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham AND sp.DaXoa = 0 " +
								"LEFT JOIN PhieuNhap pn ON lh.MaPhieuNhap = pn.MaPhieuNhap " +
								"LEFT JOIN NhaCungCap ncc ON lh.MaNCC = ncc.MaNCC " +
								"WHERE lh.MaSanPham = ? AND lh.SoLo = ? AND lh.HanSuDung = ? AND lh.TrangThai <> N'Ngưng bán'");) {
			ps.setInt(1, maSanPham);
			ps.setString(2, soLo);
			ps.setDate(3, java.sql.Date.valueOf(hanSuDung));
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
	 * Cập nhật trạng thái lô hàng
	 */
	public boolean updateTrangThai(int maLoHang, String trangThai) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("UPDATE LoHang SET TrangThai = ? WHERE MaLoHang = ?");) {
			ps.setString(1, trangThai);
			ps.setInt(2, maLoHang);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Xóa lô hàng
	 */
	public boolean delete(int maLoHang) {
		try (
				var con = ConnectDB.getCon();
				var ps = con.prepareStatement("UPDATE LoHang SET TrangThai = N'Ngưng bán' WHERE MaLoHang = ?");) {
			ps.setInt(1, maLoHang);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Map ResultSet to LoHang entity
	 */
	private LoHang mapResultSet(ResultSet rs) throws Exception {
		var lh = new LoHang();
		lh.setMaLoHang(rs.getInt("MaLoHang"));
		lh.setMaSanPham(rs.getInt("MaSanPham"));
		lh.setSoLo(rs.getString("SoLo"));
		var maNCC = rs.getObject("MaNCC");
		if (maNCC != null) {
			lh.setMaNCC((Integer) maNCC);
		}
		var maPN = rs.getObject("MaPhieuNhap");
		if (maPN != null) {
			lh.setMaPhieuNhap((Integer) maPN);
		}
		var ngaySX = rs.getDate("NgaySanXuat");
		if (ngaySX != null) {
			lh.setNgaySanXuat(ngaySX.toLocalDate());
		}
		if (rs.getDate("HanSuDung") != null) {
			lh.setHanSuDung(rs.getDate("HanSuDung").toLocalDate());
		}
		lh.setGiaNhap(rs.getBigDecimal("GiaNhap"));
		lh.setSoLuongNhap(rs.getInt("SoLuongNhap"));
		lh.setSoLuongTon(rs.getInt("SoLuongTon"));

		// Map NgayNhapPN if available, otherwise fallback to NgayNhap/ThoiGianNhap
		try {
			java.sql.Timestamp tsPN = rs.getTimestamp("NgayNhapPN");
			if (tsPN != null) {
				lh.setNgayNhap(tsPN.toLocalDateTime());
			} else {
				java.sql.Timestamp tsLH = rs.getTimestamp("NgayNhap");
				if (tsLH != null)
					lh.setNgayNhap(tsLH.toLocalDateTime());
			}
		} catch (Exception ignored) {
		}

		lh.setTrangThai(rs.getString("TrangThai"));

		// Các cột mở rộng
		try {
			lh.setLoaiHinhBan(rs.getString("LoaiHinhBan"));
		} catch (Exception ignored) {
		}
		try {
			var ts = rs.getTimestamp("ThoiGianNhap");
			if (ts != null)
				lh.setThoiGianNhap(ts.toLocalDateTime());
			else if (lh.getNgayNhap() != null)
				lh.setThoiGianNhap(lh.getNgayNhap());
		} catch (Exception ignored) {
		}

		try {
			lh.setTenSanPham(rs.getString("TenSanPham"));
		} catch (Exception ignored) {
		}
		try {
			lh.setTenNhaCungCap(rs.getString("TenNCC"));
		} catch (Exception ignored) {
		}
		try {
			lh.setDonViNhap(rs.getString("DonViNhap"));
		} catch (Exception ignored) {
		}
		try {
			lh.setSoViTrenHop(rs.getInt("SoViTrenHop"));
		} catch (Exception ignored) {
		}
		try {
			lh.setSoVienTrenVi(rs.getInt("SoVienTrenVi"));
		} catch (Exception ignored) {
		}
		try {
			lh.setTongSoVien(rs.getInt("TongSoVien_Lo"));
		} catch (Exception ignored) {
		}

		return lh;
	}
}
