package service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.logging.Logger;

import common.AppLogger;
import common.ConnectDB;
import service.dto.ReportFilter;
import service.dto.ReportFilter.*;

/**
 * ReportService – Báo cáo nâng cao cho nhà thuốc.
 *
 * Cung cấp:
 * - Báo cáo doanh thu theo khoảng ngày
 * - Báo cáo tồn kho tổng hợp
 * - Báo cáo nhập hàng
 * - Top sản phẩm bán chạy
 *
 * @version 1.0
 */
public class ReportService {

    private static final Logger LOG = AppLogger.get(ReportService.class);

    // ================================================================
    //  DOANH THU THEO NGÀY
    // ================================================================

    public ReportResult getRevenueReport(ReportFilter filter) {
        var result = new ReportResult();
        result.title = "Báo cáo doanh thu";
        result.period = filter.getTuNgay() + " → " + filter.getDenNgay();
        var rows = new ArrayList<RevenueByDateRow>();

        String sql = "SELECT FORMAT(hd.NgayBan, 'yyyy-MM-dd') AS Ngay, " +
                     "COUNT(*) AS SoHoaDon, ISNULL(SUM(hd.TongTien), 0) AS DoanhThu " +
                     "FROM HoaDonBan hd WHERE 1=1 ";
        var params = new ArrayList<Object>();

        if (filter.getTuNgay() != null) { sql += "AND hd.NgayBan >= ? "; params.add(filter.getTuNgay()); }
        if (filter.getDenNgay() != null) { sql += "AND hd.NgayBan < DATEADD(DAY,1,CAST(? AS DATE)) "; params.add(filter.getDenNgay()); }
        sql += "GROUP BY FORMAT(hd.NgayBan, 'yyyy-MM-dd') ORDER BY Ngay";

        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            var rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new RevenueByDateRow(rs.getString("Ngay"), rs.getInt("SoHoaDon"), rs.getBigDecimal("DoanhThu")));
                result.tongDoanhThu = result.tongDoanhThu.add(rs.getBigDecimal("DoanhThu"));
                result.tongHoaDon += rs.getInt("SoHoaDon");
            }
        } catch (Exception e) {
            LOG.severe("Revenue report error: " + e.getMessage());
        }
        result.revenueByDate = rows;
        LOG.info("[REPORT] Revenue: " + result.period + " | " + result.tongHoaDon + " hóa đơn");
        return result;
    }

    // ================================================================
    //  TỒN KHO TỔNG HỢP
    // ================================================================

    public ReportResult getInventoryReport() {
        var result = new ReportResult();
        result.title = "Báo cáo tồn kho";
        var rows = new ArrayList<InventoryRow>();

        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.DonViTinh, " +
                     "ISNULL(SUM(lh.SoLuongTon), 0) AS TongTon, sp.MucTonToiThieu, " +
                     "ISNULL(SUM(lh.SoLuongTon), 0) * sp.GiaBanDeXuat AS GiaTriTon " +
                     "FROM SanPham sp LEFT JOIN LoHang lh ON sp.MaSanPham = lh.MaSanPham " +
                     "WHERE sp.DaXoa = 0 " +
                     "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.DonViTinh, sp.MucTonToiThieu, sp.GiaBanDeXuat " +
                     "ORDER BY sp.TenSanPham";

        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql); var rs = ps.executeQuery()) {
            while (rs.next()) {
                int ton = rs.getInt("TongTon");
                int min = rs.getInt("MucTonToiThieu");
                String tt = ton == 0 ? "Hết hàng" : (ton <= min ? "Tồn thấp" : "Đủ hàng");
                rows.add(new InventoryRow(rs.getInt("MaSanPham"), rs.getString("TenSanPham"),
                    rs.getString("DonViTinh"), ton, min, rs.getBigDecimal("GiaTriTon"), tt));
            }
        } catch (Exception e) {
            LOG.severe("Inventory report error: " + e.getMessage());
        }
        result.inventory = rows;
        LOG.info("[REPORT] Inventory: " + rows.size() + " sản phẩm");
        return result;
    }

    // ================================================================
    //  TOP SẢN PHẨM BÁN CHẠY
    // ================================================================

    public ReportResult getTopSellingReport(ReportFilter filter) {
        var result = new ReportResult();
        result.title = "Top sản phẩm bán chạy";
        result.period = filter.getTuNgay() + " → " + filter.getDenNgay();
        var rows = new ArrayList<TopSellingRow>();

        String sql = "SELECT TOP (?) sp.TenSanPham, SUM(ct.SoLuong) AS SoLuongBan, " +
                     "SUM(ct.ThanhTien) AS DoanhThu " +
                     "FROM ChiTietHoaDon ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                     "JOIN HoaDonBan hd ON ct.MaHoaDon = hd.MaHoaDon WHERE 1=1 ";
        var params = new ArrayList<Object>();
        params.add(filter.getTopN());

        if (filter.getTuNgay() != null) { sql += "AND hd.NgayBan >= ? "; params.add(filter.getTuNgay()); }
        if (filter.getDenNgay() != null) { sql += "AND hd.NgayBan < DATEADD(DAY,1,CAST(? AS DATE)) "; params.add(filter.getDenNgay()); }
        sql += "GROUP BY sp.MaSanPham, sp.TenSanPham ORDER BY SoLuongBan DESC";

        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            var rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                rows.add(new TopSellingRow(rank++, rs.getString("TenSanPham"),
                    rs.getInt("SoLuongBan"), rs.getBigDecimal("DoanhThu")));
            }
        } catch (Exception e) {
            LOG.severe("Top selling report error: " + e.getMessage());
        }
        result.topSelling = rows;
        LOG.info("[REPORT] TopSelling: " + rows.size() + " items");
        return result;
    }

    // ================================================================
    //  NHẬP HÀNG
    // ================================================================

    public ReportResult getPurchaseReport(ReportFilter filter) {
        var result = new ReportResult();
        result.title = "Báo cáo nhập hàng";
        result.period = filter.getTuNgay() + " → " + filter.getDenNgay();
        var rows = new ArrayList<PurchaseRow>();

        String sql = "SELECT pn.MaPhieuNhap, FORMAT(pn.NgayNhap, 'dd/MM/yyyy') AS NgayNhap, " +
                     "ISNULL(ncc.TenNCC, N'Không rõ') AS NhaCungCap, " +
                     "(SELECT COUNT(*) FROM ChiTietPhieuNhap ct WHERE ct.MaPhieuNhap = pn.MaPhieuNhap) AS SoMatHang, " +
                     "pn.TongTien FROM PhieuNhap pn " +
                     "LEFT JOIN NhaCungCap ncc ON pn.MaNCC = ncc.MaNCC WHERE 1=1 ";
        var params = new ArrayList<Object>();

        if (filter.getTuNgay() != null) { sql += "AND pn.NgayNhap >= ? "; params.add(filter.getTuNgay()); }
        if (filter.getDenNgay() != null) { sql += "AND pn.NgayNhap < DATEADD(DAY,1,CAST(? AS DATE)) "; params.add(filter.getDenNgay()); }
        sql += "ORDER BY pn.NgayNhap DESC";

        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            var rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new PurchaseRow(rs.getInt("MaPhieuNhap"), rs.getString("NgayNhap"),
                    rs.getString("NhaCungCap"), rs.getInt("SoMatHang"), rs.getBigDecimal("TongTien")));
                if (rs.getBigDecimal("TongTien") != null)
                    result.tongDoanhThu = result.tongDoanhThu.add(rs.getBigDecimal("TongTien"));
            }
        } catch (Exception e) {
            LOG.severe("Purchase report error: " + e.getMessage());
        }
        result.purchases = rows;
        return result;
    }
}
