import common.ConnectDB;
import java.sql.Connection;
import java.sql.Statement;

public class SqlUpgrader {
    public static void main(String[] args) {
        try {
            Connection con = ConnectDB.getCon();
            Statement stmt = con.createStatement();
            
            // 1. ALTER TABLE SanPham
            try {
                stmt.executeUpdate("ALTER TABLE SanPham DROP COLUMN DonViTinh");
            } catch (Exception e) {}
            try {
                stmt.executeUpdate("ALTER TABLE SanPham DROP COLUMN LoaiSanPham");
            } catch (Exception e) {}
            try {
                stmt.executeUpdate("ALTER TABLE SanPham DROP COLUMN MucTonToiThieu");
            } catch (Exception e) {}

            // 2. ALTER TABLE LoHang
            try {
                stmt.executeUpdate("ALTER TABLE LoHang ADD LoaiHinhBan NVARCHAR(20) DEFAULT N'Bán sỉ'");
            } catch (Exception e) {}
            try {
                stmt.executeUpdate("ALTER TABLE LoHang ADD ThoiGianNhap DATETIME DEFAULT GETDATE()");
            } catch (Exception e) {}
            try {
                stmt.executeUpdate("ALTER TABLE LoHang ADD TongSoVien_Lo INT");
            } catch (Exception e) {}
            
            // 3. Drop then Create View
            try {
                stmt.executeUpdate("DROP VIEW v_TonKhoSanPham");
            } catch (Exception e) {}
            
            String viewSql = "CREATE VIEW v_TonKhoSanPham AS " +
                             "SELECT sp.MaSanPham, sp.TenSanPham, " +
                             "ISNULL(SUM(lh.TongSoVien_Lo), 0) AS TongTon, " +
                             "MIN(lh.HanSuDung) AS HanSuDungGanNhat " +
                             "FROM SanPham sp " +
                             "LEFT JOIN LoHang lh ON sp.MaSanPham = lh.MaSanPham AND lh.TongSoVien_Lo > 0 " +
                             "GROUP BY sp.MaSanPham, sp.TenSanPham";
            stmt.executeUpdate(viewSql);
            
            System.out.println("====== DB SCHEMA UPGRADE SUCCESS =====");
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("===== DATABASE UPGRADE FAILED =====");
        }
    }
}
