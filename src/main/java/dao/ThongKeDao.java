package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import common.ConnectDB;

/**
 * ThongKeDao - Data Access Object cho Dashboard Tổng Quan
 * Cung cấp các phương thức truy vấn thống kê cho nhà thuốc
 * 
 * @version 3.0
 */
public class ThongKeDao {

    // ===================== HELPER CLASSES =====================

    /** Thống kê ngày: số hóa đơn & doanh thu */
    public static class ThongKeNgay {
        public int soHoaDon;
        public java.math.BigDecimal doanhThu;

        public ThongKeNgay(int soHoaDon, java.math.BigDecimal doanhThu) {
            this.soHoaDon = soHoaDon;
            this.doanhThu = doanhThu;
        }
    }

    /** Cảnh báo lô hàng sắp hết hạn */
    public static class CanhBaoHetHan {
        public String tenSanPham;
        public String soLo;
        public java.sql.Date hanSuDung;
        public int soLuongTon;
        public int soNgayConLai;

        public CanhBaoHetHan(String tenSanPham, String soLo, java.sql.Date hanSuDung, int soLuongTon, int soNgayConLai) {
            this.tenSanPham = tenSanPham;
            this.soLo = soLo;
            this.hanSuDung = hanSuDung;
            this.soLuongTon = soLuongTon;
            this.soNgayConLai = soNgayConLai;
        }
    }

    /** Cảnh báo tồn kho thấp */
    public static class CanhBaoTonKho {
        public String tenSanPham;
        public int tongTon;
        public int mucTonToiThieu;

        public CanhBaoTonKho(String tenSanPham, int tongTon, int mucTonToiThieu) {
            this.tenSanPham = tenSanPham;
            this.tongTon = tongTon;
            this.mucTonToiThieu = mucTonToiThieu;
        }
    }

    /** Sản phẩm bán chạy */
    public static class SanPhamBanChay {
        public String tenSanPham;
        public int tongSoLuong;
        public java.math.BigDecimal tongDoanhThu;

        public SanPhamBanChay(String tenSanPham, int tongSoLuong, java.math.BigDecimal tongDoanhThu) {
            this.tenSanPham = tenSanPham;
            this.tongSoLuong = tongSoLuong;
            this.tongDoanhThu = tongDoanhThu;
        }
    }

    /** Doanh thu theo ngày */
    public static class DoanhThuNgay {
        public String ngay;
        public java.math.BigDecimal doanhThu;
        public int soHoaDon;

        public DoanhThuNgay(String ngay, java.math.BigDecimal doanhThu, int soHoaDon) {
            this.ngay = ngay;
            this.doanhThu = doanhThu;
            this.soHoaDon = soHoaDon;
        }
    }

    // ===================== QUERY METHODS =====================

    /**
     * Thống kê doanh thu và số hóa đơn hôm nay
     */
    public ThongKeNgay getThongKeNgay() {
        ThongKeNgay stats = new ThongKeNgay(0, java.math.BigDecimal.ZERO);
        String sql = "SELECT COUNT(*) as SoHoaDon, ISNULL(SUM(TongTien), 0) as DoanhThu " +
                     "FROM HoaDonBan " +
                     "WHERE CAST(NgayBan AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                java.math.BigDecimal dt = rs.getBigDecimal("DoanhThu");
                stats = new ThongKeNgay(
                    rs.getInt("SoHoaDon"),
                    dt != null ? dt : java.math.BigDecimal.ZERO
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Đếm tổng số sản phẩm đang hoạt động
     */
    public int getTongSanPham() {
        String sql = "SELECT COUNT(*) FROM SanPham WHERE DaXoa = 0";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm tổng số lô hàng còn tồn
     */
    public int getTongLoHang() {
        String sql = "SELECT COUNT(*) FROM LoHang WHERE SoLuongTon > 0";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm tổng khách hàng
     */
    public int getTongKhachHang() {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE DaXoa = 0";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm tổng nhà cung cấp
     */
    public int getTongNhaCungCap() {
        String sql = "SELECT COUNT(*) FROM NhaCungCap WHERE DaXoa = 0";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Doanh thu tháng hiện tại
     */
    public java.math.BigDecimal getDoanhThuThang() {
        String sql = "SELECT ISNULL(SUM(TongTien), 0) as DoanhThu FROM HoaDonBan " +
                     "WHERE MONTH(NgayBan) = MONTH(GETDATE()) AND YEAR(NgayBan) = YEAR(GETDATE())";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                java.math.BigDecimal dt = rs.getBigDecimal("DoanhThu");
                return dt != null ? dt : java.math.BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return java.math.BigDecimal.ZERO;
    }

    /**
     * Top N sản phẩm bán chạy nhất (tất cả thời gian)
     */
    public List<SanPhamBanChay> getTopSanPhamBanChay(int top) {
        List<SanPhamBanChay> list = new ArrayList<>();
        String sql = "SELECT TOP (?) sp.TenSanPham, SUM(ct.SoLuong) as TongSoLuong, SUM(ct.ThanhTien) as TongDoanhThu " +
                     "FROM ChiTietHoaDon ct " +
                     "JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                     "GROUP BY sp.MaSanPham, sp.TenSanPham " +
                     "ORDER BY TongSoLuong DESC";

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, top);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SanPhamBanChay(
                    rs.getString("TenSanPham"),
                    rs.getInt("TongSoLuong"),
                    rs.getBigDecimal("TongDoanhThu")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Doanh thu theo 7 ngày gần nhất (cho biểu đồ)
     */
    public List<DoanhThuNgay> getDoanhThu7NgayGanNhat() {
        List<DoanhThuNgay> list = new ArrayList<>();
        String sql = "SELECT FORMAT(d.Ngay, 'dd/MM') AS Ngay, " +
                     "    ISNULL(SUM(h.TongTien), 0) AS DoanhThu, " +
                     "    COUNT(h.MaHoaDon) AS SoHoaDon " +
                     "FROM ( " +
                     "    SELECT CAST(DATEADD(DAY, -v.number, GETDATE()) AS DATE) AS Ngay " +
                     "    FROM master.dbo.spt_values v " +
                     "    WHERE v.type = 'P' AND v.number BETWEEN 0 AND 6 " +
                     ") d " +
                     "LEFT JOIN HoaDonBan h ON CAST(h.NgayBan AS DATE) = d.Ngay " +
                     "GROUP BY d.Ngay " +
                     "ORDER BY d.Ngay ASC";

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new DoanhThuNgay(
                    rs.getString("Ngay"),
                    rs.getBigDecimal("DoanhThu"),
                    rs.getInt("SoHoaDon")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Phân bố sản phẩm theo loại (cho biểu đồ tròn)
     */
    public Map<String, Integer> getPhanBoSanPhamTheoLoai() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT LoaiSanPham, COUNT(*) as SoLuong FROM SanPham WHERE DaXoa = 0 GROUP BY LoaiSanPham ORDER BY SoLuong DESC";
        
        // Map tên tiếng Việt
        Map<String, String> tenLoai = new LinkedHashMap<>();
        tenLoai.put("Thuoc", "Thuốc");
        tenLoai.put("DuocMiPham", "Dược mỹ phẩm");
        tenLoai.put("ThucPhamChucNang", "TPCN");
        tenLoai.put("ChamSocCaNhan", "Chăm sóc CN");
        tenLoai.put("ThietBiYTe", "Thiết bị y tế");

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String loai = rs.getString("LoaiSanPham");
                String tenViet = tenLoai.getOrDefault(loai, loai);
                map.put(tenViet, rs.getInt("SoLuong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * Lấy danh sách lô hàng sắp hết hạn trong khoảng ngày quy định
     */
    public List<CanhBaoHetHan> getThuocSapHetHan(int days) {
        List<CanhBaoHetHan> list = new ArrayList<>();
        String sql = "SELECT sp.TenSanPham, lh.SoLo, lh.HanSuDung, lh.SoLuongTon, " +
                     "DATEDIFF(day, CAST(GETDATE() AS DATE), lh.HanSuDung) as SoNgayConLai " +
                     "FROM LoHang lh " +
                     "JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham " +
                     "WHERE lh.SoLuongTon > 0 AND sp.DaXoa = 0 " +
                     "AND lh.HanSuDung >= CAST(GETDATE() AS DATE) " +
                     "AND DATEDIFF(day, CAST(GETDATE() AS DATE), lh.HanSuDung) <= ? " +
                     "ORDER BY lh.HanSuDung ASC";

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CanhBaoHetHan(
                    rs.getString("TenSanPham"),
                    rs.getString("SoLo"),
                    rs.getDate("HanSuDung"),
                    rs.getInt("SoLuongTon"),
                    rs.getInt("SoNgayConLai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách sản phẩm dưới mức tồn tối thiểu
     */
    public List<CanhBaoTonKho> getThuocCanNhap() {
        List<CanhBaoTonKho> list = new ArrayList<>();
        String sql = "SELECT sp.TenSanPham, COALESCE(SUM(lh.SoLuongTon), 0) as TongTon, sp.MucTonToiThieu " +
                     "FROM SanPham sp " +
                     "LEFT JOIN LoHang lh ON sp.MaSanPham = lh.MaSanPham " +
                     "WHERE sp.DaXoa = 0 " +
                     "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.MucTonToiThieu " +
                     "HAVING COALESCE(SUM(lh.SoLuongTon), 0) <= sp.MucTonToiThieu " +
                     "ORDER BY TongTon ASC";

        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CanhBaoTonKho(
                    rs.getString("TenSanPham"),
                    rs.getInt("TongTon"),
                    rs.getInt("MucTonToiThieu")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm số lô đã hết hạn nhưng còn tồn
     */
    public int getSoLoHetHan() {
        String sql = "SELECT COUNT(*) FROM LoHang lh JOIN SanPham sp ON lh.MaSanPham = sp.MaSanPham " +
                     "WHERE sp.DaXoa = 0 AND lh.SoLuongTon > 0 AND lh.HanSuDung < CAST(GETDATE() AS DATE)";
        try (Connection con = ConnectDB.getCon();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
