package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class cho bảng SanPham (Dashboard cốt lõi - 7 trường)
 */
public class SanPham {
	private int maSanPham;
	private String tenSanPham;
	private String donViTinh;
	private BigDecimal giaBanDeXuat;
	private String loaiSanPham;
	private String moTa;
	private int mucTonToiThieu;

	// View/Transient properties liên kết từ LoHang
	private int tongTon; 
	private LocalDate hanSuDungGanNhat;
	private String loaiHinhBan; 
	
	// Dấu vết hệ thống
	private boolean daXoa;
	private LocalDateTime ngayTao;

	public SanPham() {}

	public SanPham(int maSanPham, String tenSanPham, String donViTinh, BigDecimal giaBanDeXuat, String loaiSanPham, String moTa,
			int mucTonToiThieu, int tongTon, LocalDate hanSuDungGanNhat, String loaiHinhBan, boolean daXoa, LocalDateTime ngayTao) {
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
		this.donViTinh = donViTinh;
		this.giaBanDeXuat = giaBanDeXuat;
		this.loaiSanPham = loaiSanPham;
		this.moTa = moTa;
		this.mucTonToiThieu = mucTonToiThieu;
		this.tongTon = tongTon;
		this.hanSuDungGanNhat = hanSuDungGanNhat;
		this.loaiHinhBan = loaiHinhBan;
		this.daXoa = daXoa;
		this.ngayTao = ngayTao;
	}

	public int getMaSanPham() { return maSanPham; }
	public void setMaSanPham(int maSanPham) { this.maSanPham = maSanPham; }

	public String getTenSanPham() { return tenSanPham; }
	public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

	public BigDecimal getGiaBanDeXuat() { return giaBanDeXuat; }
	public void setGiaBanDeXuat(BigDecimal giaBanDeXuat) { this.giaBanDeXuat = giaBanDeXuat; }

	public String getDonViTinh() { return donViTinh; }
	public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }

	public String getLoaiSanPham() { return loaiSanPham; }
	public void setLoaiSanPham(String loaiSanPham) { this.loaiSanPham = loaiSanPham; }

	public String getMoTa() { return moTa; }
	public void setMoTa(String moTa) { this.moTa = moTa; }

	public int getMucTonToiThieu() { return mucTonToiThieu; }
	public void setMucTonToiThieu(int mucTonToiThieu) { this.mucTonToiThieu = mucTonToiThieu; }

	public int getTongTon() { return tongTon; }
	public void setTongTon(int tongTon) { this.tongTon = tongTon; }

	public LocalDate getHanSuDungGanNhat() { return hanSuDungGanNhat; }
	public void setHanSuDungGanNhat(LocalDate hanSuDungGanNhat) { this.hanSuDungGanNhat = hanSuDungGanNhat; }

	public String getLoaiHinhBan() { return loaiHinhBan; }
	public void setLoaiHinhBan(String loaiHinhBan) { this.loaiHinhBan = loaiHinhBan; }

	public boolean isDaXoa() { return daXoa; }
	public void setDaXoa(boolean daXoa) { this.daXoa = daXoa; }

	public LocalDateTime getNgayTao() { return ngayTao; }
	public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

	@Override
	public String toString() {
		return tenSanPham;
	}
}
