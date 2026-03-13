package service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DashboardSummaryDTO – Toàn bộ dữ liệu Dashboard gom lại từ 1 điểm.
 * DashboardPanel chỉ gọi service.getDashboardSummary() rồi bind.
 */
public class DashboardSummaryDTO {

    // ── KPI ──
    public int hoaDonHomNay;
    public BigDecimal doanhThuHomNay = BigDecimal.ZERO;
    public BigDecimal doanhThuHomQua = BigDecimal.ZERO;
    public int tongSanPham;
    public int soSanPhamSapHet;
    public int soLoSapHetHan;
    public int tongKhachHang;

    // ── Alert tables ──
    public List<LowStockRow> lowStockItems = List.of();
    public List<ExpiringBatchRow> expiringBatches = List.of();

    // ── Recent invoices ──
    public List<RecentInvoiceRow> recentInvoices = List.of();

    // ── Stats ──
    public List<TopSellingRow> topSelling = List.of();
    public Map<String, Integer> inventoryStatus = Map.of();

    // ── Nested row DTOs ──

    public static class LowStockRow {
        public final String tenSanPham;
        public final String donViTinh;
        public final int tonHienTai;
        public final int mucToiThieu;

        public LowStockRow(String ten, String dvt, int ton, int min) {
            this.tenSanPham = ten; this.donViTinh = dvt;
            this.tonHienTai = ton; this.mucToiThieu = min;
        }
    }

    public static class ExpiringBatchRow {
        public final String tenSanPham;
        public final String soLo;
        public final String hanSuDung;
        public final int soLuong;
        public final long soNgayConLai;

        public ExpiringBatchRow(String ten, String lo, String hsd, int sl, long ngay) {
            this.tenSanPham = ten; this.soLo = lo; this.hanSuDung = hsd;
            this.soLuong = sl; this.soNgayConLai = ngay;
        }
    }

    public static class RecentInvoiceRow {
        public final int maHoaDon;
        public final String thoiGian;
        public final String nhanVien;
        public final String khachHang;
        public final BigDecimal tongTien;
        public final String trangThai;

        public RecentInvoiceRow(int ma, String t, String nv, String kh, BigDecimal tt, String tt2) {
            this.maHoaDon = ma; this.thoiGian = t; this.nhanVien = nv;
            this.khachHang = kh; this.tongTien = tt; this.trangThai = tt2;
        }
    }

    public static class TopSellingRow {
        public final String tenSanPham;
        public final int soLuongBan;
        public final BigDecimal doanhThu;

        public TopSellingRow(String ten, int sl, BigDecimal dt) {
            this.tenSanPham = ten; this.soLuongBan = sl; this.doanhThu = dt;
        }
    }
}
