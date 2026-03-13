package service.dto;

import java.math.BigDecimal;

/**
 * SaleSummary – Tóm tắt tổng kết giỏ hàng POS.
 * Không phải entity DB, chỉ là view model cho UI.
 */
public class SaleSummary {
    private int soMatHang;
    private int tongSoLuong;
    private BigDecimal tamTinh;
    private BigDecimal tongThanhToan;

    public SaleSummary() {
        this.tamTinh = BigDecimal.ZERO;
        this.tongThanhToan = BigDecimal.ZERO;
    }

    public int getSoMatHang() { return soMatHang; }
    public void setSoMatHang(int v) { this.soMatHang = v; }

    public int getTongSoLuong() { return tongSoLuong; }
    public void setTongSoLuong(int v) { this.tongSoLuong = v; }

    public BigDecimal getTamTinh() { return tamTinh; }
    public void setTamTinh(BigDecimal v) { this.tamTinh = v; }

    public BigDecimal getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(BigDecimal v) { this.tongThanhToan = v; }
}
