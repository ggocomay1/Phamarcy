package dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.ConnectDB;
import entity.ChiTietHoaDon;

/**
 * DAO class cho ChiTietHoaDon
 * 
 * @author Generated
 * @version 1.0
 */
public class ChiTietHoaDonDao {

	/**
	 * Lấy chi tiết hóa đơn theo mã hóa đơn
	 */
	public List<ChiTietHoaDon> getByMaHoaDon(int maHoaDon) {
		List<ChiTietHoaDon> list = new ArrayList<>();
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"SELECT cthd.*, sp.TenSanPham, lh.SoLo " +
				"FROM ChiTietHoaDon cthd " +
				"JOIN SanPham sp ON sp.MaSanPham = cthd.MaSanPham " +
				"JOIN LoHang lh ON lh.MaLoHang = cthd.MaLoHang " +
				"WHERE cthd.MaHoaDon = ? " +
				"ORDER BY cthd.MaCTHD"
			);
		) {
			ps.setInt(1, maHoaDon);
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
	 * Map ResultSet to ChiTietHoaDon entity
	 */
	private ChiTietHoaDon mapResultSet(ResultSet rs) throws Exception {
		var cthd = new ChiTietHoaDon();
		cthd.setMaCTHD(rs.getInt("MaCTHD"));
		cthd.setMaHoaDon(rs.getInt("MaHoaDon"));
		cthd.setMaLoHang(rs.getInt("MaLoHang"));
		cthd.setMaSanPham(rs.getInt("MaSanPham"));
		cthd.setSoLuong(rs.getInt("SoLuong"));
		cthd.setGiaBan(rs.getBigDecimal("GiaBan"));
		cthd.setThanhTien(rs.getBigDecimal("ThanhTien"));
		return cthd;
	}

	/**
	 * Lấy chi tiết hóa đơn với thông tin sản phẩm và lô hàng (cho hiển thị)
	 * Trả về: MaCTHD, MaSanPham, TenSanPham, SoLo, SoLuong, GiaBan, ThanhTien
	 */
	public List<Object[]> getDetailForDisplay(int maHoaDon) {
		List<Object[]> list = new ArrayList<>();
		try (
			var con = ConnectDB.getCon();
			var ps = con.prepareStatement(
				"SELECT cthd.MaCTHD, cthd.MaSanPham, sp.TenSanPham, lh.SoLo, " +
				"cthd.SoLuong, cthd.GiaBan, cthd.ThanhTien " +
				"FROM ChiTietHoaDon cthd " +
				"JOIN SanPham sp ON sp.MaSanPham = cthd.MaSanPham " +
				"JOIN LoHang lh ON lh.MaLoHang = cthd.MaLoHang " +
				"WHERE cthd.MaHoaDon = ? " +
				"ORDER BY cthd.MaCTHD"
			);
		) {
			ps.setInt(1, maHoaDon);
			var rs = ps.executeQuery();
			while (rs.next()) {
				list.add(new Object[]{
					rs.getInt("MaCTHD"),
					rs.getInt("MaSanPham"),
					rs.getString("TenSanPham"),
					rs.getString("SoLo"),
					rs.getInt("SoLuong"),
					rs.getBigDecimal("GiaBan"),
					rs.getBigDecimal("ThanhTien")
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Cập nhật số lượng chi tiết hóa đơn + cập nhật thành tiền
	 * Cũng hoàn trả/điều chỉnh tồn kho lô hàng tương ứng
	 */
	public boolean updateSoLuong(int maCTHD, int soLuongMoi) {
		try (
			var con = ConnectDB.getCon();
		) {
			// Lấy thông tin hiện tại
			var ps1 = con.prepareStatement(
				"SELECT MaLoHang, SoLuong, GiaBan FROM ChiTietHoaDon WHERE MaCTHD = ?"
			);
			ps1.setInt(1, maCTHD);
			var rs = ps1.executeQuery();
			if (!rs.next()) return false;

			int maLoHang = rs.getInt("MaLoHang");
			int soLuongCu = rs.getInt("SoLuong");
			BigDecimal giaBan = rs.getBigDecimal("GiaBan");
			rs.close();
			ps1.close();

			int chenhLech = soLuongMoi - soLuongCu; // dương = bán thêm, âm = hoàn trả

			// Kiểm tra tồn kho nếu bán thêm
			if (chenhLech > 0) {
				var psCheck = con.prepareStatement(
					"SELECT SoLuongTon FROM LoHang WHERE MaLoHang = ?"
				);
				psCheck.setInt(1, maLoHang);
				var rsCheck = psCheck.executeQuery();
				if (rsCheck.next()) {
					int ton = rsCheck.getInt("SoLuongTon");
					if (ton < chenhLech) {
						rsCheck.close();
						psCheck.close();
						return false; // Không đủ tồn kho
					}
				}
				rsCheck.close();
				psCheck.close();
			}

			// Cập nhật chi tiết hóa đơn
			BigDecimal thanhTienMoi = giaBan.multiply(BigDecimal.valueOf(soLuongMoi));
			var ps2 = con.prepareStatement(
				"UPDATE ChiTietHoaDon SET SoLuong = ?, ThanhTien = ? WHERE MaCTHD = ?"
			);
			ps2.setInt(1, soLuongMoi);
			ps2.setBigDecimal(2, thanhTienMoi);
			ps2.setInt(3, maCTHD);
			ps2.executeUpdate();
			ps2.close();

			// Cập nhật tồn kho lô hàng
			var ps3 = con.prepareStatement(
				"UPDATE LoHang SET SoLuongTon = SoLuongTon - ? WHERE MaLoHang = ?"
			);
			ps3.setInt(1, chenhLech);
			ps3.setInt(2, maLoHang);
			ps3.executeUpdate();
			ps3.close();

			// Cập nhật tổng tiền hóa đơn
			var ps4 = con.prepareStatement(
				"UPDATE HoaDonBan SET TongTien = (SELECT SUM(ThanhTien) FROM ChiTietHoaDon WHERE MaHoaDon = " +
				"(SELECT MaHoaDon FROM ChiTietHoaDon WHERE MaCTHD = ?)) " +
				"WHERE MaHoaDon = (SELECT MaHoaDon FROM ChiTietHoaDon WHERE MaCTHD = ?)"
			);
			ps4.setInt(1, maCTHD);
			ps4.setInt(2, maCTHD);
			ps4.executeUpdate();
			ps4.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Xóa chi tiết hóa đơn + hoàn trả tồn kho lô hàng
	 */
	public boolean deleteChiTiet(int maCTHD) {
		try (
			var con = ConnectDB.getCon();
		) {
			// Lấy thông tin cũ để hoàn trả tồn kho
			var ps1 = con.prepareStatement(
				"SELECT MaHoaDon, MaLoHang, SoLuong FROM ChiTietHoaDon WHERE MaCTHD = ?"
			);
			ps1.setInt(1, maCTHD);
			var rs = ps1.executeQuery();
			if (!rs.next()) return false;

			int maHoaDon = rs.getInt("MaHoaDon");
			int maLoHang = rs.getInt("MaLoHang");
			int soLuong = rs.getInt("SoLuong");
			rs.close();
			ps1.close();

			// Hoàn trả tồn kho
			var ps2 = con.prepareStatement(
				"UPDATE LoHang SET SoLuongTon = SoLuongTon + ? WHERE MaLoHang = ?"
			);
			ps2.setInt(1, soLuong);
			ps2.setInt(2, maLoHang);
			ps2.executeUpdate();
			ps2.close();

			// Xóa chi tiết
			var ps3 = con.prepareStatement("DELETE FROM ChiTietHoaDon WHERE MaCTHD = ?");
			ps3.setInt(1, maCTHD);
			ps3.executeUpdate();
			ps3.close();

			// Cập nhật tổng tiền hóa đơn
			var ps4 = con.prepareStatement(
				"UPDATE HoaDonBan SET TongTien = ISNULL((SELECT SUM(ThanhTien) FROM ChiTietHoaDon WHERE MaHoaDon = ?), 0) " +
				"WHERE MaHoaDon = ?"
			);
			ps4.setInt(1, maHoaDon);
			ps4.setInt(2, maHoaDon);
			ps4.executeUpdate();
			ps4.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}

