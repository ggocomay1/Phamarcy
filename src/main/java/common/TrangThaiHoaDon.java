package common;

/**
 * TrangThaiHoaDon – Enum trạng thái hóa đơn.
 * Thay thế magic strings "Hoàn thành", "Đã hủy".
 */
public enum TrangThaiHoaDon {
    HOAN_THANH("Hoàn thành"),
    DA_HUY("Đã hủy"),
    DANG_XU_LY("Đang xử lý");

    private final String label;
    TrangThaiHoaDon(String label) { this.label = label; }
    public String getLabel() { return label; }

    public static TrangThaiHoaDon fromLabel(String label) {
        for (var v : values()) if (v.label.equalsIgnoreCase(label)) return v;
        return DANG_XU_LY;
    }
}
