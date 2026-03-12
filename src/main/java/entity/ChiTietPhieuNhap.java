package entity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class cho bảng ChiTietPhieuNhap
 * 
 * @author Generated
 * @version 1.0
 */
public class ChiTietPhieuNhap {
	private int maCTPN;
	private int maPhieuNhap;
	private int maSanPham;
	private String soLo;
	private LocalDate hanSuDung;
	private BigDecimal giaNhap;
	private int soLuong;
	private BigDecimal thanhTien;
	private String donViNhap;
	private String loaiHinhBan;
	private int soViTrenHop;
	private int soVienTrenVi;
	private int tongSoVien;
	private boolean mergeBatch; // true: Cộng dồn, false: Tạo mới dòng riêng

	public ChiTietPhieuNhap() {
	}

	public ChiTietPhieuNhap(int maCTPN, int maPhieuNhap, int maSanPham,
			String soLo, LocalDate hanSuDung, BigDecimal giaNhap,
			int soLuong, BigDecimal thanhTien, String donViNhap, String loaiHinhBan,
			int soViTrenHop, int soVienTrenVi, int tongSoVien) {
		this.maCTPN = maCTPN;
		this.maPhieuNhap = maPhieuNhap;
		this.maSanPham = maSanPham;
		this.soLo = soLo;
		this.hanSuDung = hanSuDung;
		this.giaNhap = giaNhap;
		this.soLuong = soLuong;
		this.thanhTien = thanhTien;
		this.donViNhap = donViNhap;
		this.loaiHinhBan = loaiHinhBan;
		this.soViTrenHop = soViTrenHop;
		this.soVienTrenVi = soVienTrenVi;
		this.tongSoVien = tongSoVien;
	}

	// Getters and Setters
	public int getMaCTPN() {
		return maCTPN;
	}

	public void setMaCTPN(int maCTPN) {
		this.maCTPN = maCTPN;
	}

	public int getMaPhieuNhap() {
		return maPhieuNhap;
	}

	public void setMaPhieuNhap(int maPhieuNhap) {
		this.maPhieuNhap = maPhieuNhap;
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

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public BigDecimal getThanhTien() {
		return thanhTien;
	}

	public void setThanhTien(BigDecimal thanhTien) {
		this.thanhTien = thanhTien;
	}

	public String getDonViNhap() {
		return donViNhap;
	}

	public void setDonViNhap(String donViNhap) {
		this.donViNhap = donViNhap;
	}

	public String getLoaiHinhBan() {
		return loaiHinhBan;
	}

	public void setLoaiHinhBan(String loaiHinhBan) {
		this.loaiHinhBan = loaiHinhBan;
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

	public boolean isMergeBatch() {
		return mergeBatch;
	}

	public void setMergeBatch(boolean mergeBatch) {
		this.mergeBatch = mergeBatch;
	}

	@Override
	public String toString() {
		return "ChiTietPhieuNhap [maCTPN=" + maCTPN
			+ ", maPhieuNhap=" + maPhieuNhap
			+ ", maSanPham=" + maSanPham + ", soLo=" + soLo + "]";
	}
}
