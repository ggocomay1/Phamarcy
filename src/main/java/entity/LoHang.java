package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class cho bảng LoHang
 * 
 * @author Generated
 * @version 1.0
 */
public class LoHang {
	private int maLoHang;
	private int maSanPham;
	private String soLo;
	private Integer maNCC;
	private Integer maPhieuNhap;
	private LocalDate ngaySanXuat;
	private LocalDate hanSuDung;
	private BigDecimal giaNhap;
	private int soLuongNhap;
	private int soLuongTon;
	private LocalDateTime ngayNhap;
	private String trangThai; // Đang bán, Ngưng bán, Hết hàng
	private String donViNhap;
	private String loaiHinhBan; // Bán sỉ, Bán lẻ
	private LocalDateTime thoiGianNhap;
	private int soViTrenHop;  // Số vỉ trong 1 hộp
	private int soVienTrenVi; // Số viên trong 1 vỉ
	private int tongSoVien;   // Tổng số viên = Hộp * Vỉ/Hộp * Viên/Vỉ
	private String tenSanPham; // Transient field for display
	private String tenNhaCungCap; // Transient field for display

	public LoHang() {
	}

	public LoHang(int maLoHang, int maSanPham, String soLo,
			Integer maNCC, Integer maPhieuNhap, LocalDate ngaySanXuat,
			LocalDate hanSuDung, BigDecimal giaNhap, int soLuongNhap,
			int soLuongTon, LocalDateTime ngayNhap, String trangThai, String donViNhap,
			String loaiHinhBan, LocalDateTime thoiGianNhap, int soViTrenHop, int soVienTrenVi, int tongSoVien) {
		this.maLoHang = maLoHang;
		this.maSanPham = maSanPham;
		this.soLo = soLo;
		this.maNCC = maNCC;
		this.maPhieuNhap = maPhieuNhap;
		this.ngaySanXuat = ngaySanXuat;
		this.hanSuDung = hanSuDung;
		this.giaNhap = giaNhap;
		this.soLuongNhap = soLuongNhap;
		this.soLuongTon = soLuongTon;
		this.ngayNhap = ngayNhap;
		this.trangThai = trangThai;
		this.donViNhap = donViNhap;
		this.loaiHinhBan = loaiHinhBan;
		this.thoiGianNhap = thoiGianNhap;
		this.soViTrenHop = soViTrenHop;
		this.soVienTrenVi = soVienTrenVi;
		this.tongSoVien = tongSoVien;
	}

	// Getters and Setters
	public int getMaLoHang() {
		return maLoHang;
	}

	public void setMaLoHang(int maLoHang) {
		this.maLoHang = maLoHang;
	}

	public int getMaSanPham() {
		return maSanPham;
	}

	public void setMaSanPham(int maSanPham) {
		this.maSanPham = maSanPham;
	}

	public String getSoLo() {
		return soLo;
	}

	public void setSoLo(String soLo) {
		this.soLo = soLo;
	}

	public Integer getMaNCC() {
		return maNCC;
	}

	public void setMaNCC(Integer maNCC) {
		this.maNCC = maNCC;
	}

	public Integer getMaPhieuNhap() {
		return maPhieuNhap;
	}

	public void setMaPhieuNhap(Integer maPhieuNhap) {
		this.maPhieuNhap = maPhieuNhap;
	}

	public LocalDate getNgaySanXuat() {
		return ngaySanXuat;
	}

	public void setNgaySanXuat(LocalDate ngaySanXuat) {
		this.ngaySanXuat = ngaySanXuat;
	}

	public LocalDate getHanSuDung() {
		return hanSuDung;
	}

	public void setHanSuDung(LocalDate hanSuDung) {
		this.hanSuDung = hanSuDung;
	}

	public BigDecimal getGiaNhap() {
		return giaNhap;
	}

	public void setGiaNhap(BigDecimal giaNhap) {
		this.giaNhap = giaNhap;
	}

	public int getSoLuongNhap() {
		return soLuongNhap;
	}

	public void setSoLuongNhap(int soLuongNhap) {
		this.soLuongNhap = soLuongNhap;
	}

	public int getSoLuongTon() {
		return soLuongTon;
	}

	public void setSoLuongTon(int soLuongTon) {
		this.soLuongTon = soLuongTon;
	}

	public LocalDateTime getNgayNhap() {
		return ngayNhap;
	}

	public void setNgayNhap(LocalDateTime ngayNhap) {
		this.ngayNhap = ngayNhap;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public String getDonViNhap() {
		return donViNhap;
	}

	public void setDonViNhap(String donViNhap) {
		this.donViNhap = donViNhap;
	}

	public int getSoViTrenHop() {
		return soViTrenHop;
	}

	public void setSoViTrenHop(int soViTrenHop) {
		this.soViTrenHop = soViTrenHop;
	}

	public int getSoVienTrenVi() {
		return soVienTrenVi;
	}

	public void setSoVienTrenVi(int soVienTrenVi) {
		this.soVienTrenVi = soVienTrenVi;
	}

	public int getTongSoVien() {
		return tongSoVien;
	}

	public void setTongSoVien(int tongSoVien) {
		this.tongSoVien = tongSoVien;
	}

	public String getLoaiHinhBan() {
		return loaiHinhBan;
	}

	public void setLoaiHinhBan(String loaiHinhBan) {
		this.loaiHinhBan = loaiHinhBan;
	}

	public LocalDateTime getThoiGianNhap() {
		return thoiGianNhap;
	}

	public void setThoiGianNhap(LocalDateTime thoiGianNhap) {
		this.thoiGianNhap = thoiGianNhap;
	}

	public String getTenSanPham() {
		return tenSanPham;
	}

	public void setTenSanPham(String tenSanPham) {
		this.tenSanPham = tenSanPham;
	}

	public String getTenNhaCungCap() {
		return tenNhaCungCap;
	}

	public void setTenNhaCungCap(String tenNhaCungCap) {
		this.tenNhaCungCap = tenNhaCungCap;
	}

	@Override
	public String toString() {
		return "LoHang [maLoHang=" + maLoHang
			+ ", maSanPham=" + maSanPham
			+ ", soLo=" + soLo + ", hanSuDung=" + hanSuDung + "]";
	}
}
