package dao;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.ConnectDB;
import entity.KhachHang;

/**
 * DAO class cho KhachHang
 * 
 * @author Generated
 * @version 1.0
 */
public class KhachHangDao {

	public List<KhachHang> getAll() {
		List<KhachHang> list = new ArrayList<>();
		try (
			var con = ConnectDB.getCon();
			var stmt = con.createStatement();
			var rs = stmt.executeQuery(
				"SELECT * FROM KhachHang WHERE DaXoa = 0 ORDER BY HoTen"
			);
		) {
			while (rs.next()) {
				list.add(mapResultSet(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public KhachHang findById(int maKhachHang) {
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement("SELECT * FROM KhachHang WHERE MaKhachHang = ? AND DaXoa = 0");
		) {
			ps.setInt(1, maKhachHang);
			var rs = ps.executeQuery();
			if (rs.next()) {
				return mapResultSet(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean insert(KhachHang kh) {
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"INSERT INTO KhachHang(HoTen, SoDienThoai, Email, DiaChi, HoSoBenhAn) " +
				"VALUES (?, ?, ?, ?, ?)"
			);
		) {
			ps.setNString(1, kh.getHoTen());
			ps.setNString(2, kh.getSoDienThoai());
			ps.setNString(3, kh.getEmail());
			ps.setNString(4, kh.getDiaChi());
			ps.setNString(5, kh.getHoSoBenhAn());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean update(KhachHang kh) {
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"UPDATE KhachHang SET HoTen=?, SoDienThoai=?, Email=?, DiaChi=?, HoSoBenhAn=? " +
				"WHERE MaKhachHang=?"
			);
		) {
			ps.setNString(1, kh.getHoTen());
			ps.setNString(2, kh.getSoDienThoai());
			ps.setNString(3, kh.getEmail());
			ps.setNString(4, kh.getDiaChi());
			ps.setNString(5, kh.getHoSoBenhAn());
			ps.setInt(6, kh.getMaKhachHang());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean delete(int maKhachHang) {
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement("UPDATE KhachHang SET DaXoa=1 WHERE MaKhachHang=?");
		) {
			ps.setInt(1, maKhachHang);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Tìm kiếm khách hàng theo tên hoặc số điện thoại (cho autocomplete)
	 */
	public List<KhachHang> searchByNameOrPhone(String keyword) {
		List<KhachHang> list = new ArrayList<>();
		if (keyword == null || keyword.trim().isEmpty()) {
			return list;
		}
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"SELECT TOP 10 * FROM KhachHang WHERE DaXoa = 0 " +
				"AND (HoTen LIKE ? OR SoDienThoai LIKE ?) ORDER BY HoTen"
			);
		) {
			String pattern = "%" + keyword.trim() + "%";
			ps.setNString(1, pattern);
			ps.setNString(2, pattern);
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
	 * Thêm khách hàng mới và trả về mã khách hàng vừa tạo
	 */
	public Integer insertAndGetId(KhachHang kh) {
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"INSERT INTO KhachHang(HoTen, SoDienThoai, Email, DiaChi, HoSoBenhAn) " +
				"VALUES (?, ?, ?, ?, ?); SELECT SCOPE_IDENTITY() AS NewId;"
			);
		) {
			ps.setNString(1, kh.getHoTen());
			ps.setNString(2, kh.getSoDienThoai());
			ps.setNString(3, kh.getEmail());
			ps.setNString(4, kh.getDiaChi());
			ps.setNString(5, kh.getHoSoBenhAn());
			boolean hasResult = ps.execute();
			// Skip first result set (update count), get second (SELECT SCOPE_IDENTITY)
			if (!hasResult) {
				hasResult = ps.getMoreResults();
			}
			if (hasResult) {
				var rs = ps.getResultSet();
				if (rs.next()) {
					return rs.getInt("NewId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private KhachHang mapResultSet(ResultSet rs) throws Exception {
		var kh = new KhachHang();
		kh.setMaKhachHang(rs.getInt("MaKhachHang"));
		kh.setHoTen(rs.getString("HoTen"));
		kh.setSoDienThoai(rs.getString("SoDienThoai"));
		kh.setEmail(rs.getString("Email"));
		kh.setDiaChi(rs.getString("DiaChi"));
		kh.setHoSoBenhAn(rs.getString("HoSoBenhAn"));
		kh.setDaXoa(rs.getBoolean("DaXoa"));
		if (rs.getTimestamp("NgayTao") != null) {
			kh.setNgayTao(rs.getTimestamp("NgayTao").toLocalDateTime());
		}
		return kh;
	}
}
