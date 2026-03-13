USE CuaHangThuoc_Batch;
GO

-- Drop all FK constraints temporarily
DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql += N'ALTER TABLE [' + OBJECT_SCHEMA_NAME(parent_object_id) + N'].[' + OBJECT_NAME(parent_object_id) + N'] DROP CONSTRAINT [' + name + N'];' + CHAR(13)
FROM sys.foreign_keys;
EXEC sp_executesql @sql;
PRINT N'Dropped all FK constraints';
GO

-- Truncate all data tables
TRUNCATE TABLE dbo.ChiTietHoaDon;
TRUNCATE TABLE dbo.HoaDonBan;
TRUNCATE TABLE dbo.ChiTietPhieuNhap;
TRUNCATE TABLE dbo.LoHang;
TRUNCATE TABLE dbo.PhieuNhap;
IF OBJECT_ID('dbo.SP_Thuoc') IS NOT NULL TRUNCATE TABLE dbo.SP_Thuoc;
IF OBJECT_ID('dbo.SP_DuocMiPham') IS NOT NULL TRUNCATE TABLE dbo.SP_DuocMiPham;
IF OBJECT_ID('dbo.SP_ThucPhamChucNang') IS NOT NULL TRUNCATE TABLE dbo.SP_ThucPhamChucNang;
IF OBJECT_ID('dbo.SP_ChamSocCaNhan') IS NOT NULL TRUNCATE TABLE dbo.SP_ChamSocCaNhan;
IF OBJECT_ID('dbo.SP_ThietBiYTe') IS NOT NULL TRUNCATE TABLE dbo.SP_ThietBiYTe;
TRUNCATE TABLE dbo.SanPham;
PRINT N'Truncated all tables';
GO

-- Recreate FK constraints from original schema
ALTER TABLE dbo.SP_Thuoc ADD CONSTRAINT FK_SP_Thuoc_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_DuocMiPham ADD CONSTRAINT FK_SP_DuocMiPham_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ThucPhamChucNang ADD CONSTRAINT FK_SP_TPCN_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ChamSocCaNhan ADD CONSTRAINT FK_SP_CSCN_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ThietBiYTe ADD CONSTRAINT FK_SP_TBYT_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_LoHang FOREIGN KEY (MaLoHang) REFERENCES dbo.LoHang(MaLoHang);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDonBan(MaHoaDon);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_NCC FOREIGN KEY (MaNCC) REFERENCES dbo.NhaCungCap(MaNCC);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES dbo.PhieuNhap(MaPhieuNhap);
ALTER TABLE dbo.ChiTietPhieuNhap ADD CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES dbo.PhieuNhap(MaPhieuNhap);
ALTER TABLE dbo.HoaDonBan ADD CONSTRAINT FK_HDB_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES dbo.NguoiDung(MaNguoiDung);
ALTER TABLE dbo.HoaDonBan ADD CONSTRAINT FK_HDB_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang);
ALTER TABLE dbo.PhieuNhap ADD CONSTRAINT FK_PN_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES dbo.NguoiDung(MaNguoiDung);
ALTER TABLE dbo.PhieuNhap ADD CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNCC) REFERENCES dbo.NhaCungCap(MaNCC);
PRINT N'Recreated FK constraints';
GO

SELECT COUNT(*) AS SanPhamCount FROM dbo.SanPham;
GO
