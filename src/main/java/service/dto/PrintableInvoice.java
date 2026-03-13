package service.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * PrintableInvoice – DTO chứa toàn bộ dữ liệu cần in cho 1 hóa đơn.
 */
public class PrintableInvoice {
    // Header
    public String maHoaDon;
    public String ngayBan;
    public String nhanVien;
    public String khachHang;
    public String soDienThoai;
    public String phuongThucTT;

    // Lines
    public List<PrintLine> lines = new ArrayList<>();

    // Footer
    public BigDecimal tamTinh = BigDecimal.ZERO;
    public BigDecimal tongTien = BigDecimal.ZERO;
    public BigDecimal tienKhachDua = BigDecimal.ZERO;
    public BigDecimal tienThua = BigDecimal.ZERO;
    public int soMatHang;

    // Store info
    public String tenCuaHang = "MEPHAR - NHÀ THUỐC";
    public String diaChi = "";
    public String soDienThoaiCuaHang = "";

    public static class PrintLine {
        public int stt;
        public String tenSanPham;
        public String donViTinh;
        public int soLuong;
        public BigDecimal donGia;
        public BigDecimal thanhTien;

        public PrintLine(int stt, String ten, String dvt, int sl, BigDecimal dg, BigDecimal tt) {
            this.stt = stt; this.tenSanPham = ten; this.donViTinh = dvt;
            this.soLuong = sl; this.donGia = dg; this.thanhTien = tt;
        }
    }
}
