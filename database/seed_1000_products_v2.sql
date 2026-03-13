USE CuaHangThuoc_Batch;
GO
SET NOCOUNT ON;
GO

-- =====================================================================
-- BUOC 1: XOA SACH DU LIEU CU
-- =====================================================================
-- Drop FK
DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql += N'ALTER TABLE [' + OBJECT_SCHEMA_NAME(parent_object_id) + N'].[' + OBJECT_NAME(parent_object_id) + N'] DROP CONSTRAINT [' + name + N'];' + CHAR(13)
FROM sys.foreign_keys;
EXEC sp_executesql @sql;

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
TRUNCATE TABLE dbo.KhachHang;
DELETE FROM dbo.NhaCungCap;

-- Recreate FK
ALTER TABLE dbo.SP_Thuoc ADD CONSTRAINT FK_SP_Thuoc_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_DuocMiPham ADD CONSTRAINT FK_SP_DuocMiPham_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ThucPhamChucNang ADD CONSTRAINT FK_SP_TPCN_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ChamSocCaNhan ADD CONSTRAINT FK_SP_CSCN_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.SP_ThietBiYTe ADD CONSTRAINT FK_SP_TBYT_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_NCC FOREIGN KEY (MaNCC) REFERENCES dbo.NhaCungCap(MaNCC);
ALTER TABLE dbo.LoHang ADD CONSTRAINT FK_LoHang_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES dbo.PhieuNhap(MaPhieuNhap);
ALTER TABLE dbo.ChiTietPhieuNhap ADD CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES dbo.PhieuNhap(MaPhieuNhap);
ALTER TABLE dbo.ChiTietPhieuNhap ADD CONSTRAINT FK_CTPN_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (MaSanPham) REFERENCES dbo.SanPham(MaSanPham);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_LoHang FOREIGN KEY (MaLoHang) REFERENCES dbo.LoHang(MaLoHang);
ALTER TABLE dbo.ChiTietHoaDon ADD CONSTRAINT FK_CTHD_HDB FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDonBan(MaHoaDon);
ALTER TABLE dbo.HoaDonBan ADD CONSTRAINT FK_HDB_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES dbo.NguoiDung(MaNguoiDung);
ALTER TABLE dbo.HoaDonBan ADD CONSTRAINT FK_HDB_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang);
ALTER TABLE dbo.PhieuNhap ADD CONSTRAINT FK_PN_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES dbo.NguoiDung(MaNguoiDung);
ALTER TABLE dbo.PhieuNhap ADD CONSTRAINT FK_PN_NCC FOREIGN KEY (MaNCC) REFERENCES dbo.NhaCungCap(MaNCC);
PRINT N'[OK] Da xoa sach du lieu cu';
GO

-- =====================================================================
-- BUOC 2: NHA CUNG CAP + KHACH HANG
-- =====================================================================
SET IDENTITY_INSERT dbo.NhaCungCap ON;
INSERT dbo.NhaCungCap(MaNCC, TenNCC, SoDienThoai, Email, DiaChi) VALUES
(1, N'C' + NCHAR(244) + N'ng ty D' + NCHAR(432) + NCHAR(7907) + N'c ph' + NCHAR(7849) + N'm A', N'0901234567', N'duoca@mail.com', N'TP.HCM'),
(2, N'C' + NCHAR(244) + N'ng ty D' + NCHAR(432) + NCHAR(7907) + N'c ph' + NCHAR(7849) + N'm B', N'0912345678', N'duocb@mail.com', N'HN'),
(3, N'C' + NCHAR(244) + N'ng ty Thi' + NCHAR(7871) + N't b' + NCHAR(7883) + N' y t' + NCHAR(7871) + N' C', N'0923456789', N'tbytc@mail.com', N'DN');
SET IDENTITY_INSERT dbo.NhaCungCap OFF;

INSERT dbo.KhachHang(HoTen, SoDienThoai, DiaChi) VALUES
(N'Nguy' + NCHAR(7877) + N'n V' + NCHAR(259) + N'n An', N'0911111111', N'Q1 TP.HCM'),
(N'Tr' + NCHAR(7847) + N'n Th' + NCHAR(7883) + N' B' + NCHAR(237) + N'nh', N'0922222222', N'Q3 TP.HCM'),
(N'L' + NCHAR(234) + N' V' + NCHAR(259) + N'n C' + NCHAR(432) + NCHAR(7901) + N'ng', N'0933333333', N'Q7 TP.HCM'),
(N'Ph' + NCHAR(7841) + N'm Th' + NCHAR(7883) + N' D' + NCHAR(7847) + N'u', N'0944444444', N'Thu Duc'),
(N'Ho' + NCHAR(224) + N'ng V' + NCHAR(259) + N'n Em', N'0955555555', N'BD');
PRINT N'[OK] Da tao NCC + KH';
GO

-- =====================================================================
-- BUOC 3: 1000 SAN PHAM - TIENG VIET CHUAN (dung NCHAR)
-- =====================================================================
-- De dam bao tieng viet dung, dung NCHAR() cho cac ky tu co dau
-- NCHAR(7889)=o', NCHAR(7897)=o., NCHAR(7841)=a., NCHAR(7855)=a', NCHAR(7849)=a^', 
-- NCHAR(7871)=e^', NCHAR(7879)=e^., NCHAR(7883)=i., NCHAR(7899)=o^', NCHAR(7907)=o+.
-- NCHAR(432)=u+, NCHAR(7911)=u+', NCHAR(259)=a(, NCHAR(417)=o+, NCHAR(224)=a`, NCHAR(244)=o^

-- Insert 50 thuoc co ban bang ten ASCII (khong dau - luon hien thi dung)
INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu) VALUES
(N'Paracetamol 500mg', N'Hop', 15000, N'Thuoc', N'Giam dau ha sot', 10),
(N'Amoxicillin 500mg', N'Hop', 25000, N'Thuoc', N'Khang sinh penicillin', 10),
(N'Ibuprofen 400mg', N'Hop', 18000, N'Thuoc', N'Chong viem giam dau', 10),
(N'Cefuroxim 500mg', N'Hop', 85000, N'Thuoc', N'Khang sinh cephalosporin', 8),
(N'Azithromycin 250mg', N'Hop', 45000, N'Thuoc', N'Khang sinh macrolid', 8),
(N'Levofloxacin 500mg', N'Hop', 65000, N'Thuoc', N'Khang sinh quinolone', 8),
(N'Metformin 850mg', N'Hop', 22000, N'Thuoc', N'Dieu tri tieu duong', 10),
(N'Amlodipine 5mg', N'Hop', 35000, N'Thuoc', N'Ha huyet ap', 10),
(N'Losartan 50mg', N'Hop', 42000, N'Thuoc', N'Tang huyet ap', 10),
(N'Omeprazole 20mg', N'Hop', 28000, N'Thuoc', N'Uc che bom proton', 10),
(N'Pantoprazole 40mg', N'Hop', 55000, N'Thuoc', N'Loet da day', 8),
(N'Cetirizine 10mg', N'Hop', 12000, N'Thuoc', N'Chong di ung', 15),
(N'Loratadine 10mg', N'Hop', 15000, N'Thuoc', N'Khang histamine', 15),
(N'Salbutamol 2mg', N'Hop', 20000, N'Thuoc', N'Gian phe quan', 10),
(N'Montelukast 10mg', N'Hop', 75000, N'Thuoc', N'Du phong hen suyen', 8),
(N'Prednisolone 5mg', N'Hop', 18000, N'Thuoc', N'Chong viem corticoid', 10),
(N'Dexamethasone 0.5mg', N'Hop', 12000, N'Thuoc', N'Corticosteroid', 10),
(N'Diclofenac 75mg', N'Hop', 22000, N'Thuoc', N'Chong viem NSAID', 10),
(N'Meloxicam 15mg', N'Hop', 30000, N'Thuoc', N'Giam dau khop', 10),
(N'Ciprofloxacin 500mg', N'Hop', 38000, N'Thuoc', N'Khang sinh fluoroquinolone', 8),
(N'Clarithromycin 500mg', N'Hop', 72000, N'Thuoc', N'Khang sinh macrolid', 8),
(N'Clopidogrel 75mg', N'Hop', 55000, N'Thuoc', N'Chong ket tap tieu cau', 8),
(N'Atorvastatin 20mg', N'Hop', 48000, N'Thuoc', N'Giam cholesterol', 10),
(N'Rosuvastatin 10mg', N'Hop', 65000, N'Thuoc', N'Ha mo mau statin', 10),
(N'Simvastatin 20mg', N'Hop', 32000, N'Thuoc', N'Giam lipid mau', 10),
(N'Metronidazole 250mg', N'Hop', 15000, N'Thuoc', N'Khang khuan khang nam', 10),
(N'Fluconazole 150mg', N'Vien', 25000, N'Thuoc', N'Chong nam', 10),
(N'Acyclovir 800mg', N'Hop', 40000, N'Thuoc', N'Khang virus herpes', 8),
(N'Domperidone 10mg', N'Hop', 18000, N'Thuoc', N'Chong non', 10),
(N'Loperamide 2mg', N'Hop', 12000, N'Thuoc', N'Cam tieu chay', 15),
(N'Aspirin 81mg', N'Hop', 18000, N'Thuoc', N'Chong dong mau', 15),
(N'Tramadol 50mg', N'Hop', 35000, N'Thuoc', N'Giam dau opioid nhe', 5),
(N'Gabapentin 300mg', N'Hop', 60000, N'Thuoc', N'Giam dau than kinh', 5),
(N'Sertraline 50mg', N'Hop', 75000, N'Thuoc', N'Chong tram cam SSRI', 5),
(N'Carbamazepine 200mg', N'Hop', 30000, N'Thuoc', N'Chong dong kinh', 8),
(N'Levothyroxine 50mcg', N'Hop', 45000, N'Thuoc', N'Hormon tuyen giap', 10),
(N'Doxycycline 100mg', N'Hop', 22000, N'Thuoc', N'Khang sinh tetracycline', 10),
(N'Bisoprolol 5mg', N'Hop', 42000, N'Thuoc', N'Chen beta ha HA', 10),
(N'Valsartan 80mg', N'Hop', 48000, N'Thuoc', N'Chen thu the angiotensin', 8),
(N'Captopril 25mg', N'Hop', 20000, N'Thuoc', N'Uc che men chuyen ACE', 10),
(N'Warfarin 2mg', N'Hop', 25000, N'Thuoc', N'Chong dong mau', 5),
(N'Insulin Lantus 100IU', N'But', 450000, N'Thuoc', N'Insulin nen tac dung dai', 3),
(N'Esomeprazole 40mg', N'Hop', 68000, N'Thuoc', N'PPI the he moi', 8),
(N'Cefixime 200mg', N'Hop', 75000, N'Thuoc', N'Khang sinh cephalosporin 3', 8),
(N'Mebendazole 500mg', N'Vien', 5000, N'Thuoc', N'Tay giun', 20),
(N'ORS bù nuoc', N'Hop', 8000, N'Thuoc', N'Bu nuoc dien giai', 20),
(N'Berberin 100mg', N'Hop', 10000, N'Thuoc', N'Tri tieu chay', 15),
(N'Vitamin C 500mg', N'Hop', 15000, N'Thuoc', N'Bo sung vitamin C', 15),
(N'Vitamin B Complex', N'Hop', 25000, N'Thuoc', N'Vitamin B tong hop', 10),
(N'Calcium D3 500mg', N'Hop', 35000, N'Thuoc', N'Bo sung canxi', 10);
GO

-- Generate 350 thuoc nua (51-400) bang WHILE loop
DECLARE @thuocBase TABLE(id INT IDENTITY, ten NVARCHAR(80), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100));
INSERT @thuocBase VALUES
(N'Amoxicillin 250mg',N'Hop',18000,N'Khang sinh beta-lactam'),
(N'Cephalexin 500mg',N'Hop',35000,N'Cephalosporin the he 1'),
(N'Erythromycin 500mg',N'Hop',28000,N'Khang sinh macrolid'),
(N'Naproxen 500mg',N'Hop',28000,N'NSAID giam dau'),
(N'Celecoxib 200mg',N'Hop',85000,N'Uc che COX-2'),
(N'Allopurinol 300mg',N'Hop',18000,N'Ha acid uric'),
(N'Furosemide 40mg',N'Hop',15000,N'Loi tieu quai'),
(N'Propranolol 40mg',N'Hop',18000,N'Chen beta'),
(N'Amiodarone 200mg',N'Hop',85000,N'Chong loan nhip'),
(N'Digoxin 0.25mg',N'Hop',22000,N'Suy tim rung nhi'),
(N'Fenofibrate 160mg',N'Hop',48000,N'Giam triglyceride'),
(N'Metoclopramide 10mg',N'Hop',15000,N'Dieu hoa nhu dong ruot'),
(N'Spironolactone 25mg',N'Hop',35000,N'Loi tieu'),
(N'Colchicine 0.5mg',N'Hop',25000,N'Dieu tri gout'),
(N'Acarbose 50mg',N'Hop',48000,N'Ha duong huyet'),
(N'Glimepiride 2mg',N'Hop',45000,N'Tieu duong type 2'),
(N'Terbinafine 250mg',N'Hop',55000,N'Chong nam da'),
(N'Oseltamivir 75mg',N'Hop',150000,N'Khang virus cum'),
(N'Tranexamic acid 500mg',N'Hop',25000,N'Cam mau'),
(N'Folic acid 5mg',N'Hop',8000,N'Bo sung folate');

DECLARE @j INT = 1, @jcnt INT, @jid INT;
DECLARE @jten NVARCHAR(200), @jdvt NVARCHAR(20), @jgia INT, @jmota NVARCHAR(100);
SELECT @jcnt = COUNT(*) FROM @thuocBase;
WHILE @j <= 350
BEGIN
    SET @jid = ((@j-1) % @jcnt) + 1;
    SELECT @jten=ten, @jdvt=dvt, @jgia=gia, @jmota=mota FROM @thuocBase WHERE id=@jid;
    SET @jten = @jten + N' lot' + CAST(@j AS NVARCHAR(10));
    SET @jgia = @jgia + (ABS(CHECKSUM(NEWID())) % 20000) - 10000;
    IF @jgia < 5000 SET @jgia = 5000;
    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@jten, @jdvt, @jgia, N'Thuoc', @jmota, 5 + ABS(CHECKSUM(NEWID())) % 16);
    SET @j = @j + 1;
END
PRINT N'[OK] 400 Thuoc';
GO

-- 200 Duoc my pham
DECLARE @dBase TABLE(id INT IDENTITY, ten NVARCHAR(80), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100));
INSERT @dBase VALUES
(N'Kem chong nang SPF50',N'Tuyp',420000,N'Chong nang pho rong'),
(N'Serum Vitamin C',N'Chai',350000,N'Duong sang da'),
(N'Kem duong am',N'Hu',280000,N'Duong am da'),
(N'Nuoc tay trang',N'Chai',250000,N'Tay trang micellar'),
(N'Gel rua mat',N'Chai',180000,N'Lam sach diu nhe'),
(N'Kem tri mun',N'Tuyp',150000,N'Tri mun viem'),
(N'Serum retinol',N'Chai',380000,N'Chong lao hoa'),
(N'Toner cap am',N'Chai',220000,N'HA cap am sau'),
(N'Kem mat chong nhan',N'Tuyp',480000,N'Giam nhan mat'),
(N'Sua rua mat',N'Tuyp',150000,N'Lam sach nhe nhang');

DECLARE @dBrands TABLE(id INT IDENTITY, brand NVARCHAR(30));
INSERT @dBrands VALUES(N'La Roche-Posay'),(N'CeraVe'),(N'Eucerin'),(N'Vichy'),(N'Bioderma'),
(N'Neutrogena'),(N'The Ordinary'),(N'SVR'),(N'Avene'),(N'Obagi');

DECLARE @d INT=1, @did INT, @dbid INT;
DECLARE @dten NVARCHAR(200), @ddvt NVARCHAR(20), @dgia INT, @dmt NVARCHAR(100), @dbrd NVARCHAR(30);
WHILE @d <= 200
BEGIN
    SET @did = ((@d-1) % 10) + 1;
    SET @dbid = ((@d-1) % 10) + 1;
    SELECT @dten=ten, @ddvt=dvt, @dgia=gia, @dmt=mota FROM @dBase WHERE id=@did;
    SELECT @dbrd=brand FROM @dBrands WHERE id=@dbid;
    SET @dten = @dbrd + N' ' + @dten + N' #' + CAST(@d AS NVARCHAR(10));
    SET @dgia = @dgia + ABS(CHECKSUM(NEWID())) % 80000 - 40000;
    IF @dgia < 50000 SET @dgia = 50000;
    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@dten, @ddvt, @dgia, N'DuocMiPham', @dmt, 3 + ABS(CHECKSUM(NEWID())) % 8);
    SET @d = @d + 1;
END
PRINT N'[OK] 200 DuocMiPham';
GO

-- 200 TPCN
DECLARE @tBase TABLE(id INT IDENTITY, ten NVARCHAR(80), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100));
INSERT @tBase VALUES
(N'Vitamin C 1000mg',N'Chai',150000,N'Tang cuong mien dich'),
(N'Omega 3 Fish Oil',N'Chai',350000,N'Bo sung DHA EPA'),
(N'Vitamin D3 1000IU',N'Chai',150000,N'Hap thu canxi'),
(N'Canxi Nano MK7',N'Hop',280000,N'Xuong khop'),
(N'Glucosamine 1500mg',N'Chai',450000,N'Ho tro xuong khop'),
(N'Collagen Type 1',N'Chai',550000,N'Dep da chong lao hoa'),
(N'Probiotics 50B CFU',N'Chai',420000,N'Men vi sinh'),
(N'Multivitamin',N'Chai',380000,N'Da vitamin tong hop'),
(N'Coenzyme Q10 200mg',N'Chai',480000,N'Ho tro tim mach'),
(N'Spirulina 500mg',N'Chai',250000,N'Tao xoan dinh duong');

DECLARE @tBrands TABLE(id INT IDENTITY, brand NVARCHAR(30));
INSERT @tBrands VALUES(N'DHC'),(N'Blackmores'),(N'Kirkland'),(N'Nature Made'),(N'Swisse'),
(N'Now Foods'),(N'GNC'),(N'Solgar'),(N'Puritan Pride'),(N'Centrum');

DECLARE @t INT=1, @tid INT, @tbid INT;
DECLARE @tten NVARCHAR(200), @tdvt NVARCHAR(20), @tgia INT, @tmt NVARCHAR(100), @tbrd NVARCHAR(30);
WHILE @t <= 200
BEGIN
    SET @tid = ((@t-1) % 10) + 1;
    SET @tbid = ((@t-1) % 10) + 1;
    SELECT @tten=ten, @tdvt=dvt, @tgia=gia, @tmt=mota FROM @tBase WHERE id=@tid;
    SELECT @tbrd=brand FROM @tBrands WHERE id=@tbid;
    SET @tten = @tbrd + N' ' + @tten + N' #' + CAST(@t AS NVARCHAR(10));
    SET @tgia = @tgia + ABS(CHECKSUM(NEWID())) % 60000 - 30000;
    IF @tgia < 30000 SET @tgia = 30000;
    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@tten, @tdvt, @tgia, N'ThucPhamChucNang', @tmt, 3 + ABS(CHECKSUM(NEWID())) % 10);
    SET @t = @t + 1;
END
PRINT N'[OK] 200 ThucPhamChucNang';
GO

-- 100 Cham soc ca nhan
DECLARE @cBase TABLE(id INT IDENTITY, ten NVARCHAR(80), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100));
INSERT @cBase VALUES
(N'Nuoc rua tay 500ml',N'Chai',45000,N'Diet khuan 99.9%'),
(N'Kem danh rang Sensodyne',N'Tuyp',65000,N'Chong e buot'),
(N'Nuoc suc mieng 750ml',N'Chai',85000,N'Khang khuan mieng'),
(N'NaCl 0.9% rua mui',N'Chai',25000,N'Ve sinh mui'),
(N'Sua tam duong am 900ml',N'Chai',120000,N'Duong am toan than'),
(N'Dau goi tri gau 650ml',N'Chai',110000,N'Cham soc toc'),
(N'Gel rua tay kho 500ml',N'Chai',55000,N'Diet khuan nhanh'),
(N'Bong gon y te 100g',N'Cuon',15000,N'Tiet trung'),
(N'Kem chong muoi 60ml',N'Tuyp',22000,N'Chong muoi 8h'),
(N'Lan khu mui 150ml',N'Chai',55000,N'Khu mui 48h');

DECLARE @cBrands TABLE(id INT IDENTITY, brand NVARCHAR(30));
INSERT @cBrands VALUES(N'Dove'),(N'Nivea'),(N'Lifebuoy'),(N'Oral-B'),(N'Listerine');

DECLARE @c INT=1, @cid INT, @cbid INT;
DECLARE @cten NVARCHAR(200), @cdvt NVARCHAR(20), @cgia INT, @cmt NVARCHAR(100), @cbrd NVARCHAR(30);
WHILE @c <= 100
BEGIN
    SET @cid = ((@c-1) % 10) + 1;
    SET @cbid = ((@c-1) % 5) + 1;
    SELECT @cten=ten, @cdvt=dvt, @cgia=gia, @cmt=mota FROM @cBase WHERE id=@cid;
    SELECT @cbrd=brand FROM @cBrands WHERE id=@cbid;
    SET @cten = @cbrd + N' ' + @cten + N' #' + CAST(@c AS NVARCHAR(10));
    SET @cgia = @cgia + ABS(CHECKSUM(NEWID())) % 20000 - 10000;
    IF @cgia < 10000 SET @cgia = 10000;
    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@cten, @cdvt, @cgia, N'ChamSocCaNhan', @cmt, 5 + ABS(CHECKSUM(NEWID())) % 15);
    SET @c = @c + 1;
END
PRINT N'[OK] 100 ChamSocCaNhan';
GO

-- 100 Thiet bi y te
DECLARE @eBase TABLE(id INT IDENTITY, ten NVARCHAR(80), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100));
INSERT @eBase VALUES
(N'Nhiet ke dien tu',N'Cai',85000,N'Do nhiet nach mieng'),
(N'May do huyet ap',N'Cai',1250000,N'Do bap tay tu dong'),
(N'May do duong huyet',N'Cai',850000,N'Kiem tra glucose'),
(N'May xong mui hong',N'Cai',980000,N'Xong hoi khi dung'),
(N'May do SpO2 kep ngon',N'Cai',350000,N'Do oxy mau'),
(N'Khau trang y te 50 cai',N'Hop',45000,N'Khang khuan BFE 99%'),
(N'Gang tay latex 100 cai',N'Hop',85000,N'Tiet trung'),
(N'Can suc khoe dien tu',N'Cai',480000,N'Do BMI'),
(N'Bang keo y te cuon',N'Cuon',15000,N'Bang keo vai'),
(N'Bang gac vo trung 10x10',N'Goi',12000,N'Gac tiet trung');

DECLARE @eBrands TABLE(id INT IDENTITY, brand NVARCHAR(30));
INSERT @eBrands VALUES(N'Omron'),(N'Microlife'),(N'Accu-Chek'),(N'Beurer'),(N'Yuwell');

DECLARE @e INT=1, @eid INT, @ebid INT;
DECLARE @eten NVARCHAR(200), @edvt NVARCHAR(20), @egia INT, @emt NVARCHAR(100), @ebrd NVARCHAR(30);
WHILE @e <= 100
BEGIN
    SET @eid = ((@e-1) % 10) + 1;
    SET @ebid = ((@e-1) % 5) + 1;
    SELECT @eten=ten, @edvt=dvt, @egia=gia, @emt=mota FROM @eBase WHERE id=@eid;
    SELECT @ebrd=brand FROM @eBrands WHERE id=@ebid;
    SET @eten = @ebrd + N' ' + @eten + N' #' + CAST(@e AS NVARCHAR(10));
    SET @egia = @egia + ABS(CHECKSUM(NEWID())) % 50000 - 25000;
    IF @egia < 10000 SET @egia = 10000;
    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@eten, @edvt, @egia, N'ThietBiYTe', @emt, 3 + ABS(CHECKSUM(NEWID())) % 8);
    SET @e = @e + 1;
END
PRINT N'[OK] 100 ThietBiYTe';
GO

-- =====================================================================
-- BUOC 4: LO HANG (con han, sap het han, da het han)
-- =====================================================================
-- Tao 1 phieu nhap goc
INSERT dbo.PhieuNhap(MaNguoiDung, MaNCC, GhiChu) VALUES(2, 1, N'Phieu nhap mau');
DECLARE @pnId INT = SCOPE_IDENTITY();

-- Tao lo hang cho 200 san pham dau (Thuoc) voi cac muc han su dung da dang
DECLARE @sp INT = 1;
DECLARE @hsd DATE, @loaiHSD INT, @soLuong INT, @giaNhap DECIMAL(18,2);
WHILE @sp <= 200
BEGIN
    -- Lo 1: con han dai (180-365 ngay)
    SET @hsd = DATEADD(DAY, 180 + ABS(CHECKSUM(NEWID())) % 186, GETDATE());
    SET @soLuong = 50 + ABS(CHECKSUM(NEWID())) % 200;
    SET @giaNhap = 5000 + ABS(CHECKSUM(NEWID())) % 50000;
    INSERT dbo.LoHang(MaSanPham, SoLo, MaNCC, MaPhieuNhap, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon)
    VALUES(@sp, N'LO' + RIGHT('000'+CAST(@sp AS VARCHAR),3) + N'A', 1, @pnId, @hsd, @giaNhap, @soLuong, @soLuong);

    -- Lo 2: sap het han (5-30 ngay)
    IF @sp <= 80
    BEGIN
        SET @hsd = DATEADD(DAY, 5 + ABS(CHECKSUM(NEWID())) % 26, GETDATE());
        SET @soLuong = 10 + ABS(CHECKSUM(NEWID())) % 40;
        INSERT dbo.LoHang(MaSanPham, SoLo, MaNCC, MaPhieuNhap, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon)
        VALUES(@sp, N'LO' + RIGHT('000'+CAST(@sp AS VARCHAR),3) + N'B', 1, @pnId, @hsd, @giaNhap * 0.8, @soLuong, @soLuong);
    END

    -- Lo 3: da het han (cho 30 SP dau) - insert truc tiep, skip SP check
    IF @sp <= 30
    BEGIN
        SET @hsd = DATEADD(DAY, -(1 + ABS(CHECKSUM(NEWID())) % 30), GETDATE());
        SET @soLuong = 5 + ABS(CHECKSUM(NEWID())) % 20;
        INSERT dbo.LoHang(MaSanPham, SoLo, MaNCC, MaPhieuNhap, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon)
        VALUES(@sp, N'LO' + RIGHT('000'+CAST(@sp AS VARCHAR),3) + N'X', 1, @pnId, @hsd, @giaNhap * 0.5, @soLuong, @soLuong);
    END

    SET @sp = @sp + 1;
END

-- Lo hang cho san pham 201-400 (chi lo con han)
SET @sp = 201;
WHILE @sp <= 400
BEGIN
    SET @hsd = DATEADD(DAY, 90 + ABS(CHECKSUM(NEWID())) % 275, GETDATE());
    SET @soLuong = 30 + ABS(CHECKSUM(NEWID())) % 100;
    SET @giaNhap = 5000 + ABS(CHECKSUM(NEWID())) % 40000;
    INSERT dbo.LoHang(MaSanPham, SoLo, MaNCC, MaPhieuNhap, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon)
    VALUES(@sp, N'LO' + RIGHT('000'+CAST(@sp AS VARCHAR),3), 1, @pnId, @hsd, @giaNhap, @soLuong, @soLuong);
    SET @sp = @sp + 1;
END

-- Lo hang cho san pham 401-600 (DuocMiPham, TPCN co ton)
SET @sp = 401;
WHILE @sp <= 600
BEGIN
    SET @hsd = DATEADD(DAY, 120 + ABS(CHECKSUM(NEWID())) % 240, GETDATE());
    SET @soLuong = 20 + ABS(CHECKSUM(NEWID())) % 80;
    SET @giaNhap = 30000 + ABS(CHECKSUM(NEWID())) % 200000;
    INSERT dbo.LoHang(MaSanPham, SoLo, MaNCC, MaPhieuNhap, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon)
    VALUES(@sp, N'LO' + RIGHT('000'+CAST(@sp AS VARCHAR),3), 1, @pnId, @hsd, @giaNhap, @soLuong, @soLuong);
    SET @sp = @sp + 1;
END

PRINT N'[OK] Lo hang da tao (con han + sap het han + da het han)';
GO

-- =====================================================================
-- BUOC 5: 100 HOA DON TRONG 7 NGAY GAN NHAT
-- =====================================================================
DECLARE @h INT = 1;
DECLARE @hdId INT, @ngayBan DATETIME, @spBan INT, @slBan INT, @giaBan DECIMAL(18,2);
DECLARE @loHangId INT, @tonHienTai INT;

WHILE @h <= 100
BEGIN
    -- Random ngay trong 7 ngay gan nhat
    SET @ngayBan = DATEADD(HOUR, -(ABS(CHECKSUM(NEWID())) % 168), GETDATE());

    -- Tao hoa don
    INSERT dbo.HoaDonBan(MaNguoiDung, MaKhachHang, NgayBan, GhiChu)
    VALUES(
        CASE WHEN @h % 3 = 0 THEN 2 ELSE 3 END,
        CASE WHEN @h % 4 = 0 THEN 1 + ABS(CHECKSUM(NEWID())) % 5 ELSE NULL END,
        @ngayBan,
        N'Hoa don test #' + CAST(@h AS NVARCHAR(10))
    );
    SET @hdId = SCOPE_IDENTITY();

    -- Moi hoa don co 1-4 san pham
    DECLARE @numItems INT = 1 + ABS(CHECKSUM(NEWID())) % 4;
    DECLARE @item INT = 1;
    WHILE @item <= @numItems
    BEGIN
        -- Chon san pham co lo hang con ton
        SELECT TOP 1 @loHangId = lh.MaLoHang, @spBan = lh.MaSanPham, @tonHienTai = lh.SoLuongTon,
               @giaBan = sp.GiaBanDeXuat
        FROM dbo.LoHang lh
        JOIN dbo.SanPham sp ON sp.MaSanPham = lh.MaSanPham
        WHERE lh.SoLuongTon > 5
          AND lh.HanSuDung > CAST(GETDATE() AS DATE)
        ORDER BY NEWID();

        IF @loHangId IS NOT NULL AND @tonHienTai > 5
        BEGIN
            SET @slBan = 1 + ABS(CHECKSUM(NEWID())) % CASE WHEN @tonHienTai > 20 THEN 5 ELSE 2 END;
            IF @slBan > @tonHienTai SET @slBan = 1;

            INSERT dbo.ChiTietHoaDon(MaHoaDon, MaLoHang, MaSanPham, SoLuong, GiaBan)
            VALUES(@hdId, @loHangId, @spBan, @slBan, @giaBan);

            UPDATE dbo.LoHang SET SoLuongTon = SoLuongTon - @slBan WHERE MaLoHang = @loHangId;
        END

        SET @item = @item + 1;
    END

    -- Cap nhat tong tien hoa don
    UPDATE dbo.HoaDonBan
    SET TongTien = ISNULL((SELECT SUM(ThanhTien) FROM dbo.ChiTietHoaDon WHERE MaHoaDon = @hdId), 0)
    WHERE MaHoaDon = @hdId;

    SET @h = @h + 1;
END
PRINT N'[OK] 100 hoa don trong 7 ngay gan nhat';
GO

-- =====================================================================
-- BUOC 6: KIEM TRA KET QUA
-- =====================================================================
SELECT N'TONG SAN PHAM' AS Info, COUNT(*) AS SL FROM dbo.SanPham;
SELECT LoaiSanPham, COUNT(*) AS SL FROM dbo.SanPham GROUP BY LoaiSanPham ORDER BY SL DESC;
SELECT N'TONG LO HANG' AS Info, COUNT(*) AS SL FROM dbo.LoHang;
SELECT N'Lo con han' AS Info, COUNT(*) AS SL FROM dbo.LoHang WHERE HanSuDung > GETDATE() AND SoLuongTon > 0;
SELECT N'Lo sap het han (30 ngay)' AS Info, COUNT(*) AS SL FROM dbo.LoHang WHERE HanSuDung BETWEEN GETDATE() AND DATEADD(DAY,30,GETDATE()) AND SoLuongTon > 0;
SELECT N'Lo da het han' AS Info, COUNT(*) AS SL FROM dbo.LoHang WHERE HanSuDung < GETDATE() AND SoLuongTon > 0;
SELECT N'TONG HOA DON' AS Info, COUNT(*) AS SL FROM dbo.HoaDonBan;
SELECT N'HOA DON 7 NGAY' AS Info, COUNT(*) AS SL FROM dbo.HoaDonBan WHERE NgayBan >= DATEADD(DAY,-7,GETDATE());
PRINT N'[OK] HOAN TAT SEED DATA';
GO
