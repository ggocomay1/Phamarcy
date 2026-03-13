package service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * SaleCartItem – Một dòng hàng trong giỏ POS.
 * Đây là model nghiệp vụ, không phải entity DB.
 */
public class SaleCartItem {
    private int maCTHD;
    private int maSanPham;
    private String tenSanPham;
    private String donViTinh;
    private int maLoHang;
    private String soLo;
    private LocalDate hanSuDung;
    private BigDecimal donGia;
    private int soLuong;
    private BigDecimal thanhTien;
    private int tonKhaDung;

    // ── Getters / Setters ──
    public int getMaCTHD() { return maCTHD; }
    public void setMaCTHD(int v) { this.maCTHD = v; }

    public int getMaSanPham() { return maSanPham; }
    public void setMaSanPham(int v) { this.maSanPham = v; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String v) { this.tenSanPham = v; }

    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String v) { this.donViTinh = v; }

    public int getMaLoHang() { return maLoHang; }
    public void setMaLoHang(int v) { this.maLoHang = v; }

    public String getSoLo() { return soLo; }
    public void setSoLo(String v) { this.soLo = v; }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate v) { this.hanSuDung = v; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal v) { this.donGia = v; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int v) { this.soLuong = v; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal v) { this.thanhTien = v; }

    public int getTonKhaDung() { return tonKhaDung; }
    public void setTonKhaDung(int v) { this.tonKhaDung = v; }
}
