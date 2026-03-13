USE CuaHangThuoc_Batch;
GO
SET NOCOUNT ON;

-- Tao 100 hoa don trong 7 ngay gan nhat
DECLARE @h INT, @hdId INT, @ngayBan DATETIME;
DECLARE @spBan INT, @slBan INT, @giaBan DECIMAL(18,2);
DECLARE @loHangId INT, @tonHienTai INT;
DECLARE @numItems INT, @item INT;

SET @h = 1;
WHILE @h <= 100
BEGIN
    -- Random thoi diem trong 7 ngay gan nhat (10080 phut)
    SET @ngayBan = DATEADD(MINUTE, -(ABS(CHECKSUM(NEWID())) % 10080), GETDATE());

    INSERT dbo.HoaDonBan(MaNguoiDung, MaKhachHang, NgayBan, GhiChu)
    VALUES(
        CASE WHEN @h % 3 = 0 THEN 2 ELSE 3 END,   -- ql01 hoac nv01
        CASE WHEN @h % 4 = 0 THEN 1 + ABS(CHECKSUM(NEWID())) % 5 ELSE NULL END,
        @ngayBan,
        N'HD #' + CAST(@h AS NVARCHAR(10))
    );
    SET @hdId = SCOPE_IDENTITY();

    -- Moi HD co 1-4 san pham
    SET @numItems = 1 + ABS(CHECKSUM(NEWID())) % 4;
    SET @item = 1;
    WHILE @item <= @numItems
    BEGIN
        SET @loHangId = NULL;
        -- Chon random lo con ton va con han
        SELECT TOP 1 @loHangId = lh.MaLoHang, @spBan = lh.MaSanPham,
               @tonHienTai = lh.SoLuongTon, @giaBan = sp.GiaBanDeXuat
        FROM dbo.LoHang lh
        JOIN dbo.SanPham sp ON sp.MaSanPham = lh.MaSanPham
        WHERE lh.SoLuongTon > 3 AND lh.HanSuDung > CAST(GETDATE() AS DATE)
        ORDER BY NEWID();

        IF @loHangId IS NOT NULL
        BEGIN
            SET @slBan = 1 + ABS(CHECKSUM(NEWID())) % 3;
            IF @slBan > @tonHienTai SET @slBan = 1;

            INSERT dbo.ChiTietHoaDon(MaHoaDon, MaLoHang, MaSanPham, SoLuong, GiaBan)
            VALUES(@hdId, @loHangId, @spBan, @slBan, @giaBan);

            -- Tru ton
            UPDATE dbo.LoHang SET SoLuongTon = SoLuongTon - @slBan WHERE MaLoHang = @loHangId;
        END
        SET @item = @item + 1;
    END

    -- Cap nhat tong tien HD
    UPDATE dbo.HoaDonBan
    SET TongTien = ISNULL((SELECT SUM(SoLuong * GiaBan) FROM dbo.ChiTietHoaDon WHERE MaHoaDon = @hdId), 0)
    WHERE MaHoaDon = @hdId;

    SET @h = @h + 1;
END
GO

-- Kiem tra
SELECT 'Hoa don' AS T, COUNT(*) AS SL FROM HoaDonBan;
SELECT 'Chi tiet' AS T, COUNT(*) AS SL FROM ChiTietHoaDon;
SELECT 'Doanh thu 7 ngay' AS T, SUM(TongTien) AS TongDoanhThu FROM HoaDonBan WHERE NgayBan >= DATEADD(DAY,-7,GETDATE());

-- Top 10 san pham ban chay
SELECT TOP 10 sp.TenSanPham, SUM(ct.SoLuong) AS TongBan, SUM(ct.SoLuong * ct.GiaBan) AS DoanhThu
FROM ChiTietHoaDon ct
JOIN SanPham sp ON sp.MaSanPham=ct.MaSanPham
GROUP BY sp.TenSanPham ORDER BY TongBan DESC;

-- Lo sap het han
SELECT 'Lo sap het han' AS T, COUNT(*) AS SL
FROM LoHang WHERE SoLuongTon > 0 AND HanSuDung BETWEEN GETDATE() AND DATEADD(DAY,30,GETDATE());

-- Lo da het han
SELECT 'Lo da het han' AS T, COUNT(*) AS SL
FROM LoHang WHERE SoLuongTon > 0 AND HanSuDung < GETDATE();

PRINT N'=== HOAN TAT: 100 hoa don + chi tiet ===';
GO
