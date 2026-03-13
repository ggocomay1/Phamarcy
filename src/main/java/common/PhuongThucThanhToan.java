package common;

/**
 * PhuongThucThanhToan – Enum phương thức thanh toán.
 * Thay thế magic strings "Tiền mặt", "Chuyển khoản".
 */
public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản");

    private final String label;
    PhuongThucThanhToan(String label) { this.label = label; }
    public String getLabel() { return label; }

    public static PhuongThucThanhToan fromLabel(String label) {
        for (var v : values()) if (v.label.equalsIgnoreCase(label)) return v;
        return TIEN_MAT;
    }
}
