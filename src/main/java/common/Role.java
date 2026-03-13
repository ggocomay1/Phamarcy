package common;

/**
 * Role - Vai trò người dùng trong hệ thống MEPHAR.
 * Enum chính thức được dùng bởi PermissionManager.
 *
 * @version 2.0
 */
public enum Role {
    ADMIN("Admin", new String[]{"Admin", "Administrator", "Quản trị", "Quan Tri", "1"}),
    QUANLY("Quản lý", new String[]{"Quản lý", "QuanLy", "Quan ly", "Manager", "2"}),
    NHANVIEN("Nhân viên", new String[]{"Nhân viên", "NhanVien", "Nhan vien", "Staff", "Employee", "3"});

    private final String label;
    private final String[] aliases;

    Role(String label, String[] aliases) { 
        this.label = label; 
        this.aliases = aliases;
    }

    public String getLabel() { return label; }
    
    /**
     * Lấy Role từ chuỗi theo nhiều ngôn ngữ và định dạng khác nhau.
     */
    public static Role fromString(String text) {
        if (text == null || text.trim().isEmpty()) return NHANVIEN;
        
        String normalized = text.trim();
        
        for (Role role : Role.values()) {
            for (String alias : role.aliases) {
                if (normalized.equalsIgnoreCase(alias)) {
                    return role;
                }
            }
        }
        
        return NHANVIEN;
    }
}
