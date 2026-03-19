/* =====================================================================
   SEED FULL DATA - Điền đầy đủ dữ liệu cho tất cả bảng
   =====================================================================
   Bảng cần điền:
   1. NguoiDung       - Thêm QuanLy + NhanVien
   2. KhachHang       - 30 khách hàng
   3. NhaCungCap      - 15 nhà cung cấp
   4. SP_Thuoc        - Chi tiết cho sản phẩm Thuốc
   5. SP_DuocMiPham   - Chi tiết cho Dược mỹ phẩm
   6. SP_ThucPhamChucNang - Chi tiết cho TPCN
   7. SP_ChamSocCaNhan - Chi tiết cho CSCN
   8. SP_ThietBiYTe   - Chi tiết cho TBYT
   9. LoHang          - Lô hàng cho sản phẩm
   ===================================================================== */

USE CuaHangThuoc_Batch;
GO

-- =====================================================================
-- 1) NGƯỜI DÙNG (thêm QuanLy + NhanVien)
-- =====================================================================
IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE TenDangNhap = N'quanly')
BEGIN
    INSERT NguoiDung(TenDangNhap, MatKhau, VaiTro, HoTen, Email, SoDienThoai) VALUES
    (N'quanly', N'123', N'QuanLy', N'Nguyễn Văn Quản Lý', N'quanly@shop.com', N'0901000001'),
    (N'nhanvien1', N'123', N'NhanVien', N'Trần Thị Nhân Viên', N'nv1@shop.com', N'0901000002'),
    (N'nhanvien2', N'123', N'NhanVien', N'Lê Văn Bán Hàng', N'nv2@shop.com', N'0901000003'),
    (N'nhanvien3', N'123', N'NhanVien', N'Phạm Thị Dược Sĩ', N'nv3@shop.com', N'0901000004');
    PRINT N'=== Đã thêm 4 người dùng ===';
END
GO

-- =====================================================================
-- 2) KHÁCH HÀNG (30 khách)
-- =====================================================================
IF NOT EXISTS (SELECT 1 FROM KhachHang)
BEGIN
    INSERT KhachHang(HoTen, SoDienThoai, Email, DiaChi) VALUES
    (N'Nguyễn Văn An', N'0912345001', N'an.nguyen@gmail.com', N'123 Lý Tự Trọng, Q.1, TP.HCM'),
    (N'Trần Thị Bình', N'0912345002', N'binh.tran@gmail.com', N'456 Nguyễn Huệ, Q.1, TP.HCM'),
    (N'Lê Văn Cường', N'0912345003', N'cuong.le@gmail.com', N'789 Hai Bà Trưng, Q.3, TP.HCM'),
    (N'Phạm Thị Dung', N'0912345004', N'dung.pham@gmail.com', N'12 Trần Hưng Đạo, Q.5, TP.HCM'),
    (N'Hoàng Văn Em', N'0912345005', N'em.hoang@gmail.com', N'34 Cách Mạng Tháng 8, Q.10, TP.HCM'),
    (N'Vũ Thị Phương', N'0912345006', N'phuong.vu@gmail.com', N'56 Lê Lợi, Q.1, TP.HCM'),
    (N'Đặng Văn Giang', N'0912345007', N'giang.dang@gmail.com', N'78 Pasteur, Q.3, TP.HCM'),
    (N'Bùi Thị Hương', N'0912345008', N'huong.bui@gmail.com', N'90 Nam Kỳ Khởi Nghĩa, Q.3, TP.HCM'),
    (N'Đỗ Văn Khoa', N'0912345009', N'khoa.do@gmail.com', N'11 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM'),
    (N'Ngô Thị Lan', N'0912345010', N'lan.ngo@gmail.com', N'22 Võ Văn Tần, Q.3, TP.HCM'),
    (N'Đinh Văn Minh', N'0912345011', N'minh.dinh@gmail.com', N'33 Nguyễn Thị Minh Khai, Q.1, TP.HCM'),
    (N'Lý Thị Ngọc', N'0912345012', N'ngoc.ly@gmail.com', N'44 Trường Chinh, Q.Tân Bình, TP.HCM'),
    (N'Mai Văn Phú', N'0912345013', N'phu.mai@gmail.com', N'55 Cộng Hòa, Q.Tân Bình, TP.HCM'),
    (N'Dương Thị Quỳnh', N'0912345014', N'quynh.duong@gmail.com', N'66 Phan Xích Long, Q.Phú Nhuận, TP.HCM'),
    (N'Tô Văn Sơn', N'0912345015', N'son.to@gmail.com', N'77 Nguyễn Văn Trỗi, Q.Phú Nhuận, TP.HCM'),
    (N'Cao Thị Tâm', N'0912345016', N'tam.cao@gmail.com', N'88 Lê Văn Sỹ, Q.3, TP.HCM'),
    (N'Hồ Văn Uy', N'0912345017', N'uy.ho@gmail.com', N'99 Nguyễn Đình Chiểu, Q.3, TP.HCM'),
    (N'Phan Thị Vân', N'0912345018', N'van.phan@gmail.com', N'10 Võ Thị Sáu, Q.3, TP.HCM'),
    (N'Trịnh Văn Xuân', N'0912345019', N'xuan.trinh@gmail.com', N'21 Bà Huyện Thanh Quan, Q.3, TP.HCM'),
    (N'Lương Thị Yến', N'0912345020', N'yen.luong@gmail.com', N'32 Tôn Thất Tùng, Q.1, TP.HCM'),
    (N'Châu Văn Đức', N'0912345021', N'duc.chau@gmail.com', N'43 Nguyễn Tri Phương, Q.10, TP.HCM'),
    (N'Kiều Thị Hà', N'0912345022', N'ha.kieu@gmail.com', N'54 3 Tháng 2, Q.10, TP.HCM'),
    (N'Tạ Văn Hùng', N'0912345023', N'hung.ta@gmail.com', N'65 Sư Vạn Hạnh, Q.10, TP.HCM'),
    (N'Lâm Thị Kim', N'0912345024', N'kim.lam@gmail.com', N'76 Lý Thường Kiệt, Q.10, TP.HCM'),
    (N'Quách Văn Long', N'0912345025', N'long.quach@gmail.com', N'87 Tô Hiến Thành, Q.10, TP.HCM'),
    (N'Từ Thị Mai', N'0912345026', N'mai.tu@gmail.com', N'98 Hoàng Văn Thụ, Q.Tân Bình, TP.HCM'),
    (N'Ông Văn Nam', N'0912345027', N'nam.ong@gmail.com', N'11 Phạm Văn Đồng, Q.Gò Vấp, TP.HCM'),
    (N'Sử Thị Oanh', N'0912345028', N'oanh.su@gmail.com', N'22 Quang Trung, Q.Gò Vấp, TP.HCM'),
    (N'Thái Văn Phong', N'0912345029', N'phong.thai@gmail.com', N'33 Nguyễn Oanh, Q.Gò Vấp, TP.HCM'),
    (N'Mạc Thị Quyên', N'0912345030', N'quyen.mac@gmail.com', N'44 Phan Văn Trị, Q.Gò Vấp, TP.HCM');
    PRINT N'=== Đã thêm 30 khách hàng ===';
END
GO

-- =====================================================================
-- 3) NHÀ CUNG CẤP (15 NCC)
-- =====================================================================
IF NOT EXISTS (SELECT 1 FROM NhaCungCap)
BEGIN
    INSERT NhaCungCap(TenNCC, SoDienThoai, Email, DiaChi) VALUES
    (N'Công ty Dược phẩm Hậu Giang (DHG)', N'02923891433', N'info@dhgpharma.com.vn', N'288 Bis Nguyễn Văn Cừ, Q.Ninh Kiều, TP.Cần Thơ'),
    (N'Công ty Dược Sài Gòn (Sapharco)', N'02838291020', N'sapharco@sapharco.com.vn', N'18-20 Nguyễn Trường Tộ, Q.4, TP.HCM'),
    (N'Traphaco', N'02437611234', N'info@traphaco.com.vn', N'75 Yên Ninh, Q.Ba Đình, Hà Nội'),
    (N'Dược Hà Tây (Hataphar)', N'02433520368', N'hataphar@hataphar.com.vn', N'Tổ 6 TT Quốc Oai, Hà Nội'),
    (N'Imexpharm', N'02773822244', N'info@imexpharm.com', N'04 Đường 30/4, TP.Cao Lãnh, Đồng Tháp'),
    (N'Pymepharco', N'02573824542', N'info@pymepharco.com', N'166-170 Nguyễn Huệ, TP.Tuy Hòa, Phú Yên'),
    (N'Domesco', N'02773851278', N'info@domesco.com', N'66 Quốc Lộ 30, TP.Cao Lãnh, Đồng Tháp'),
    (N'OPC Pharmaceutical', N'02838440853', N'info@opcpharma.com', N'1017 Hồng Bàng, Q.6, TP.HCM'),
    (N'Dược phẩm Trung Ương 1 (Pharbaco)', N'02439714468', N'pharbaco@pharbaco.com.vn', N'Thanh Xuân, Hà Nội'),
    (N'Mekophar', N'02838661632', N'info@mekophar.com.vn', N'297/5 Lý Thường Kiệt, Q.11, TP.HCM'),
    (N'Korea United Pharm', N'02838123456', N'kup@koreaunited.com', N'Tầng 10, Bitexco Tower, Q.1, TP.HCM'),
    (N'Abbott Việt Nam', N'02838985555', N'abbott.vn@abbott.com', N'Lầu 7 Kumho Asiana, Q.1, TP.HCM'),
    (N'Sanofi Việt Nam', N'02838240718', N'sanofi.vn@sanofi.com', N'10 Hàm Nghi, Q.1, TP.HCM'),
    (N'Johnson & Johnson VN', N'02838223344', N'jnj.vn@jnj.com', N'Tòa nhà Etown 3, Q.Tân Bình, TP.HCM'),
    (N'Roche Pharma VN', N'02838975511', N'roche.vn@roche.com', N'Deutsches Haus, Q.1, TP.HCM');
    PRINT N'=== Đã thêm 15 nhà cung cấp ===';
END
GO

-- =====================================================================
-- 4) SP_Thuoc - Thuộc tính mở rộng cho 350 sản phẩm Thuốc
-- =====================================================================
PRINT N'Đang tạo dữ liệu SP_Thuoc...';

-- Lấy danh sách MaSanPham loại Thuoc
DECLARE @thuocIds TABLE(rowId INT IDENTITY, MaSP INT);
INSERT @thuocIds(MaSP)
SELECT MaSanPham FROM SanPham WHERE LoaiSanPham = N'Thuoc' AND DaXoa = 0 ORDER BY MaSanPham;

-- Dữ liệu mẫu hoạt chất
DECLARE @hoatChat TABLE(id INT IDENTITY, val NVARCHAR(255));
INSERT @hoatChat(val) VALUES
(N'Paracetamol'),(N'Amoxicillin'),(N'Ibuprofen'),(N'Cefuroxim'),(N'Azithromycin'),
(N'Levofloxacin'),(N'Metformin'),(N'Amlodipine'),(N'Losartan'),(N'Omeprazole'),
(N'Cetirizine'),(N'Loratadine'),(N'Salbutamol'),(N'Prednisolone'),(N'Diclofenac'),
(N'Ciprofloxacin'),(N'Atorvastatin'),(N'Metronidazole'),(N'Domperidone'),(N'Captopril');

DECLARE @dangBaoChe TABLE(id INT IDENTITY, val NVARCHAR(100));
INSERT @dangBaoChe(val) VALUES
(N'Viên nén'),(N'Viên nang'),(N'Viên bao phim'),(N'Sirô'),(N'Dung dịch tiêm'),
(N'Thuốc bột pha tiêm'),(N'Viên sủi'),(N'Thuốc nhỏ mắt'),(N'Thuốc mỡ'),(N'Gel bôi');

DECLARE @hangSX TABLE(id INT IDENTITY, val NVARCHAR(255));
INSERT @hangSX(val) VALUES
(N'Dược Hậu Giang'),(N'Pymepharco'),(N'Imexpharm'),(N'Traphaco'),(N'Domesco'),
(N'Mekophar'),(N'Pharbaco'),(N'OPC'),(N'Sanofi'),(N'Abbott');

DECLARE @nuocSX TABLE(id INT IDENTITY, val NVARCHAR(100));
INSERT @nuocSX(val) VALUES
(N'Việt Nam'),(N'Việt Nam'),(N'Việt Nam'),(N'Ấn Độ'),(N'Hàn Quốc'),
(N'Pháp'),(N'Nhật Bản'),(N'Đức'),(N'Mỹ'),(N'Thụy Sĩ');

DECLARE @ti INT = 1, @tmax INT;
SELECT @tmax = COUNT(*) FROM @thuocIds;

WHILE @ti <= @tmax
BEGIN
    DECLARE @tMaSP INT;
    SELECT @tMaSP = MaSP FROM @thuocIds WHERE rowId = @ti;

    IF NOT EXISTS (SELECT 1 FROM SP_Thuoc WHERE MaSanPham = @tMaSP)
    BEGIN
        DECLARE @hcId INT = ((@ti-1) % 20) + 1;
        DECLARE @dbcId INT = ((@ti-1) % 10) + 1;
        DECLARE @hsxId INT = ((@ti-1) % 10) + 1;
        DECLARE @nsxId INT = ((@ti-1) % 10) + 1;

        INSERT SP_Thuoc(MaSanPham, HoatChatChinh, HamLuong, DangBaoChe, QuyCachDongGoi, SoDangKy, HangSanXuat, NuocSanXuat, ChiDinh, LieuDung)
        SELECT @tMaSP,
            hc.val,
            CAST(50 + (ABS(CHECKSUM(NEWID())) % 950) AS NVARCHAR) + N'mg',
            dbc.val,
            N'Hộp ' + CAST(1 + (ABS(CHECKSUM(NEWID())) % 10) AS NVARCHAR) + N' vỉ x 10 viên',
            N'VD-' + RIGHT('00000' + CAST(10000 + @ti AS NVARCHAR), 5),
            hsx.val,
            nsx.val,
            N'Theo chỉ định của bác sĩ',
            N'Người lớn: 1-2 viên/lần, 2-3 lần/ngày'
        FROM (SELECT val FROM @hoatChat WHERE id = @hcId) hc,
             (SELECT val FROM @dangBaoChe WHERE id = @dbcId) dbc,
             (SELECT val FROM @hangSX WHERE id = @hsxId) hsx,
             (SELECT val FROM @nuocSX WHERE id = @nsxId) nsx;
    END

    SET @ti = @ti + 1;
END
GO
PRINT N'=== Đã tạo SP_Thuoc ===';
GO

-- =====================================================================
-- 5) SP_DuocMiPham
-- =====================================================================
INSERT SP_DuocMiPham(MaSanPham, CongDung, LoaiDaPhuHop, ThanhPhanNoiBat, HuongDanSuDung, XuatXu)
SELECT sp.MaSanPham,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Dưỡng ẩm, làm mềm da'
        WHEN 1 THEN N'Chống nắng, bảo vệ da'
        WHEN 2 THEN N'Trị mụn, kháng khuẩn'
        WHEN 3 THEN N'Chống lão hóa, tái tạo da'
        WHEN 4 THEN N'Làm sáng da, mờ thâm'
    END,
    CASE (sp.MaSanPham % 4)
        WHEN 0 THEN N'Mọi loại da'
        WHEN 1 THEN N'Da dầu, da hỗn hợp'
        WHEN 2 THEN N'Da khô, da nhạy cảm'
        WHEN 3 THEN N'Da thường đến da dầu'
    END,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Hyaluronic Acid, Ceramide, Niacinamide'
        WHEN 1 THEN N'SPF50 PA++++, Tinosorb S, Vitamin E'
        WHEN 2 THEN N'Salicylic Acid, Tea Tree Oil, Zinc'
        WHEN 3 THEN N'Retinol, Peptide, Vitamin C'
        WHEN 4 THEN N'Alpha Arbutin, Tranexamic Acid, Niacinamide'
    END,
    N'Thoa đều lên vùng da cần điều trị, 1-2 lần/ngày. Tránh vùng mắt.',
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Pháp'
        WHEN 1 THEN N'Hàn Quốc'
        WHEN 2 THEN N'Nhật Bản'
        WHEN 3 THEN N'Mỹ'
        WHEN 4 THEN N'Việt Nam'
    END
FROM SanPham sp
WHERE sp.LoaiSanPham = N'DuocMiPham' AND sp.DaXoa = 0
AND NOT EXISTS (SELECT 1 FROM SP_DuocMiPham d WHERE d.MaSanPham = sp.MaSanPham);
GO
PRINT N'=== Đã tạo SP_DuocMiPham ===';
GO

-- =====================================================================
-- 6) SP_ThucPhamChucNang
-- =====================================================================
INSERT SP_ThucPhamChucNang(MaSanPham, ThanhPhan, CongDung, LieuDung, DoiTuongSuDung, LuuY)
SELECT sp.MaSanPham,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Vitamin C 1000mg, Kẽm 15mg, Vitamin D3'
        WHEN 1 THEN N'DHA 250mg, EPA 500mg, Vitamin E'
        WHEN 2 THEN N'Canxi nano 500mg, MK7 45mcg, Vitamin D3'
        WHEN 3 THEN N'Glucosamine 1500mg, Chondroitin 400mg, MSM'
        WHEN 4 THEN N'Collagen Type 1,3: 5000mg, Vitamin C, Biotin'
    END,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Tăng cường miễn dịch, chống oxy hóa'
        WHEN 1 THEN N'Bổ sung omega-3, hỗ trợ tim mạch, sáng mắt'
        WHEN 2 THEN N'Bổ sung canxi, hỗ trợ xương chắc khỏe'
        WHEN 3 THEN N'Hỗ trợ xương khớp, giảm đau khớp'
        WHEN 4 THEN N'Đẹp da, tóc, móng, chống lão hóa'
    END,
    N'Người lớn: 1-2 viên/ngày sau ăn',
    CASE (sp.MaSanPham % 3)
        WHEN 0 THEN N'Người lớn từ 18 tuổi'
        WHEN 1 THEN N'Người trên 50 tuổi, phụ nữ mang thai'
        WHEN 2 THEN N'Mọi lứa tuổi trừ trẻ dưới 6 tuổi'
    END,
    N'Thực phẩm bổ sung, không phải thuốc. Không dùng khi dị ứng với thành phần.'
FROM SanPham sp
WHERE sp.LoaiSanPham = N'ThucPhamChucNang' AND sp.DaXoa = 0
AND NOT EXISTS (SELECT 1 FROM SP_ThucPhamChucNang t WHERE t.MaSanPham = sp.MaSanPham);
GO
PRINT N'=== Đã tạo SP_ThucPhamChucNang ===';
GO

-- =====================================================================
-- 7) SP_ChamSocCaNhan
-- =====================================================================
INSERT SP_ChamSocCaNhan(MaSanPham, CongDung, HuongDanSuDung, DoiTuongSuDung, ChatLieu_MuiHuong)
SELECT sp.MaSanPham,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Diệt khuẩn 99.9%, bảo vệ tay'
        WHEN 1 THEN N'Chống ê buốt, bảo vệ men răng'
        WHEN 2 THEN N'Làm sạch sâu, dưỡng ẩm tóc'
        WHEN 3 THEN N'Dưỡng ẩm da toàn thân, mịn mượt'
        WHEN 4 THEN N'Kháng khuẩn, khử mùi hiệu quả'
    END,
    CASE (sp.MaSanPham % 3)
        WHEN 0 THEN N'Sử dụng trực tiếp, rửa sạch dưới nước'
        WHEN 1 THEN N'Xoa đều lên vùng cần dùng, massage nhẹ nhàng'
        WHEN 2 THEN N'Thoa đều, để 2-3 phút rồi rửa sạch'
    END,
    N'Mọi lứa tuổi',
    CASE (sp.MaSanPham % 4)
        WHEN 0 THEN N'Hương hoa nhài tự nhiên'
        WHEN 1 THEN N'Hương bạc hà mát lạnh'
        WHEN 2 THEN N'Không mùi, hypoallergenic'
        WHEN 3 THEN N'Hương lavender thư giãn'
    END
FROM SanPham sp
WHERE sp.LoaiSanPham = N'ChamSocCaNhan' AND sp.DaXoa = 0
AND NOT EXISTS (SELECT 1 FROM SP_ChamSocCaNhan c WHERE c.MaSanPham = sp.MaSanPham);
GO
PRINT N'=== Đã tạo SP_ChamSocCaNhan ===';
GO

-- =====================================================================
-- 8) SP_ThietBiYTe
-- =====================================================================
INSERT SP_ThietBiYTe(MaSanPham, ThuongHieu, Model, ThongSoKyThuat, BaoHanh, HuongDanSuDung)
SELECT sp.MaSanPham,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'Omron'
        WHEN 1 THEN N'Microlife'
        WHEN 2 THEN N'Accu-Chek'
        WHEN 3 THEN N'Beurer'
        WHEN 4 THEN N'Yuwell'
    END,
    CASE (sp.MaSanPham % 5)
        WHEN 0 THEN N'HEM-7156'
        WHEN 1 THEN N'BP-A1 Easy'
        WHEN 2 THEN N'Guide Plus'
        WHEN 3 THEN N'FT-90'
        WHEN 4 THEN N'YX-304'
    END,
    CASE (sp.MaSanPham % 4)
        WHEN 0 THEN N'Độ chính xác ±3mmHg, bộ nhớ 60 lần đo'
        WHEN 1 THEN N'Phạm vi đo 35-42°C, độ chính xác ±0.1°C'
        WHEN 2 THEN N'Đo SpO2 70-100%, nhịp tim 30-250 BPM'
        WHEN 3 THEN N'Công suất 0.2ml/min, dung tích bình 6ml'
    END,
    CASE (sp.MaSanPham % 3)
        WHEN 0 THEN N'24 tháng chính hãng'
        WHEN 1 THEN N'12 tháng chính hãng'
        WHEN 2 THEN N'36 tháng chính hãng'
    END,
    N'Đọc kỹ hướng dẫn sử dụng trước khi dùng. Bảo quản nơi khô ráo, thoáng mát.'
FROM SanPham sp
WHERE sp.LoaiSanPham = N'ThietBiYTe' AND sp.DaXoa = 0
AND NOT EXISTS (SELECT 1 FROM SP_ThietBiYTe t WHERE t.MaSanPham = sp.MaSanPham);
GO
PRINT N'=== Đã tạo SP_ThietBiYTe ===';
GO

-- =====================================================================
-- 9) LÔ HÀNG - Tạo 2-3 lô cho mỗi sản phẩm
-- =====================================================================
PRINT N'Đang tạo lô hàng...';

DECLARE @spIds TABLE(rowId INT IDENTITY, MaSP INT);
INSERT @spIds(MaSP) SELECT MaSanPham FROM SanPham WHERE DaXoa = 0 ORDER BY MaSanPham;

DECLARE @si INT = 1, @smax INT;
SELECT @smax = COUNT(*) FROM @spIds;

DECLARE @nccCount INT;
SELECT @nccCount = COUNT(*) FROM NhaCungCap WHERE DaXoa = 0;

WHILE @si <= @smax
BEGIN
    DECLARE @sMaSP INT;
    SELECT @sMaSP = MaSP FROM @spIds WHERE rowId = @si;

    -- Lô 1: Lô chính (còn hàng, hạn còn xa)
    DECLARE @nccId1 INT = 1 + (ABS(CHECKSUM(NEWID())) % @nccCount);
    DECLARE @soLuong1 INT = 20 + (ABS(CHECKSUM(NEWID())) % 80);
    DECLARE @giaNhap1 DECIMAL(18,2);
    SELECT @giaNhap1 = GiaBanDeXuat * 0.6 FROM SanPham WHERE MaSanPham = @sMaSP;

    INSERT LoHang(MaSanPham, SoLo, MaNCC, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon, NgayNhap, TrangThai, LoaiHinhBan, ThoiGianNhap)
    VALUES(@sMaSP,
        N'LO-' + RIGHT('000' + CAST(@si AS NVARCHAR), 3) + N'-A',
        @nccId1,
        DATEADD(MONTH, 6 + (ABS(CHECKSUM(NEWID())) % 18), GETDATE()),
        @giaNhap1,
        @soLuong1,
        @soLuong1 - (ABS(CHECKSUM(NEWID())) % (@soLuong1 / 3 + 1)),
        DATEADD(DAY, -(30 + ABS(CHECKSUM(NEWID())) % 60), GETDATE()),
        N'Đang bán', N'Bán lẻ',
        DATEADD(DAY, -(30 + ABS(CHECKSUM(NEWID())) % 60), GETDATE()));

    -- Lô 2: Lô phụ (ít hàng hơn)
    IF @si % 2 = 0  -- 50% sản phẩm có lô thứ 2
    BEGIN
        DECLARE @nccId2 INT = 1 + (ABS(CHECKSUM(NEWID())) % @nccCount);
        DECLARE @soLuong2 INT = 10 + (ABS(CHECKSUM(NEWID())) % 40);

        INSERT LoHang(MaSanPham, SoLo, MaNCC, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon, NgayNhap, TrangThai, LoaiHinhBan, ThoiGianNhap)
        VALUES(@sMaSP,
            N'LO-' + RIGHT('000' + CAST(@si AS NVARCHAR), 3) + N'-B',
            @nccId2,
            DATEADD(MONTH, 3 + (ABS(CHECKSUM(NEWID())) % 12), GETDATE()),
            @giaNhap1 * 1.05,
            @soLuong2,
            @soLuong2 - (ABS(CHECKSUM(NEWID())) % (@soLuong2 / 4 + 1)),
            DATEADD(DAY, -(10 + ABS(CHECKSUM(NEWID())) % 30), GETDATE()),
            N'Đang bán', N'Bán lẻ',
            DATEADD(DAY, -(10 + ABS(CHECKSUM(NEWID())) % 30), GETDATE()));
    END

    -- Lô 3: Lô sắp hết hạn (5% sản phẩm - để test cảnh báo)
    IF @si % 20 = 0
    BEGIN
        INSERT LoHang(MaSanPham, SoLo, MaNCC, HanSuDung, GiaNhap, SoLuongNhap, SoLuongTon, NgayNhap, TrangThai, LoaiHinhBan, ThoiGianNhap)
        VALUES(@sMaSP,
            N'LO-' + RIGHT('000' + CAST(@si AS NVARCHAR), 3) + N'-EXP',
            @nccId1,
            DATEADD(DAY, (ABS(CHECKSUM(NEWID())) % 25) + 1, GETDATE()),
            @giaNhap1 * 0.9,
            15,
            3 + (ABS(CHECKSUM(NEWID())) % 10),
            DATEADD(DAY, -90, GETDATE()),
            N'Đang bán', N'Bán lẻ',
            DATEADD(DAY, -90, GETDATE()));
    END

    SET @si = @si + 1;
END
GO
PRINT N'=== Đã tạo lô hàng ===';
GO

-- =====================================================================
-- KIỂM TRA KẾT QUẢ
-- =====================================================================
SELECT 'NguoiDung' AS Bang, COUNT(*) AS SoLuong FROM NguoiDung WHERE DaXoa=0
UNION ALL SELECT 'KhachHang', COUNT(*) FROM KhachHang WHERE DaXoa=0
UNION ALL SELECT 'NhaCungCap', COUNT(*) FROM NhaCungCap WHERE DaXoa=0
UNION ALL SELECT 'SP_Thuoc', COUNT(*) FROM SP_Thuoc
UNION ALL SELECT 'SP_DuocMiPham', COUNT(*) FROM SP_DuocMiPham
UNION ALL SELECT 'SP_ThucPhamChucNang', COUNT(*) FROM SP_ThucPhamChucNang
UNION ALL SELECT 'SP_ChamSocCaNhan', COUNT(*) FROM SP_ChamSocCaNhan
UNION ALL SELECT 'SP_ThietBiYTe', COUNT(*) FROM SP_ThietBiYTe
UNION ALL SELECT 'LoHang', COUNT(*) FROM LoHang;
GO

PRINT N'=== HOÀN TẤT: Đã điền đầy đủ dữ liệu cho tất cả bảng ===';
GO
