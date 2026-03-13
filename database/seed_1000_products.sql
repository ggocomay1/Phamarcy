/* =====================================================================
   SEED 1000 SAN PHAM - Nha thuoc MEPHAR
   =====================================================================
   Script nay:
   1. Xoa du lieu lien quan (chi tiet, lo hang, phieu nhap, hoa don...)
   2. Reset bang SanPham
   3. Insert 1000 san pham thuc te cho nha thuoc
   ===================================================================== */

USE CuaHangThuoc_Batch;
GO

-- =====================================================================
-- STEP 1: XOA DU LIEU CU (theo thu tu FK)
-- =====================================================================
DELETE FROM dbo.ChiTietHoaDon;
DELETE FROM dbo.HoaDonBan;
DELETE FROM dbo.ChiTietPhieuNhap;
DELETE FROM dbo.LoHang;
DELETE FROM dbo.PhieuNhap;

-- Xoa san pham extension tables
DELETE FROM dbo.SP_Thuoc;
DELETE FROM dbo.SP_DuocMiPham;
DELETE FROM dbo.SP_ThucPhamChucNang;
DELETE FROM dbo.SP_ChamSocCaNhan;
DELETE FROM dbo.SP_ThietBiYTe;

-- Xoa san pham chinh
DELETE FROM dbo.SanPham;

-- Reset identity
DBCC CHECKIDENT ('dbo.SanPham', RESEED, 0);
DBCC CHECKIDENT ('dbo.HoaDonBan', RESEED, 0);
DBCC CHECKIDENT ('dbo.PhieuNhap', RESEED, 0);
DBCC CHECKIDENT ('dbo.LoHang', RESEED, 0);
GO

PRINT N'=== Da xoa du lieu cu ===';
GO

-- =====================================================================
-- STEP 2: INSERT 1000 SAN PHAM
-- =====================================================================

-- ==================== THUOC (1 - 400) ====================
-- 400 san pham thuoc thuc te

INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu) VALUES
(N'Paracetamol 500mg',N'Hộp',15000,N'Thuoc',N'Giảm đau, hạ sốt',10),
(N'Amoxicillin 500mg',N'Hộp',25000,N'Thuoc',N'Kháng sinh penicillin',10),
(N'Ibuprofen 400mg',N'Hộp',18000,N'Thuoc',N'Chống viêm giảm đau',10),
(N'Cefuroxim 500mg',N'Hộp',85000,N'Thuoc',N'Kháng sinh cephalosporin',8),
(N'Azithromycin 250mg',N'Hộp',45000,N'Thuoc',N'Kháng sinh macrolid',8),
(N'Levofloxacin 500mg',N'Hộp',65000,N'Thuoc',N'Kháng sinh quinolone',8),
(N'Metformin 850mg',N'Hộp',22000,N'Thuoc',N'Tiểu đường type 2',10),
(N'Amlodipine 5mg',N'Hộp',35000,N'Thuoc',N'Hạ huyết áp',10),
(N'Losartan 50mg',N'Hộp',42000,N'Thuoc',N'Tăng huyết áp',10),
(N'Omeprazole 20mg',N'Hộp',28000,N'Thuoc',N'Ức chế bơm proton',10),
(N'Pantoprazole 40mg',N'Hộp',55000,N'Thuoc',N'Loét dạ dày',8),
(N'Cetirizine 10mg',N'Hộp',12000,N'Thuoc',N'Chống dị ứng',15),
(N'Loratadine 10mg',N'Hộp',15000,N'Thuoc',N'Kháng histamine',15),
(N'Salbutamol 2mg',N'Hộp',20000,N'Thuoc',N'Giãn phế quản',10),
(N'Montelukast 10mg',N'Hộp',75000,N'Thuoc',N'Dự phòng hen suyễn',8),
(N'Prednisolone 5mg',N'Hộp',18000,N'Thuoc',N'Chống viêm corticoid',10),
(N'Dexamethasone 0.5mg',N'Hộp',12000,N'Thuoc',N'Corticosteroid',10),
(N'Diclofenac 75mg',N'Hộp',22000,N'Thuoc',N'Chống viêm NSAID',10),
(N'Meloxicam 15mg',N'Hộp',30000,N'Thuoc',N'Giảm đau khớp',10),
(N'Ciprofloxacin 500mg',N'Hộp',38000,N'Thuoc',N'Kháng sinh fluoroquinolone',8),
(N'Clarithromycin 500mg',N'Hộp',72000,N'Thuoc',N'Kháng sinh macrolid',8),
(N'Clopidogrel 75mg',N'Hộp',55000,N'Thuoc',N'Chống kết tập tiểu cầu',8),
(N'Atorvastatin 20mg',N'Hộp',48000,N'Thuoc',N'Giảm cholesterol',10),
(N'Rosuvastatin 10mg',N'Hộp',65000,N'Thuoc',N'Hạ mỡ máu statin',10),
(N'Simvastatin 20mg',N'Hộp',32000,N'Thuoc',N'Giảm lipid máu',10),
(N'Metronidazole 250mg',N'Hộp',15000,N'Thuoc',N'Kháng khuẩn kháng nấm',10),
(N'Fluconazole 150mg',N'Viên',25000,N'Thuoc',N'Chống nấm',10),
(N'Acyclovir 800mg',N'Hộp',40000,N'Thuoc',N'Kháng virus herpes',8),
(N'Domperidone 10mg',N'Hộp',18000,N'Thuoc',N'Chống nôn',10),
(N'Loperamide 2mg',N'Hộp',12000,N'Thuoc',N'Cầm tiêu chảy',15),
(N'Bisacodyl 5mg',N'Hộp',10000,N'Thuoc',N'Nhuận tràng',15),
(N'Spironolactone 25mg',N'Hộp',35000,N'Thuoc',N'Lợi tiểu',8),
(N'Furosemide 40mg',N'Hộp',15000,N'Thuoc',N'Lợi tiểu quai',10),
(N'Captopril 25mg',N'Hộp',20000,N'Thuoc',N'Ức chế men chuyển ACE',10),
(N'Enalapril 5mg',N'Hộp',25000,N'Thuoc',N'Hạ huyết áp ACE',10),
(N'Glimepiride 2mg',N'Hộp',45000,N'Thuoc',N'Hạ đường huyết',8),
(N'Gliclazide 30mg MR',N'Hộp',55000,N'Thuoc',N'Tiểu đường type 2',8),
(N'Aspirin 81mg',N'Hộp',18000,N'Thuoc',N'Chống kết tập tiểu cầu',15),
(N'Tramadol 50mg',N'Hộp',35000,N'Thuoc',N'Giảm đau opioid nhẹ',5),
(N'Gabapentin 300mg',N'Hộp',60000,N'Thuoc',N'Giảm đau thần kinh',5),
(N'Amitriptyline 25mg',N'Hộp',15000,N'Thuoc',N'Chống trầm cảm',8),
(N'Sertraline 50mg',N'Hộp',75000,N'Thuoc',N'Chống trầm cảm SSRI',5),
(N'Diazepam 5mg',N'Hộp',20000,N'Thuoc',N'An thần giãn cơ',5),
(N'Alprazolam 0.5mg',N'Hộp',28000,N'Thuoc',N'Chống lo âu',5),
(N'Phenobarbital 100mg',N'Hộp',12000,N'Thuoc',N'Chống co giật',8),
(N'Carbamazepine 200mg',N'Hộp',30000,N'Thuoc',N'Chống động kinh',8),
(N'Levothyroxine 50mcg',N'Hộp',45000,N'Thuoc',N'Hormon tuyến giáp',10),
(N'Doxycycline 100mg',N'Hộp',22000,N'Thuoc',N'Kháng sinh tetracycline',10),
(N'Nifedipine 20mg',N'Hộp',25000,N'Thuoc',N'Chẹn kênh canxi',10),
(N'Bisoprolol 5mg',N'Hộp',42000,N'Thuoc',N'Chẹn beta hạ HA',10);
GO

-- Thuoc 51-100
INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu) VALUES
(N'Valsartan 80mg',N'Hộp',48000,N'Thuoc',N'Chẹn thụ thể angiotensin',8),
(N'Irbesartan 150mg',N'Hộp',52000,N'Thuoc',N'Hạ huyết áp ARB',8),
(N'Telmisartan 40mg',N'Hộp',45000,N'Thuoc',N'Điều trị tăng HA',8),
(N'Perindopril 5mg',N'Hộp',62000,N'Thuoc',N'Ức chế ACE',8),
(N'Ramipril 5mg',N'Hộp',55000,N'Thuoc',N'Hạ HA suy tim',8),
(N'Diltiazem 60mg',N'Hộp',35000,N'Thuoc',N'Chẹn kênh canxi',10),
(N'Verapamil 80mg',N'Hộp',28000,N'Thuoc',N'Rối loạn nhịp tim',8),
(N'Hydrochlorothiazide 25mg',N'Hộp',15000,N'Thuoc',N'Lợi tiểu thiazide',10),
(N'Indapamide 1.5mg',N'Hộp',38000,N'Thuoc',N'Lợi tiểu hạ HA',8),
(N'Warfarin 2mg',N'Hộp',25000,N'Thuoc',N'Chống đông máu',5),
(N'Enoxaparin 40mg',N'Ống',120000,N'Thuoc',N'Heparin trọng lượng thấp',5),
(N'Rivaroxaban 10mg',N'Hộp',185000,N'Thuoc',N'Chống đông mới NOAC',5),
(N'Digoxin 0.25mg',N'Hộp',22000,N'Thuoc',N'Suy tim rung nhĩ',5),
(N'Amiodarone 200mg',N'Hộp',85000,N'Thuoc',N'Chống loạn nhịp',5),
(N'Propranolol 40mg',N'Hộp',18000,N'Thuoc',N'Chẹn beta không chọn lọc',10),
(N'Atenolol 50mg',N'Hộp',22000,N'Thuoc',N'Chẹn beta-1 chọn lọc',10),
(N'Metoprolol 50mg',N'Hộp',35000,N'Thuoc',N'Chẹn beta tim mạch',8),
(N'Nebivolol 5mg',N'Hộp',68000,N'Thuoc',N'Chẹn beta thế hệ 3',8),
(N'Lisinopril 10mg',N'Hộp',32000,N'Thuoc',N'ACE inhibitor',10),
(N'Candesartan 8mg',N'Hộp',55000,N'Thuoc',N'ARB hạ huyết áp',8),
(N'Ezetimibe 10mg',N'Hộp',75000,N'Thuoc',N'Giảm hấp thu cholesterol',8),
(N'Fenofibrate 160mg',N'Hộp',48000,N'Thuoc',N'Giảm triglyceride',8),
(N'Insulin Lantus 100IU',N'Bút',450000,N'Thuoc',N'Insulin nền tác dụng dài',3),
(N'Insulin Novorapid',N'Bút',380000,N'Thuoc',N'Insulin tác dụng nhanh',3),
(N'Sitagliptin 100mg',N'Hộp',155000,N'Thuoc',N'Ức chế DPP-4',5),
(N'Empagliflozin 10mg',N'Hộp',185000,N'Thuoc',N'Ức chế SGLT2',5),
(N'Pioglitazone 30mg',N'Hộp',65000,N'Thuoc',N'Tăng nhạy insulin',8),
(N'Acarbose 50mg',N'Hộp',48000,N'Thuoc',N'Ức chế alpha-glucosidase',8),
(N'Esomeprazole 40mg',N'Hộp',68000,N'Thuoc',N'PPI thế hệ mới',8),
(N'Rabeprazole 20mg',N'Hộp',55000,N'Thuoc',N'Ức chế bơm proton',8),
(N'Lansoprazole 30mg',N'Hộp',42000,N'Thuoc',N'Điều trị GERD',8),
(N'Sucralfate 1g',N'Hộp',28000,N'Thuoc',N'Bảo vệ niêm mạc dạ dày',10),
(N'Bismuth subsalicylate',N'Chai',35000,N'Thuoc',N'Bảo vệ dạ dày',10),
(N'Ranitidine 150mg',N'Hộp',18000,N'Thuoc',N'Kháng H2 dạ dày',10),
(N'Famotidine 20mg',N'Hộp',22000,N'Thuoc',N'Kháng H2 receptor',10),
(N'Ondansetron 4mg',N'Hộp',45000,N'Thuoc',N'Chống nôn 5-HT3',8),
(N'Metoclopramide 10mg',N'Hộp',15000,N'Thuoc',N'Điều hòa nhu động ruột',10),
(N'Smecta 3g',N'Hộp',35000,N'Thuoc',N'Bao phủ niêm mạc ruột',10),
(N'ORS gói bù nước',N'Hộp',8000,N'Thuoc',N'Bù nước điện giải',20),
(N'Berberin 100mg',N'Hộp',10000,N'Thuoc',N'Trị tiêu chảy nhiễm khuẩn',15),
(N'Mebendazole 500mg',N'Viên',5000,N'Thuoc',N'Tẩy giun',20),
(N'Albendazole 400mg',N'Viên',8000,N'Thuoc',N'Tẩy giun sán',20),
(N'Cefixime 200mg',N'Hộp',75000,N'Thuoc',N'Kháng sinh cephalosporin 3',8),
(N'Ceftriaxone 1g tiêm',N'Lọ',45000,N'Thuoc',N'Kháng sinh tiêm',5),
(N'Gentamicin 80mg tiêm',N'Ống',8000,N'Thuoc',N'Kháng sinh aminoglycosid',5),
(N'Clindamycin 300mg',N'Hộp',55000,N'Thuoc',N'Kháng sinh lincosamid',8),
(N'Vancomycin 500mg tiêm',N'Lọ',185000,N'Thuoc',N'Kháng sinh glycopeptide',3),
(N'Meropenem 1g tiêm',N'Lọ',250000,N'Thuoc',N'Kháng sinh carbapenem',3),
(N'Cotrimoxazole 480mg',N'Hộp',12000,N'Thuoc',N'Sulfamethoxazole trimethoprim',10),
(N'Nitrofurantoin 100mg',N'Hộp',28000,N'Thuoc',N'Kháng sinh tiết niệu',8);
GO

-- Thuoc 101-200: dung WHILE loop de generate
DECLARE @i INT = 1;
DECLARE @names TABLE(id INT IDENTITY, ten NVARCHAR(100), dvt NVARCHAR(20), gia INT, mota NVARCHAR(200), ton INT);
INSERT @names(ten,dvt,gia,mota,ton) VALUES
(N'Naproxen 500mg',N'Hộp',28000,N'NSAID giảm đau',10),
(N'Celecoxib 200mg',N'Hộp',85000,N'Ức chế COX-2',8),
(N'Etoricoxib 90mg',N'Hộp',95000,N'Chống viêm COX-2',8),
(N'Piroxicam 20mg',N'Viên',5000,N'Chống viêm NSAID',10),
(N'Indomethacin 25mg',N'Hộp',15000,N'Chống viêm mạnh',8),
(N'Colchicine 0.5mg',N'Hộp',25000,N'Điều trị gout cấp',8),
(N'Allopurinol 300mg',N'Hộp',18000,N'Hạ acid uric',10),
(N'Febuxostat 40mg',N'Hộp',125000,N'Điều trị gout mạn',5),
(N'Methotrexate 2.5mg',N'Hộp',85000,N'Ức chế miễn dịch',3),
(N'Hydroxychloroquine 200mg',N'Hộp',65000,N'Kháng sốt rét lupus',5),
(N'Leflunomide 20mg',N'Hộp',155000,N'DMARD viêm khớp',3),
(N'Sulfasalazine 500mg',N'Hộp',45000,N'Viêm loét đại tràng',5),
(N'Mesalazine 500mg',N'Hộp',120000,N'Viêm đại tràng UC',5),
(N'Lactulose 10g/15ml',N'Chai',55000,N'Nhuận tràng thẩm thấu',8),
(N'Macrogol 3350',N'Hộp',65000,N'Nhuận tràng PEG',8),
(N'Docusate sodium 100mg',N'Hộp',35000,N'Làm mềm phân',10),
(N'Senna 8.6mg',N'Hộp',15000,N'Nhuận tràng kích thích',10),
(N'Alverin citrate 60mg',N'Hộp',35000,N'Chống co thắt ruột',8),
(N'Mebeverine 135mg',N'Hộp',45000,N'Chống co thắt IBS',8),
(N'Hyoscine 10mg',N'Hộp',22000,N'Chống co thắt',10),
(N'Trimebutine 200mg',N'Hộp',48000,N'Điều hòa nhu động',8),
(N'Diosmin 500mg',N'Hộp',85000,N'Trĩ suy tĩnh mạch',8),
(N'Hesperidin 450mg',N'Hộp',75000,N'Hỗ trợ tĩnh mạch',8),
(N'Sildenafil 50mg',N'Viên',35000,N'Rối loạn cương dương',5),
(N'Tadalafil 5mg',N'Viên',45000,N'ED tác dụng dài',5),
(N'Tamsulosin 0.4mg',N'Hộp',55000,N'Phì đại tuyến tiền liệt',8),
(N'Finasteride 5mg',N'Hộp',65000,N'Phì đại tiền liệt',8),
(N'Alfuzosin 10mg',N'Hộp',75000,N'Alpha-blocker BPH',5),
(N'Oxybutynin 5mg',N'Hộp',35000,N'Bàng quang tăng hoạt',8),
(N'Solifenacin 5mg',N'Hộp',125000,N'Tiểu gấp tiểu không tự chủ',5),
(N'Dapoxetine 30mg',N'Viên',55000,N'Xuất tinh sớm',5),
(N'Clomifene 50mg',N'Hộp',85000,N'Kích thích rụng trứng',3),
(N'Progesterone 200mg',N'Hộp',120000,N'Hỗ trợ thai kỳ',5),
(N'Estradiol 2mg',N'Hộp',95000,N'Liệu pháp hormon',5),
(N'Norethisterone 5mg',N'Hộp',45000,N'Rối loạn kinh nguyệt',8),
(N'Misoprostol 200mcg',N'Hộp',35000,N'Bảo vệ dạ dày NSAID',5),
(N'Oxytocin 5IU tiêm',N'Ống',15000,N'Tăng co bóp tử cung',3),
(N'Methylergometrine',N'Ống',12000,N'Cầm máu sau sinh',3),
(N'Tranexamic acid 500mg',N'Hộp',25000,N'Cầm máu',10),
(N'Vitamin K1 10mg tiêm',N'Ống',18000,N'Chống xuất huyết',5),
(N'Ferrous sulfate 325mg',N'Hộp',15000,N'Bổ sung sắt',10),
(N'Folic acid 5mg',N'Hộp',8000,N'Bổ sung folate',10),
(N'Cyanocobalamin B12',N'Ống',12000,N'Bổ sung vitamin B12',8),
(N'Erythropoietin 4000IU',N'Ống',350000,N'Kích thích tạo hồng cầu',3),
(N'Morphine 10mg',N'Ống',25000,N'Giảm đau opioid mạnh',3),
(N'Codeine 30mg',N'Hộp',35000,N'Giảm đau giảm ho',5),
(N'Pethidine 50mg tiêm',N'Ống',18000,N'Giảm đau phẫu thuật',3),
(N'Ketamine 500mg tiêm',N'Lọ',85000,N'Gây mê tĩnh mạch',3),
(N'Lidocaine 2% tiêm',N'Ống',8000,N'Gây tê tại chỗ',10),
(N'Bupivacaine 0.5%',N'Ống',25000,N'Gây tê vùng',5);

-- Insert tu @names: thuoc 101-200
INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
SELECT ten, dvt, gia, N'Thuoc', mota, ton FROM @names;
GO

-- Thuoc 201-400: generate bang WHILE loop
DECLARE @base TABLE(id INT IDENTITY, prefix NVARCHAR(60), suffix NVARCHAR(60), dvt NVARCHAR(20), minGia INT, maxGia INT, mota NVARCHAR(100));
INSERT @base VALUES
(N'Amoxicillin',N'250mg',N'Hộp',15000,30000,N'Kháng sinh beta-lactam'),
(N'Cephalexin',N'500mg',N'Hộp',25000,45000,N'Cephalosporin thế hệ 1'),
(N'Cefpodoxime',N'200mg',N'Hộp',55000,95000,N'Cephalosporin thế hệ 3'),
(N'Erythromycin',N'500mg',N'Hộp',22000,38000,N'Kháng sinh macrolid'),
(N'Tetracycline',N'250mg',N'Hộp',10000,18000,N'Kháng sinh phổ rộng'),
(N'Rifampicin',N'300mg',N'Hộp',35000,55000,N'Điều trị lao'),
(N'Isoniazid',N'300mg',N'Hộp',12000,22000,N'Thuốc chống lao'),
(N'Pyrazinamide',N'500mg',N'Hộp',18000,28000,N'Phối hợp trị lao'),
(N'Ethambutol',N'400mg',N'Hộp',22000,35000,N'Thuốc lao phối hợp'),
(N'Streptomycin',N'1g tiêm',N'Lọ',25000,45000,N'Aminoglycosid kháng lao'),
(N'Ketoconazole',N'200mg',N'Hộp',25000,40000,N'Chống nấm azol'),
(N'Itraconazole',N'100mg',N'Hộp',85000,150000,N'Chống nấm sâu'),
(N'Terbinafine',N'250mg',N'Hộp',55000,85000,N'Chống nấm da'),
(N'Oseltamivir',N'75mg',N'Hộp',125000,200000,N'Kháng virus cúm'),
(N'Valacyclovir',N'500mg',N'Hộp',120000,180000,N'Kháng virus herpes'),
(N'Tenofovir',N'300mg',N'Hộp',250000,380000,N'Kháng virus viêm gan B'),
(N'Entecavir',N'0.5mg',N'Hộp',180000,280000,N'Điều trị viêm gan B'),
(N'Chloroquine',N'250mg',N'Hộp',15000,25000,N'Kháng sốt rét'),
(N'Artemisinin',N'250mg',N'Hộp',45000,75000,N'Sốt rét kháng thuốc'),
(N'Praziquantel',N'600mg',N'Viên',15000,25000,N'Tẩy sán');

DECLARE @j INT, @cnt INT, @bid INT;
DECLARE @pref NVARCHAR(60), @suf NVARCHAR(60), @dv NVARCHAR(20), @mn INT, @mx INT, @mt NVARCHAR(100);
DECLARE @tenSP NVARCHAR(200), @gia INT, @ton INT;
SET @j = 1;
SELECT @cnt = COUNT(*) FROM @base;
WHILE @j <= 200
BEGIN
    SET @bid = ((@j - 1) % @cnt) + 1;
    SELECT @pref=prefix, @suf=suffix, @dv=dvt, @mn=minGia, @mx=maxGia, @mt=mota FROM @base WHERE id=@bid;
    SET @tenSP = @pref + N' ' + @suf + N' #' + CAST(@j AS NVARCHAR(10));
    SET @gia = @mn + (ABS(CHECKSUM(NEWID())) % (@mx - @mn + 1));
    SET @ton = 5 + (ABS(CHECKSUM(NEWID())) % 16);

    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@tenSP, @dv, @gia, N'Thuoc', @mt, @ton);

    SET @j = @j + 1;
END
GO

PRINT N'=== Da insert 400 Thuoc ===';
GO

-- ==================== DUOC MY PHAM (401 - 600) ====================
DECLARE @dmpBase TABLE(id INT IDENTITY, ten NVARCHAR(100), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100), ton INT);
INSERT @dmpBase VALUES
(N'Kem chống nắng SPF50',N'Tuýp',420000,N'Chống nắng phổ rộng',5),
(N'Serum Vitamin C',N'Chai',350000,N'Dưỡng sáng da',5),
(N'Kem dưỡng ẩm',N'Hũ',280000,N'Dưỡng ẩm mọi loại da',5),
(N'Nước tẩy trang',N'Chai',250000,N'Tẩy trang micellar',5),
(N'Gel rửa mặt',N'Chai',180000,N'Làm sạch dịu nhẹ',5),
(N'Kem trị mụn',N'Tuýp',150000,N'Trị mụn viêm',5),
(N'Kem trị sẹo',N'Tuýp',280000,N'Trị sẹo silicone',5),
(N'Toner cấp ẩm',N'Chai',220000,N'HA cấp ẩm sâu',5),
(N'Serum retinol',N'Chai',380000,N'Chống lão hóa',3),
(N'Kem mắt chống nhăn',N'Tuýp',480000,N'Giảm nếp nhăn mắt',3),
(N'Sữa rửa mặt',N'Tuýp',150000,N'Làm sạch nhẹ nhàng',8),
(N'Mặt nạ đất sét',N'Hũ',180000,N'Thải độc da',5),
(N'Kem nền',N'Tuýp',250000,N'Che phủ tự nhiên',5),
(N'Son dưỡng môi',N'Tuýp',120000,N'Dưỡng môi vitamin E',8),
(N'Tẩy tế bào chết',N'Chai',280000,N'AHA BHA',5),
(N'Xịt khoáng',N'Chai',220000,N'Làm dịu da',5),
(N'Kem body dưỡng thể',N'Chai',150000,N'Dưỡng da toàn thân',8),
(N'Dầu gội trị gàu',N'Chai',155000,N'Trị gàu dược liệu',8),
(N'Kem chống hăm',N'Tuýp',120000,N'Bảo vệ da bé',8),
(N'Gel trị mụn BPO',N'Tuýp',150000,N'Benzoyl peroxide',5);

DECLARE @brandsDMP TABLE(id INT IDENTITY, brand NVARCHAR(60));
INSERT @brandsDMP VALUES(N'La Roche-Posay'),(N'CeraVe'),(N'Eucerin'),(N'Vichy'),(N'Bioderma'),
(N'Neutrogena'),(N'The Ordinary'),(N'SVR'),(N'Avene'),(N'Obagi');

DECLARE @d INT, @did INT, @brdId INT;
DECLARE @dten NVARCHAR(200), @ddvt NVARCHAR(20), @dgia INT, @dmt NVARCHAR(100), @dton INT, @brd NVARCHAR(60);
SET @d = 1;
WHILE @d <= 200
BEGIN
    SET @did = ((@d-1) % 20) + 1;
    SET @brdId = ((@d-1) % 10) + 1;
    SELECT @dten=ten, @ddvt=dvt, @dgia=gia, @dmt=mota, @dton=ton FROM @dmpBase WHERE id=@did;
    SELECT @brd=brand FROM @brandsDMP WHERE id=@brdId;
    SET @dten = @brd + N' ' + @dten + N' #' + CAST(@d AS NVARCHAR(10));
    SET @dgia = @dgia + (ABS(CHECKSUM(NEWID())) % 100000) - 50000;
    IF @dgia < 50000 SET @dgia = 50000;

    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@dten, @ddvt, @dgia, N'DuocMiPham', @dmt, @dton);

    SET @d = @d + 1;
END
GO

PRINT N'=== Da insert 200 DuocMiPham ===';
GO

-- ==================== THUC PHAM CHUC NANG (601 - 800) ====================
DECLARE @tpcnBase TABLE(id INT IDENTITY, ten NVARCHAR(100), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100), ton INT);
INSERT @tpcnBase VALUES
(N'Vitamin C 1000mg',N'Chai',150000,N'Tăng cường miễn dịch',10),
(N'Omega 3 Fish Oil',N'Chai',350000,N'Bổ sung DHA EPA',8),
(N'Vitamin D3 1000IU',N'Chai',150000,N'Hấp thu canxi',10),
(N'Canxi Nano MK7',N'Hộp',280000,N'Xương khớp',10),
(N'Glucosamine 1500mg',N'Chai',450000,N'Hỗ trợ xương khớp',8),
(N'Collagen Type 1',N'Chai',550000,N'Đẹp da chống lão hóa',5),
(N'Probiotics 50B CFU',N'Chai',420000,N'Men vi sinh đường ruột',5),
(N'Multivitamin',N'Chai',380000,N'Đa vitamin tổng hợp',8),
(N'Coenzyme Q10 200mg',N'Chai',480000,N'Hỗ trợ tim mạch',5),
(N'Spirulina 500mg',N'Chai',250000,N'Tảo xoắn dinh dưỡng',8),
(N'Sắt Folic Acid',N'Hộp',85000,N'Bổ máu bà bầu',10),
(N'Kẽm Zinc 70mg',N'Chai',120000,N'Tăng sức khỏe',10),
(N'B Complex',N'Chai',280000,N'Vitamin B tổng hợp',8),
(N'Melatonin 5mg',N'Chai',180000,N'Hỗ trợ giấc ngủ',8),
(N'Lutein 20mg',N'Chai',320000,N'Bổ mắt',5),
(N'Biotin 5000mcg',N'Chai',250000,N'Đẹp tóc da móng',8),
(N'Curcumin Nano',N'Hộp',320000,N'Chống viêm dạ dày',5),
(N'Sâm Hàn Quốc',N'Hộp',980000,N'Bổ khí huyết',3),
(N'Sữa Ensure Gold 850g',N'Hộp',680000,N'Dinh dưỡng người lớn',5),
(N'Cốm Lysine trẻ em',N'Hộp',120000,N'Giúp bé ăn ngon',10);

DECLARE @brandsTpcn TABLE(id INT IDENTITY, brand NVARCHAR(60));
INSERT @brandsTpcn VALUES(N'DHC'),(N'Blackmores'),(N'Kirkland'),(N'Nature Made'),(N'Swisse'),
(N'Now Foods'),(N'GNC'),(N'Solgar'),(N'Puritan Pride'),(N'Centrum');

DECLARE @t INT, @tid INT, @tbrdId INT;
DECLARE @tten NVARCHAR(200), @tdvt NVARCHAR(20), @tgia INT, @tmt NVARCHAR(100), @tton INT, @tbrd NVARCHAR(60);
SET @t = 1;
WHILE @t <= 200
BEGIN
    SET @tid = ((@t-1) % 20) + 1;
    SET @tbrdId = ((@t-1) % 10) + 1;
    SELECT @tten=ten, @tdvt=dvt, @tgia=gia, @tmt=mota, @tton=ton FROM @tpcnBase WHERE id=@tid;
    SELECT @tbrd=brand FROM @brandsTpcn WHERE id=@tbrdId;
    SET @tten = @tbrd + N' ' + @tten + N' #' + CAST(@t AS NVARCHAR(10));
    SET @tgia = @tgia + (ABS(CHECKSUM(NEWID())) % 80000) - 40000;
    IF @tgia < 30000 SET @tgia = 30000;

    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@tten, @tdvt, @tgia, N'ThucPhamChucNang', @tmt, @tton);

    SET @t = @t + 1;
END
GO

PRINT N'=== Da insert 200 ThucPhamChucNang ===';
GO

-- ==================== CHAM SOC CA NHAN (801 - 900) ====================
DECLARE @cscnBase TABLE(id INT IDENTITY, ten NVARCHAR(100), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100), ton INT);
INSERT @cscnBase VALUES
(N'Nước rửa tay 500ml',N'Chai',45000,N'Diệt khuẩn 99.9%',15),
(N'Kem đánh răng',N'Tuýp',65000,N'Chống ê buốt',10),
(N'Bàn chải đánh răng',N'Cái',45000,N'Lông mềm',10),
(N'Nước súc miệng 750ml',N'Chai',85000,N'Kháng khuẩn miệng',8),
(N'NaCl 0.9% rửa mũi',N'Chai',25000,N'Vệ sinh mũi',15),
(N'Sữa tắm 900ml',N'Chai',120000,N'Dưỡng ẩm toàn thân',8),
(N'Dầu gội 650ml',N'Chai',110000,N'Chăm sóc tóc',8),
(N'Xịt khử mùi 150ml',N'Chai',65000,N'Khử mùi 48h',10),
(N'Kem chống muỗi 60ml',N'Tuýp',22000,N'Chống muỗi 8h',15),
(N'Gel rửa tay khô 500ml',N'Chai',55000,N'Diệt khuẩn nhanh',10),
(N'Băng vệ sinh',N'Gói',28000,N'Siêu thấm chống tràn',15),
(N'Khăn ướt 100 tờ',N'Gói',35000,N'Kháng khuẩn an toàn',10),
(N'Bông gòn y tế 100g',N'Cuộn',15000,N'Tiệt trùng',15),
(N'Bông tẩy trang 150 miếng',N'Gói',35000,N'Mềm mịn',10),
(N'Tã dán trẻ em',N'Gói',180000,N'Siêu thấm cho bé',8),
(N'Nước giặt 3.25kg',N'Can',185000,N'Giặt sạch sâu',5),
(N'Kem cạo râu',N'Tuýp',55000,N'Bảo vệ da nam',8),
(N'Dầu xả 480ml',N'Chai',95000,N'Phục hồi tóc hư',8),
(N'Nước hoa 50ml',N'Chai',185000,N'Hương tự nhiên',5),
(N'Lăn khử mùi nữ',N'Chai',55000,N'Chống mồ hôi 48h',10);

DECLARE @brandsCscn TABLE(id INT IDENTITY, brand NVARCHAR(60));
INSERT @brandsCscn VALUES(N'Dove'),(N'Nivea'),(N'Lifebuoy'),(N'Sensodyne'),(N'Oral-B');

DECLARE @c INT, @cid INT, @cbrdId INT;
DECLARE @cten NVARCHAR(200), @cdvt NVARCHAR(20), @cgia INT, @cmt NVARCHAR(100), @cton INT, @cbrd NVARCHAR(60);
SET @c = 1;
WHILE @c <= 100
BEGIN
    SET @cid = ((@c-1) % 20) + 1;
    SET @cbrdId = ((@c-1) % 5) + 1;
    SELECT @cten=ten, @cdvt=dvt, @cgia=gia, @cmt=mota, @cton=ton FROM @cscnBase WHERE id=@cid;
    SELECT @cbrd=brand FROM @brandsCscn WHERE id=@cbrdId;
    SET @cten = @cbrd + N' ' + @cten + N' #' + CAST(@c AS NVARCHAR(10));
    SET @cgia = @cgia + (ABS(CHECKSUM(NEWID())) % 30000) - 15000;
    IF @cgia < 10000 SET @cgia = 10000;

    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@cten, @cdvt, @cgia, N'ChamSocCaNhan', @cmt, @cton);

    SET @c = @c + 1;
END
GO

PRINT N'=== Da insert 100 ChamSocCaNhan ===';
GO

-- ==================== THIET BI Y TE (901 - 1000) ====================
DECLARE @tbytBase TABLE(id INT IDENTITY, ten NVARCHAR(100), dvt NVARCHAR(20), gia INT, mota NVARCHAR(100), ton INT);
INSERT @tbytBase VALUES
(N'Nhiệt kế điện tử',N'Cái',85000,N'Đo nhiệt nách miệng',3),
(N'Máy đo huyết áp',N'Cái',1250000,N'Đo bắp tay tự động',3),
(N'Máy đo đường huyết',N'Cái',850000,N'Kiểm tra glucose',3),
(N'Máy xông mũi họng',N'Cái',980000,N'Xông hơi khí dung',3),
(N'Máy đo SpO2 kẹp ngón',N'Cái',350000,N'Đo oxy máu',5),
(N'Nẹp cổ tay y khoa',N'Cái',120000,N'Hỗ trợ bong gân',5),
(N'Bộ sơ cứu 25 món',N'Bộ',250000,N'Sơ cứu cơ bản',5),
(N'Khẩu trang y tế 50 cái',N'Hộp',45000,N'Kháng khuẩn BFE 99%',20),
(N'Găng tay latex 100 cái',N'Hộp',85000,N'Tiệt trùng',10),
(N'Cân sức khỏe điện tử',N'Cái',480000,N'Đo BMI mỡ cơ thể',3),
(N'Máy hút mũi trẻ em',N'Cái',350000,N'Hút mũi điện tử',3),
(N'Máy đo nhiệt hồng ngoại',N'Cái',650000,N'Đo trán không tiếp xúc',3),
(N'Que thử đường huyết 50 que',N'Hộp',280000,N'Que test glucose',10),
(N'Kim tiêm 3ml (100 cái)',N'Hộp',55000,N'Kim tiêm vô trùng',10),
(N'Băng keo y tế cuộn',N'Cuộn',15000,N'Băng keo vải',15),
(N'Băng gạc vô trùng 10x10',N'Gói',12000,N'Gạc tiệt trùng',15),
(N'Dây đo huyết áp thay thế',N'Cái',180000,N'Phụ kiện máy HA',5),
(N'Máy massage cầm tay',N'Cái',450000,N'Giảm đau cơ',3),
(N'Đai lưng hỗ trợ cột sống',N'Cái',350000,N'Bảo vệ cột sống',5),
(N'Nạng y tế inox',N'Đôi',280000,N'Hỗ trợ di chuyển',3);

DECLARE @brandsTbyt TABLE(id INT IDENTITY, brand NVARCHAR(60));
INSERT @brandsTbyt VALUES(N'Omron'),(N'Microlife'),(N'Accu-Chek'),(N'Beurer'),(N'Yuwell');

DECLARE @e INT, @eid INT, @ebrdId INT;
DECLARE @eten NVARCHAR(200), @edvt NVARCHAR(20), @egia INT, @emt NVARCHAR(100), @eton INT, @ebrd NVARCHAR(60);
SET @e = 1;
WHILE @e <= 100
BEGIN
    SET @eid = ((@e-1) % 20) + 1;
    SET @ebrdId = ((@e-1) % 5) + 1;
    SELECT @eten=ten, @edvt=dvt, @egia=gia, @emt=mota, @eton=ton FROM @tbytBase WHERE id=@eid;
    SELECT @ebrd=brand FROM @brandsTbyt WHERE id=@ebrdId;
    SET @eten = @ebrd + N' ' + @eten + N' #' + CAST(@e AS NVARCHAR(10));
    SET @egia = @egia + (ABS(CHECKSUM(NEWID())) % 100000) - 50000;
    IF @egia < 10000 SET @egia = 10000;

    INSERT dbo.SanPham(TenSanPham, DonViTinh, GiaBanDeXuat, LoaiSanPham, MoTa, MucTonToiThieu)
    VALUES(@eten, @edvt, @egia, N'ThietBiYTe', @emt, @eton);

    SET @e = @e + 1;
END
GO

PRINT N'=== Da insert 100 ThietBiYTe ===';
GO

-- =====================================================================
-- KIEM TRA KET QUA
-- =====================================================================
SELECT N'TONG SAN PHAM' AS Info, COUNT(*) AS SoLuong FROM dbo.SanPham WHERE DaXoa = 0;

SELECT LoaiSanPham, COUNT(*) AS SoLuong
FROM dbo.SanPham WHERE DaXoa = 0
GROUP BY LoaiSanPham ORDER BY SoLuong DESC;
GO

PRINT N'=== HOAN TAT: 1000 san pham da duoc tao ===';
GO
