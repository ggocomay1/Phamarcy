package service.dto;

import java.math.BigDecimal;

/**
 * CheckoutRequest – Dữ liệu panel gửi đến service khi thanh toán.
 */
public class CheckoutRequest {
    private int maHoaDon;
    private int maNguoiDung;
    private Integer maKhachHang;         // null = khách lẻ
    private String phuongThucThanhToan;  // "Tiền mặt" / "Chuyển khoản"
    private BigDecimal tongTien;
    private BigDecimal tienKhachDua;     // null nếu chuyển khoản
    private boolean xacNhanChuyenKhoan;  // true nếu CK đã xác nhận
    private String ghiChu;

    // ── Getters / Setters ──
    public int getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(int v) { this.maHoaDon = v; }

    public int getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(int v) { this.maNguoiDung = v; }

    public Integer getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(Integer v) { this.maKhachHang = v; }

    public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(String v) { this.phuongThucThanhToan = v; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal v) { this.tongTien = v; }

    public BigDecimal getTienKhachDua() { return tienKhachDua; }
    public void setTienKhachDua(BigDecimal v) { this.tienKhachDua = v; }

    public boolean isXacNhanChuyenKhoan() { return xacNhanChuyenKhoan; }
    public void setXacNhanChuyenKhoan(boolean v) { this.xacNhanChuyenKhoan = v; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String v) { this.ghiChu = v; }
}
