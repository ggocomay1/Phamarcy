package service.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * ReportFilter – Tham số lọc cho các báo cáo nâng cao.
 */
public class ReportFilter {
    private String tuNgay;      // yyyy-MM-dd
    private String denNgay;     // yyyy-MM-dd
    private String loaiBaoCao;  // "doanh_thu", "ton_kho", "nhap_hang", "ban_chay"
    private int topN = 10;

    public String getTuNgay() { return tuNgay; }
    public void setTuNgay(String v) { this.tuNgay = v; }
    public String getDenNgay() { return denNgay; }
    public void setDenNgay(String v) { this.denNgay = v; }
    public String getLoaiBaoCao() { return loaiBaoCao; }
    public void setLoaiBaoCao(String v) { this.loaiBaoCao = v; }
    public int getTopN() { return topN; }
    public void setTopN(int v) { this.topN = v; }

    // ── Nested result DTOs ──

    /** Doanh thu theo ngày */
    public static class RevenueByDateRow {
        public final String ngay;
        public final int soHoaDon;
        public final BigDecimal doanhThu;
        public RevenueByDateRow(String ngay, int shd, BigDecimal dt) {
            this.ngay = ngay; this.soHoaDon = shd; this.doanhThu = dt;
        }
    }

    /** Tồn kho tổng hợp */
    public static class InventoryRow {
        public final int maSanPham;
        public final String tenSanPham;
        public final String donViTinh;
        public final int tongTon;
        public final int mucToiThieu;
        public final BigDecimal giaTriTon; // tongTon * giaBan
        public final String trangThai;
        public InventoryRow(int ma, String ten, String dvt, int ton, int min, BigDecimal gt, String tt) {
            this.maSanPham = ma; this.tenSanPham = ten; this.donViTinh = dvt;
            this.tongTon = ton; this.mucToiThieu = min; this.giaTriTon = gt; this.trangThai = tt;
        }
    }

    /** Nhập hàng tổng hợp */
    public static class PurchaseRow {
        public final int maPhieuNhap;
        public final String ngayNhap;
        public final String nhaCungCap;
        public final int soMatHang;
        public final BigDecimal tongTien;
        public PurchaseRow(int ma, String ngay, String ncc, int smh, BigDecimal tt) {
            this.maPhieuNhap = ma; this.ngayNhap = ngay; this.nhaCungCap = ncc;
            this.soMatHang = smh; this.tongTien = tt;
        }
    }

    /** Sản phẩm bán chạy (khoảng thời gian) */
    public static class TopSellingRow {
        public final int rank;
        public final String tenSanPham;
        public final int soLuongBan;
        public final BigDecimal doanhThu;
        public TopSellingRow(int rank, String ten, int sl, BigDecimal dt) {
            this.rank = rank; this.tenSanPham = ten; this.soLuongBan = sl; this.doanhThu = dt;
        }
    }

    /** Kết quả báo cáo tổng hợp */
    public static class ReportResult {
        public String title;
        public String period;
        public BigDecimal tongDoanhThu = BigDecimal.ZERO;
        public int tongHoaDon;
        public List<RevenueByDateRow> revenueByDate = List.of();
        public List<InventoryRow> inventory = List.of();
        public List<PurchaseRow> purchases = List.of();
        public List<TopSellingRow> topSelling = List.of();
    }
}
