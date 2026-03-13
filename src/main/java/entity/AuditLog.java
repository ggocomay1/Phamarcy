package entity;

import java.time.LocalDateTime;

/**
 * AuditLog – Ghi nhận hành động quan trọng trong hệ thống.
 */
public class AuditLog {
    private int maAuditLog;
    private String action;       // LOGIN, LOGOUT, CHECKOUT, CANCEL_INVOICE, IMPORT_GOODS, STOCK_UPDATE, USER_ACTION
    private String entity;       // HoaDonBan, PhieuNhap, SanPham, NguoiDung
    private Integer entityId;    // Mã đối tượng (nullable)
    private String detail;       // Mô tả chi tiết
    private String username;     // Người thực hiện
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(String action, String entity, Integer entityId, String detail, String username) {
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.detail = detail;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }

    // Getters/Setters
    public int getMaAuditLog() { return maAuditLog; }
    public void setMaAuditLog(int v) { this.maAuditLog = v; }
    public String getAction() { return action; }
    public void setAction(String v) { this.action = v; }
    public String getEntity() { return entity; }
    public void setEntity(String v) { this.entity = v; }
    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer v) { this.entityId = v; }
    public String getDetail() { return detail; }
    public void setDetail(String v) { this.detail = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
