package tools;

import java.sql.*;
import common.ConnectDB;

/**
 * Cập nhật toàn bộ tên sản phẩm sang tiếng Việt có dấu đầy đủ.
 * Chạy 1 lần duy nhất.
 */
public class SeedVietnameseNames {

    public static void main(String[] args) {
        System.out.println("=== Bắt đầu cập nhật tên tiếng Việt có dấu ===");

        // 50 thuốc cơ bản (MaSanPham 1-50)
        String[][] thuoc = {
            {"1", "Paracetamol 500mg", "Hộp", "Giảm đau, hạ sốt"},
            {"2", "Amoxicillin 500mg", "Hộp", "Kháng sinh penicillin"},
            {"3", "Ibuprofen 400mg", "Hộp", "Chống viêm, giảm đau"},
            {"4", "Cefuroxim 500mg", "Hộp", "Kháng sinh cephalosporin"},
            {"5", "Azithromycin 250mg", "Hộp", "Kháng sinh macrolid"},
            {"6", "Levofloxacin 500mg", "Hộp", "Kháng sinh quinolone"},
            {"7", "Metformin 850mg", "Hộp", "Điều trị tiểu đường type 2"},
            {"8", "Amlodipine 5mg", "Hộp", "Hạ huyết áp"},
            {"9", "Losartan 50mg", "Hộp", "Điều trị tăng huyết áp"},
            {"10", "Omeprazole 20mg", "Hộp", "Ức chế bơm proton, dạ dày"},
            {"11", "Pantoprazole 40mg", "Hộp", "Điều trị loét dạ dày"},
            {"12", "Cetirizine 10mg", "Hộp", "Chống dị ứng"},
            {"13", "Loratadine 10mg", "Hộp", "Kháng histamine"},
            {"14", "Salbutamol 2mg", "Hộp", "Giãn phế quản"},
            {"15", "Montelukast 10mg", "Hộp", "Dự phòng hen suyễn"},
            {"16", "Prednisolone 5mg", "Hộp", "Chống viêm corticoid"},
            {"17", "Dexamethasone 0.5mg", "Hộp", "Corticosteroid"},
            {"18", "Diclofenac 75mg", "Hộp", "Chống viêm NSAID"},
            {"19", "Meloxicam 15mg", "Hộp", "Giảm đau khớp"},
            {"20", "Ciprofloxacin 500mg", "Hộp", "Kháng sinh fluoroquinolone"},
            {"21", "Clarithromycin 500mg", "Hộp", "Kháng sinh macrolid"},
            {"22", "Clopidogrel 75mg", "Hộp", "Chống kết tập tiểu cầu"},
            {"23", "Atorvastatin 20mg", "Hộp", "Giảm cholesterol"},
            {"24", "Rosuvastatin 10mg", "Hộp", "Hạ mỡ máu statin"},
            {"25", "Simvastatin 20mg", "Hộp", "Giảm lipid máu"},
            {"26", "Metronidazole 250mg", "Hộp", "Kháng khuẩn, kháng nấm"},
            {"27", "Fluconazole 150mg", "Viên", "Chống nấm"},
            {"28", "Acyclovir 800mg", "Hộp", "Kháng virus herpes"},
            {"29", "Domperidone 10mg", "Hộp", "Chống nôn"},
            {"30", "Loperamide 2mg", "Hộp", "Cầm tiêu chảy"},
            {"31", "Aspirin 81mg", "Hộp", "Chống đông máu"},
            {"32", "Tramadol 50mg", "Hộp", "Giảm đau opioid nhẹ"},
            {"33", "Gabapentin 300mg", "Hộp", "Giảm đau thần kinh"},
            {"34", "Sertraline 50mg", "Hộp", "Chống trầm cảm SSRI"},
            {"35", "Carbamazepine 200mg", "Hộp", "Chống động kinh"},
            {"36", "Levothyroxine 50mcg", "Hộp", "Hormon tuyến giáp"},
            {"37", "Doxycycline 100mg", "Hộp", "Kháng sinh tetracycline"},
            {"38", "Bisoprolol 5mg", "Hộp", "Chẹn beta, hạ huyết áp"},
            {"39", "Valsartan 80mg", "Hộp", "Chẹn thụ thể angiotensin"},
            {"40", "Captopril 25mg", "Hộp", "Ức chế men chuyển ACE"},
            {"41", "Warfarin 2mg", "Hộp", "Chống đông máu"},
            {"42", "Insulin Lantus 100IU", "Bút", "Insulin nền tác dụng dài"},
            {"43", "Esomeprazole 40mg", "Hộp", "PPI thế hệ mới"},
            {"44", "Cefixime 200mg", "Hộp", "Kháng sinh cephalosporin 3"},
            {"45", "Mebendazole 500mg", "Viên", "Tẩy giun"},
            {"46", "ORS bù nước", "Hộp", "Bù nước điện giải"},
            {"47", "Berberin 100mg", "Hộp", "Trị tiêu chảy nhiễm khuẩn"},
            {"48", "Vitamin C 500mg", "Hộp", "Bổ sung vitamin C"},
            {"49", "Vitamin B Complex", "Hộp", "Vitamin B tổng hợp"},
            {"50", "Calcium D3 500mg", "Hộp", "Bổ sung canxi"},
        };

        // Thuốc generated (51-400): prefix + suffix
        String[][] thuocGen = {
            {"Amoxicillin 250mg", "Hộp", "Kháng sinh beta-lactam"},
            {"Cephalexin 500mg", "Hộp", "Cephalosporin thế hệ 1"},
            {"Erythromycin 500mg", "Hộp", "Kháng sinh macrolid"},
            {"Naproxen 500mg", "Hộp", "NSAID giảm đau"},
            {"Celecoxib 200mg", "Hộp", "Ức chế COX-2"},
            {"Allopurinol 300mg", "Hộp", "Hạ acid uric"},
            {"Furosemide 40mg", "Hộp", "Lợi tiểu quai"},
            {"Propranolol 40mg", "Hộp", "Chẹn beta"},
            {"Amiodarone 200mg", "Hộp", "Chống loạn nhịp"},
            {"Digoxin 0.25mg", "Hộp", "Suy tim, rung nhĩ"},
            {"Fenofibrate 160mg", "Hộp", "Giảm triglyceride"},
            {"Metoclopramide 10mg", "Hộp", "Điều hòa nhu động ruột"},
            {"Spironolactone 25mg", "Hộp", "Lợi tiểu"},
            {"Colchicine 0.5mg", "Hộp", "Điều trị gout cấp"},
            {"Acarbose 50mg", "Hộp", "Hạ đường huyết"},
            {"Glimepiride 2mg", "Hộp", "Tiểu đường type 2"},
            {"Terbinafine 250mg", "Hộp", "Chống nấm da"},
            {"Oseltamivir 75mg", "Hộp", "Kháng virus cúm"},
            {"Tranexamic acid 500mg", "Hộp", "Cầm máu"},
            {"Folic acid 5mg", "Hộp", "Bổ sung folate"},
        };

        // Dược mỹ phẩm base
        String[][] dmpBase = {
            {"Kem chống nắng SPF50", "Tuýp", "Chống nắng phổ rộng"},
            {"Serum Vitamin C", "Chai", "Dưỡng sáng da"},
            {"Kem dưỡng ẩm", "Hũ", "Dưỡng ẩm mọi loại da"},
            {"Nước tẩy trang", "Chai", "Tẩy trang micellar"},
            {"Gel rửa mặt", "Chai", "Làm sạch dịu nhẹ"},
            {"Kem trị mụn", "Tuýp", "Trị mụn viêm"},
            {"Serum retinol", "Chai", "Chống lão hóa"},
            {"Toner cấp ẩm", "Chai", "HA cấp ẩm sâu"},
            {"Kem mắt chống nhăn", "Tuýp", "Giảm nếp nhăn mắt"},
            {"Sữa rửa mặt", "Tuýp", "Làm sạch nhẹ nhàng"},
        };
        String[] dmpBrands = {"La Roche-Posay", "CeraVe", "Eucerin", "Vichy", "Bioderma",
                              "Neutrogena", "The Ordinary", "SVR", "Avène", "Obagi"};

        // TPCN base
        String[][] tpcnBase = {
            {"Vitamin C 1000mg", "Chai", "Tăng cường miễn dịch"},
            {"Omega 3 Fish Oil", "Chai", "Bổ sung DHA, EPA"},
            {"Vitamin D3 1000IU", "Chai", "Hỗ trợ hấp thu canxi"},
            {"Canxi Nano MK7", "Hộp", "Bổ sung canxi xương khớp"},
            {"Glucosamine 1500mg", "Chai", "Hỗ trợ xương khớp"},
            {"Collagen Type 1", "Chai", "Đẹp da, chống lão hóa"},
            {"Probiotics 50B CFU", "Chai", "Men vi sinh đường ruột"},
            {"Multivitamin tổng hợp", "Chai", "Đa vitamin tổng hợp"},
            {"Coenzyme Q10 200mg", "Chai", "Hỗ trợ tim mạch"},
            {"Spirulina 500mg", "Chai", "Tảo xoắn dinh dưỡng"},
        };
        String[] tpcnBrands = {"DHC", "Blackmores", "Kirkland", "Nature Made", "Swisse",
                               "Now Foods", "GNC", "Solgar", "Puritan's Pride", "Centrum"};

        // CSCN base
        String[][] cscnBase = {
            {"Nước rửa tay 500ml", "Chai", "Diệt khuẩn 99.9%"},
            {"Kem đánh răng", "Tuýp", "Chống ê buốt"},
            {"Nước súc miệng 750ml", "Chai", "Kháng khuẩn miệng"},
            {"NaCl 0.9% rửa mũi", "Chai", "Vệ sinh mũi hàng ngày"},
            {"Sữa tắm dưỡng ẩm 900ml", "Chai", "Dưỡng ẩm toàn thân"},
            {"Dầu gội trị gàu 650ml", "Chai", "Chăm sóc tóc"},
            {"Gel rửa tay khô 500ml", "Chai", "Diệt khuẩn nhanh"},
            {"Bông gòn y tế 100g", "Cuộn", "Bông gòn tiệt trùng"},
            {"Kem chống muỗi 60ml", "Tuýp", "Chống muỗi 8 giờ"},
            {"Lăn khử mùi 150ml", "Chai", "Khử mùi 48 giờ"},
        };
        String[] cscnBrands = {"Dove", "Nivea", "Lifebuoy", "Oral-B", "Listerine"};

        // TBYT base
        String[][] tbytBase = {
            {"Nhiệt kế điện tử", "Cái", "Đo nhiệt nách, miệng"},
            {"Máy đo huyết áp", "Cái", "Đo bắp tay tự động"},
            {"Máy đo đường huyết", "Cái", "Kiểm tra glucose nhanh"},
            {"Máy xông mũi họng", "Cái", "Xông hơi khí dung"},
            {"Máy đo SpO2 kẹp ngón", "Cái", "Đo nồng độ oxy máu"},
            {"Khẩu trang y tế 50 cái", "Hộp", "Kháng khuẩn BFE 99%"},
            {"Găng tay latex 100 cái", "Hộp", "Không bột, tiệt trùng"},
            {"Cân sức khỏe điện tử", "Cái", "Đo BMI, mỡ cơ thể"},
            {"Băng keo y tế cuộn", "Cuộn", "Băng keo vải y tế"},
            {"Băng gạc vô trùng 10x10", "Gói", "Gạc tiệt trùng"},
        };
        String[] tbytBrands = {"Omron", "Microlife", "Accu-Chek", "Beurer", "Yuwell"};

        // NCC
        String[][] ncc = {
            {"1", "Công ty Dược phẩm A", "0901234567", "duoca@mail.com", "TP. Hồ Chí Minh"},
            {"2", "Công ty Dược phẩm B", "0912345678", "duocb@mail.com", "Hà Nội"},
            {"3", "Công ty Thiết bị Y tế C", "0923456789", "tbytc@mail.com", "Đà Nẵng"},
        };

        // Khách hàng
        String[][] kh = {
            {"1", "Nguyễn Văn An", "0911111111", "Quận 1, TP.HCM"},
            {"2", "Trần Thị Bình", "0922222222", "Quận 3, TP.HCM"},
            {"3", "Lê Văn Cường", "0933333333", "Quận 7, TP.HCM"},
            {"4", "Phạm Thị Dậu", "0944444444", "TP. Thủ Đức"},
            {"5", "Hoàng Văn Em", "0955555555", "Bình Dương"},
        };

        try (Connection conn = ConnectDB.getCon()) {
            conn.setAutoCommit(false);

            // 1. Update NCC
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE NhaCungCap SET TenNCC=?, SoDienThoai=?, Email=?, DiaChi=? WHERE MaNCC=?")) {
                for (String[] r : ncc) {
                    ps.setNString(1, r[1]); ps.setString(2, r[2]);
                    ps.setString(3, r[3]); ps.setNString(4, r[4]);
                    ps.setInt(5, Integer.parseInt(r[0]));
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 3 NCC");
            }

            // 2. Update KH
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE KhachHang SET HoTen=?, SoDienThoai=?, DiaChi=? WHERE MaKhachHang=?")) {
                for (String[] r : kh) {
                    ps.setNString(1, r[1]); ps.setString(2, r[2]);
                    ps.setNString(3, r[3]); ps.setInt(4, Integer.parseInt(r[0]));
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 5 KH");
            }

            // 3. Update 50 thuốc cơ bản
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (String[] r : thuoc) {
                    ps.setNString(1, r[1]); ps.setNString(2, r[2]);
                    ps.setNString(3, r[3]); ps.setInt(4, Integer.parseInt(r[0]));
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 50 thuốc cơ bản");
            }

            // 4. Update thuốc generated (51-400)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (int i = 51; i <= 400; i++) {
                    int idx = (i - 51) % thuocGen.length;
                    String ten = thuocGen[idx][0] + " #" + (i - 50);
                    ps.setNString(1, ten);
                    ps.setNString(2, thuocGen[idx][1]);
                    ps.setNString(3, thuocGen[idx][2]);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 350 thuốc generated");
            }

            // 5. Update DMP (401-600)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (int i = 401; i <= 600; i++) {
                    int idx = (i - 401) % dmpBase.length;
                    int bidx = (i - 401) % dmpBrands.length;
                    String ten = dmpBrands[bidx] + " " + dmpBase[idx][0] + " #" + (i - 400);
                    ps.setNString(1, ten);
                    ps.setNString(2, dmpBase[idx][1]);
                    ps.setNString(3, dmpBase[idx][2]);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 200 dược mỹ phẩm");
            }

            // 6. Update TPCN (601-800)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (int i = 601; i <= 800; i++) {
                    int idx = (i - 601) % tpcnBase.length;
                    int bidx = (i - 601) % tpcnBrands.length;
                    String ten = tpcnBrands[bidx] + " " + tpcnBase[idx][0] + " #" + (i - 600);
                    ps.setNString(1, ten);
                    ps.setNString(2, tpcnBase[idx][1]);
                    ps.setNString(3, tpcnBase[idx][2]);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 200 TPCN");
            }

            // 7. Update CSCN (801-900)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (int i = 801; i <= 900; i++) {
                    int idx = (i - 801) % cscnBase.length;
                    int bidx = (i - 801) % cscnBrands.length;
                    String ten = cscnBrands[bidx] + " " + cscnBase[idx][0] + " #" + (i - 800);
                    ps.setNString(1, ten);
                    ps.setNString(2, cscnBase[idx][1]);
                    ps.setNString(3, cscnBase[idx][2]);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 100 CSCN");
            }

            // 8. Update TBYT (901-1000)
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE SanPham SET TenSanPham=?, DonViTinh=?, MoTa=? WHERE MaSanPham=?")) {
                for (int i = 901; i <= 1000; i++) {
                    int idx = (i - 901) % tbytBase.length;
                    int bidx = (i - 901) % tbytBrands.length;
                    String ten = tbytBrands[bidx] + " " + tbytBase[idx][0] + " #" + (i - 900);
                    ps.setNString(1, ten);
                    ps.setNString(2, tbytBase[idx][1]);
                    ps.setNString(3, tbytBase[idx][2]);
                    ps.setInt(4, i);
                    ps.addBatch();
                }
                ps.executeBatch();
                System.out.println("[OK] Cập nhật 100 TBYT");
            }

            conn.commit();
            System.out.println("\n=== HOÀN TẤT: 1000 sản phẩm đã có tiếng Việt có dấu! ===");

            // Verify
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT TOP 10 MaSanPham, TenSanPham, DonViTinh, MoTa FROM SanPham ORDER BY MaSanPham")) {
                System.out.println("\n--- Mẫu 10 sản phẩm đầu ---");
                while (rs.next()) {
                    System.out.printf("  %d | %s | %s | %s%n",
                        rs.getInt(1), rs.getNString(2), rs.getNString(3), rs.getNString(4));
                }
            }

        } catch (Exception e) {
            System.err.println("LỖI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
