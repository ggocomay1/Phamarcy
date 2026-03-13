package service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.ConnectDB;

/**
 * HoaDonService – Service layer cho module Lịch sử hóa đơn.
 *
 * Chịu trách nhiệm:
 * - Tìm kiếm / lọc hóa đơn theo ngày, nhân viên, trạng thái, keyword
 * - Lấy chi tiết hóa đơn
 * - Lấy danh sách nhân viên đã bán hàng
 *
 * Panel chỉ gọi service, không viết SQL trực tiếp.
 *
 * @version 1.0
 */
public class HoaDonService {

    // ================================================================
    //  DTO nội bộ
    // ================================================================

    /** DTO cho mỗi dòng trong bảng lịch sử hóa đơn */
    public static class InvoiceRow {
        public final int maHoaDon;
        public final LocalDateTime ngayBan;
        public final String tenNhanVien;
        public final String tenKhachHang;
        public final String sdtKhachHang;
        public final int soMatHang;
        public final BigDecimal tongTien;
        public final String phuongThucTT;
        public final String trangThai;

        public InvoiceRow(int maHD, LocalDateTime ngayBan, String tenNV, String tenKH,
                          String sdt, int soMH, BigDecimal tt, String pttt, String tt2) {
            this.maHoaDon = maHD;
            this.ngayBan = ngayBan;
            this.tenNhanVien = tenNV;
            this.tenKhachHang = tenKH;
            this.sdtKhachHang = sdt;
            this.soMatHang = soMH;
            this.tongTien = tt;
            this.phuongThucTT = pttt;
            this.trangThai = tt2;
        }

        /** Mã HĐ hiển thị */
        public String getMaHDDisplay() {
            return "HD" + String.format("%04d", maHoaDon);
        }
    }

    /** DTO cho mỗi dòng chi tiết hóa đơn */
    public static class InvoiceDetailRow {
        public final int maCTHD;
        public final String tenSanPham;
        public final String soLo;
        public final String donViTinh;
        public final int soLuong;
        public final BigDecimal giaBan;
        public final BigDecimal thanhTien;

        public InvoiceDetailRow(int maCTHD, String tenSP, String soLo, String dvt,
                                int sl, BigDecimal gia, BigDecimal tt) {
            this.maCTHD = maCTHD;
            this.tenSanPham = tenSP;
            this.soLo = soLo;
            this.donViTinh = dvt;
            this.soLuong = sl;
            this.giaBan = gia;
            this.thanhTien = tt;
        }
    }

    /** Tham số lọc hóa đơn */
    public static class InvoiceFilter {
        public String tuNgaySql;    // yyyy-MM-dd
        public String denNgaySql;   // yyyy-MM-dd
        public String nhanVien;     // null or "Tất cả" = không lọc
        public String trangThai;    // "Tất cả", "Hoàn thành", "Đã hủy"
        public String keyword;      // mã HĐ, tên KH, SĐT

        public InvoiceFilter() {}

        public boolean hasNhanVien() {
            return nhanVien != null && !"Tất cả".equals(nhanVien);
        }
        public boolean hasTrangThai() {
            return trangThai != null && !"Tất cả".equals(trangThai);
        }
        public boolean hasKeyword() {
            return keyword != null && !keyword.trim().isEmpty();
        }
    }

    // ================================================================
    //  NHÂN VIÊN
    // ================================================================

    /**
     * Lấy danh sách nhân viên đã từng bán hàng.
     */
    public List<String> getActiveStaffNames() {
        var names = new ArrayList<String>();
        try (var con = ConnectDB.getCon();
             var stmt = con.createStatement();
             var rs = stmt.executeQuery(
                 "SELECT DISTINCT nd.HoTen FROM NguoiDung nd " +
                 "JOIN HoaDonBan hd ON hd.MaNguoiDung = nd.MaNguoiDung " +
                 "WHERE nd.DaXoa = 0 ORDER BY nd.HoTen"
             )) {
            while (rs.next()) {
                names.add(rs.getString("HoTen"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return names;
    }

    // ================================================================
    //  DANH SÁCH HÓA ĐƠN
    // ================================================================

    /**
     * Tìm kiếm hóa đơn theo filter.
     * @param filter điều kiện lọc (có thể null = lấy tất cả)
     * @return danh sách InvoiceRow
     */
    public List<InvoiceRow> searchInvoices(InvoiceFilter filter) {
        var results = new ArrayList<InvoiceRow>();
        var sql = new StringBuilder();
        sql.append("SELECT hd.MaHoaDon, hd.NgayBan, nd.HoTen AS TenNhanVien, ");
        sql.append("ISNULL(kh.HoTen, N'Khách lẻ') AS TenKhachHang, ");
        sql.append("kh.SoDienThoai AS SoDienThoaiKH, ");
        sql.append("(SELECT COUNT(DISTINCT cthd.MaSanPham) FROM ChiTietHoaDon cthd WHERE cthd.MaHoaDon = hd.MaHoaDon) AS SoMatHang, ");
        sql.append("hd.TongTien, ");
        sql.append("ISNULL(hd.GhiChu, N'Tiền mặt') AS PhuongThucTT, ");
        sql.append("N'Hoàn thành' AS TrangThai ");
        sql.append("FROM HoaDonBan hd ");
        sql.append("JOIN NguoiDung nd ON hd.MaNguoiDung = nd.MaNguoiDung ");
        sql.append("LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang ");
        sql.append("WHERE 1=1 ");

        var params = new ArrayList<Object>();

        if (filter != null) {
            if (filter.tuNgaySql != null && !filter.tuNgaySql.isEmpty()) {
                sql.append("AND hd.NgayBan >= ? ");
                params.add(filter.tuNgaySql);
            }
            if (filter.denNgaySql != null && !filter.denNgaySql.isEmpty()) {
                sql.append("AND hd.NgayBan < DATEADD(DAY, 1, CAST(? AS DATE)) ");
                params.add(filter.denNgaySql);
            }
            if (filter.hasNhanVien()) {
                sql.append("AND nd.HoTen = ? ");
                params.add(filter.nhanVien);
            }
            if (filter.hasKeyword()) {
                sql.append("AND (CAST(hd.MaHoaDon AS NVARCHAR) LIKE ? OR ISNULL(kh.HoTen,'') LIKE ? OR ISNULL(kh.SoDienThoai,'') LIKE ?) ");
                String kw = "%" + filter.keyword.trim() + "%";
                params.add(kw);
                params.add(kw);
                params.add(kw);
            }
        }

        sql.append("ORDER BY hd.NgayBan DESC");

        try (var con = ConnectDB.getCon();
             var ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            var rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("NgayBan");
                LocalDateTime ngayBan = ts != null ? ts.toLocalDateTime() : null;
                String ptttRaw = rs.getString("PhuongThucTT");

                results.add(new InvoiceRow(
                    rs.getInt("MaHoaDon"),
                    ngayBan,
                    rs.getString("TenNhanVien"),
                    rs.getString("TenKhachHang"),
                    rs.getString("SoDienThoaiKH"),
                    rs.getInt("SoMatHang"),
                    rs.getBigDecimal("TongTien"),
                    formatPaymentMethod(ptttRaw),
                    rs.getString("TrangThai")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // ================================================================
    //  CHI TIẾT HÓA ĐƠN
    // ================================================================

    /**
     * Lấy chi tiết hóa đơn theo mã HĐ.
     */
    public List<InvoiceDetailRow> getInvoiceDetails(int maHoaDon) {
        var results = new ArrayList<InvoiceDetailRow>();
        try (var con = ConnectDB.getCon();
             var ps = con.prepareStatement(
                 "SELECT cthd.MaCTHD, sp.TenSanPham, lh.SoLo, sp.DonViTinh, " +
                 "cthd.SoLuong, cthd.GiaBan, cthd.ThanhTien " +
                 "FROM ChiTietHoaDon cthd " +
                 "JOIN SanPham sp ON sp.MaSanPham = cthd.MaSanPham " +
                 "JOIN LoHang lh ON lh.MaLoHang = cthd.MaLoHang " +
                 "WHERE cthd.MaHoaDon = ? ORDER BY cthd.MaCTHD"
             )) {
            ps.setInt(1, maHoaDon);
            var rs = ps.executeQuery();
            while (rs.next()) {
                results.add(new InvoiceDetailRow(
                    rs.getInt("MaCTHD"),
                    rs.getString("TenSanPham"),
                    rs.getString("SoLo"),
                    rs.getString("DonViTinh"),
                    rs.getInt("SoLuong"),
                    rs.getBigDecimal("GiaBan"),
                    rs.getBigDecimal("ThanhTien")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    // ================================================================
    //  HELPER
    // ================================================================

    /**
     * Chuẩn hóa phương thức thanh toán từ GhiChu DB.
     */
    private String formatPaymentMethod(String raw) {
        if (raw == null) return "Tiền mặt";
        String lower = raw.toLowerCase();
        if (lower.contains("chuyển khoản") || lower.contains("ck")) return "Chuyển khoản";
        if (lower.contains("tiền mặt") || lower.contains("cash")) return "Tiền mặt";
        return raw;
    }
}
