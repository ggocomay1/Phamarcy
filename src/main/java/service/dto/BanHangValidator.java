package service.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * BanHangValidator – Validation nghiệp vụ cho module Bán hàng.
 * Tách validation ra khỏi UI panel.
 */
public class BanHangValidator {

    private static final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    /** Validate trước khi checkout */
    public static List<String> validateCheckout(CheckoutRequest req) {
        var errors = new java.util.ArrayList<String>();

        if (req.getMaHoaDon() <= 0)
            errors.add("Hóa đơn chưa được tạo.");

        if (req.getTongTien() == null || req.getTongTien().compareTo(BigDecimal.ZERO) <= 0)
            errors.add("Tổng tiền phải lớn hơn 0.");

        String pt = req.getPhuongThucThanhToan();
        if (pt == null || pt.isBlank())
            errors.add("Chưa chọn phương thức thanh toán.");

        if ("Tiền mặt".equals(pt)) {
            if (req.getTienKhachDua() == null || req.getTienKhachDua().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add("Vui lòng nhập tiền khách đưa.");
            } else if (req.getTienKhachDua().compareTo(req.getTongTien()) < 0) {
                errors.add("Tiền chưa đủ! Cần: " + VND.format(req.getTongTien()));
            }
        }

        if ("Chuyển khoản".equals(pt) && !req.isXacNhanChuyenKhoan()) {
            errors.add("Vui lòng xác nhận đã nhận chuyển khoản.");
        }

        return errors;
    }

    /** Validate giỏ hàng không rỗng */
    public static String validateCartNotEmpty(Integer maHoaDon, int rowCount) {
        if (maHoaDon == null || rowCount == 0)
            return "Chưa có sản phẩm trong giỏ!";
        return null;
    }

    /** Validate số lượng dòng giỏ */
    public static String validateQuantity(int soLuong) {
        if (soLuong <= 0) return "Số lượng phải lớn hơn 0!";
        return null;
    }
}
