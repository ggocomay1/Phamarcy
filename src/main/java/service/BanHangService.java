package service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import common.AppLogger;
import dao.*;
import entity.*;
import service.dto.*;

/**
 * BanHangService – Service layer cho module Bán hàng (POS).
 * 
 * Điều phối nghiệp vụ bán hàng:
 * - Tìm kiếm sản phẩm
 * - Quản lý giỏ hàng (qua DB - HoaDonBan + ChiTietHoaDon)
 * - Tính toán tổng tiền
 * - Xử lý thanh toán (checkout orchestration)
 * - Validate nghiệp vụ (delegate to BanHangValidator)
 *
 * Panel chỉ gọi service, không trực tiếp thao tác DAO.
 *
 * @version 2.0
 */
public class BanHangService {

    private static final Logger LOG = AppLogger.get(BanHangService.class);
    private static final NumberFormat VND_FMT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private final HoaDonBanDao hoaDonDao;
    private final ChiTietHoaDonDao cthdDao;
    private final SanPhamDao spDao;
    private final KhachHangDao khDao;

    public BanHangService() {
        this.hoaDonDao = new HoaDonBanDao();
        this.cthdDao = new ChiTietHoaDonDao();
        this.spDao = new SanPhamDao();
        this.khDao = new KhachHangDao();
    }

    // ================================================================
    //  SẢN PHẨM
    // ================================================================

    public List<SanPham> searchProducts(String keyword) {
        return spDao.searchForSale(keyword);
    }

    /** Tìm sản phẩm theo barcode (quét mã) */
    public SanPham findByBarcode(String barcode) {
        var sp = spDao.findByBarcode(barcode);
        if (sp != null) LOG.info("[BARCODE] Found: " + barcode + " → " + sp.getTenSanPham());
        else LOG.warning("[BARCODE] Not found: " + barcode);
        return sp;
    }

    public String getStockStatus(SanPham sp) {
        if (sp.getTongTon() <= 0) return "Hết hàng";
        if (sp.getTongTon() <= sp.getMucTonToiThieu()) return "Sắp hết";
        return "Còn hàng";
    }

    public ServiceResult<Void> validateProductForSale(SanPham sp) {
        if (sp == null) return ServiceResult.fail("Sản phẩm không tồn tại.");
        if (sp.getTongTon() <= 0) return ServiceResult.fail("\"" + sp.getTenSanPham() + "\" đã hết hàng!");
        return ServiceResult.ok("Có thể bán");
    }

    // ================================================================
    //  HÓA ĐƠN / GIỎ HÀNG
    // ================================================================

    public ServiceResult<Integer> createInvoice(int maNguoiDung, Integer maKhachHang) {
        Integer id = hoaDonDao.createHoaDon(maNguoiDung, maKhachHang, "");
        if (id == null) {
            LOG.warning("[CHECKOUT] Không thể tạo hóa đơn cho user=" + maNguoiDung);
            return ServiceResult.fail("Không thể tạo hóa đơn! Kiểm tra kết nối DB.");
        }
        LOG.info("[CHECKOUT] Tạo hóa đơn HD" + String.format("%04d", id) + " (user=" + maNguoiDung + ")");
        return ServiceResult.ok(id);
    }

    public ServiceResult<Void> addProductToCart(int maHoaDon, int maSanPham, int soLuong) {
        boolean ok = hoaDonDao.sellProductFEFO(maHoaDon, maSanPham, soLuong, null);
        if (!ok) {
            LOG.warning("[CART] Thêm SP thất bại: HD=" + maHoaDon + " SP=" + maSanPham + " SL=" + soLuong);
            return ServiceResult.fail("Thêm thất bại! Kiểm tra tồn kho.");
        }
        LOG.info("[CART] Thêm SP=" + maSanPham + " SL=" + soLuong + " vào HD=" + maHoaDon);
        return ServiceResult.ok("Đã thêm vào giỏ");
    }

    public List<Object[]> getCartItems(int maHoaDon) {
        return cthdDao.getDetailForDisplay(maHoaDon);
    }

    public ServiceResult<Void> updateCartItemQuantity(int maCTHD, int newQuantity) {
        String qtyErr = BanHangValidator.validateQuantity(newQuantity);
        if (qtyErr != null) return ServiceResult.fail(qtyErr);
        boolean ok = cthdDao.updateSoLuong(maCTHD, newQuantity);
        if (!ok) return ServiceResult.fail("Cập nhật thất bại! Không đủ tồn kho.");
        return ServiceResult.ok("Đã cập nhật");
    }

    public ServiceResult<Void> removeCartItem(int maCTHD) {
        boolean ok = cthdDao.deleteChiTiet(maCTHD);
        if (!ok) return ServiceResult.fail("Xóa thất bại!");
        return ServiceResult.ok("Đã xóa");
    }

    public void clearCart(int maHoaDon) {
        var items = cthdDao.getDetailForDisplay(maHoaDon);
        for (var row : items) {
            int maCTHD = (int) row[0];
            cthdDao.deleteChiTiet(maCTHD);
        }
    }

    // ================================================================
    //  TỔNG KẾT GIỎ HÀNG (SaleSummary)
    // ================================================================

    /**
     * Tính SaleSummary từ dữ liệu DB cart hiện tại.
     * Đây là nguồn dữ liệu chính cho tổng tiền – không phải JTable.
     */
    public SaleSummary getSaleSummary(int maHoaDon) {
        var items = cthdDao.getDetailForDisplay(maHoaDon);
        var summary = new SaleSummary();
        BigDecimal total = BigDecimal.ZERO;
        int tongSL = 0;
        for (var row : items) {
            // col 8 = SoLuong, col 10 = ThanhTien (theo ChiTietHoaDonTableModel)
            if (row.length > 10) {
                tongSL += row[8] instanceof Integer ? (Integer) row[8] : 0;
                if (row[10] instanceof BigDecimal) total = total.add((BigDecimal) row[10]);
            }
        }
        summary.setSoMatHang(items.size());
        summary.setTongSoLuong(tongSL);
        summary.setTamTinh(total);
        summary.setTongThanhToan(total);
        return summary;
    }

    // ================================================================
    //  TÍNH TOÁN (legacy – dùng tạm cho BanHangPanel)
    // ================================================================

    public BigDecimal calculateTotal(javax.swing.table.DefaultTableModel model, int thanhTienCol) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            var v = model.getValueAt(i, thanhTienCol);
            if (v instanceof BigDecimal) total = total.add((BigDecimal) v);
        }
        return total;
    }

    public BigDecimal calculateChange(BigDecimal total, String tienKhachText) {
        String s = tienKhachText.trim().replaceAll("[^\\d]", "");
        if (s.isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(s).subtract(total).max(BigDecimal.ZERO);
    }

    // ================================================================
    //  KHÁCH HÀNG
    // ================================================================

    public List<KhachHang> searchCustomers(String keyword) {
        return khDao.searchByNameOrPhone(keyword);
    }

    // ================================================================
    //  THANH TOÁN – Legacy granular (backward compat)
    // ================================================================

    public ServiceResult<BigDecimal> validateCashPayment(BigDecimal total, String tienKhachText) {
        String s = tienKhachText.trim().replaceAll("[^\\d]", "");
        if (s.isEmpty()) return ServiceResult.fail("Vui lòng nhập tiền khách đưa!");
        BigDecimal tienKhach = new BigDecimal(s);
        if (tienKhach.compareTo(total) < 0)
            return ServiceResult.fail("Tiền chưa đủ!\nCần: " + VND_FMT.format(total));
        return ServiceResult.ok(tienKhach.subtract(total));
    }

    public ServiceResult<Void> validateTransferPayment(boolean confirmed) {
        if (!confirmed) return ServiceResult.fail("Vui lòng xác nhận đã nhận chuyển khoản!");
        return ServiceResult.ok("OK");
    }

    public ServiceResult<Void> validateCartForCheckout(Integer maHoaDon, int itemCount) {
        String err = BanHangValidator.validateCartNotEmpty(maHoaDon, itemCount);
        if (err != null) return ServiceResult.fail(err);
        return ServiceResult.ok("OK");
    }

    public ServiceResult<String> checkout(int maHoaDon, BigDecimal total, String paymentMethod) {
        String hdCode = "HD" + String.format("%04d", maHoaDon);
        String msg = "Thanh toán thành công!\nHĐ: " + hdCode + "\nTổng: " + VND_FMT.format(total);
        return ServiceResult.ok(msg, hdCode);
    }

    // ================================================================
    //  THANH TOÁN – Orchestrated (dùng CheckoutRequest DTO)
    // ================================================================

    /**
     * Checkout orchestration hoàn chỉnh:
     * 1. Validate request qua BanHangValidator
     * 2. Tính tiền thừa
     * 3. Trả kết quả có cấu trúc
     *
     * Panel chỉ cần gọi: banHangService.processCheckout(request)
     */
    public ServiceResult<String> processCheckout(CheckoutRequest req) {
        // 1. Validate request
        var errors = BanHangValidator.validateCheckout(req);
        if (!errors.isEmpty()) {
            return ServiceResult.fail(String.join("\n", errors));
        }

        // 2. Tính tiền thừa nếu tiền mặt
        BigDecimal tienThua = BigDecimal.ZERO;
        if ("Tiền mặt".equals(req.getPhuongThucThanhToan()) && req.getTienKhachDua() != null) {
            tienThua = req.getTienKhachDua().subtract(req.getTongTien()).max(BigDecimal.ZERO);
        }

        // 3. TODO: Cập nhật trạng thái hóa đơn DB, ghi log, tích điểm KH

        // 4. Trả kết quả
        String hdCode = "HD" + String.format("%04d", req.getMaHoaDon());
        StringBuilder msg = new StringBuilder();
        msg.append("Thanh toán thành công!\n");
        msg.append("HĐ: ").append(hdCode).append("\n");
        msg.append("Tổng: ").append(VND_FMT.format(req.getTongTien())).append("\n");
        msg.append("PTTT: ").append(req.getPhuongThucThanhToan());
        if (tienThua.compareTo(BigDecimal.ZERO) > 0) {
            msg.append("\nTiền thừa: ").append(VND_FMT.format(tienThua));
        }
        LOG.info("[CHECKOUT] Thành công: " + hdCode + " | Tổng=" + VND_FMT.format(req.getTongTien()) + " | PTTT=" + req.getPhuongThucThanhToan());
        AuditService.logCheckout(req.getMaHoaDon(), hdCode + " " + VND_FMT.format(req.getTongTien()), "user_" + req.getMaNguoiDung());
        return ServiceResult.ok(msg.toString(), hdCode);
    }
}
