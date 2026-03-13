package service;

import java.util.List;
import java.util.logging.Logger;

import common.AppLogger;
import dao.AuditLogDao;
import entity.AuditLog;

/**
 * AuditService – Facade cho audit log.
 *
 * Cách dùng:
 *   AuditService.log("LOGIN", "NguoiDung", null, "Login thành công", "admin");
 *   AuditService.log("CHECKOUT", "HoaDonBan", 12, "HD0012 thanh toán 150k", "nv01");
 *
 * @version 1.0
 */
public class AuditService {

    private static final Logger LOG = AppLogger.get(AuditService.class);
    private static final AuditLogDao dao = new AuditLogDao();

    // ── Static convenience methods ──

    public static void log(String action, String entity, Integer entityId, String detail, String username) {
        try {
            var entry = new AuditLog(action, entity, entityId, detail, username);
            dao.insert(entry);
            LOG.fine("[AUDIT] " + action + " | " + entity + " | " + detail);
        } catch (Exception e) {
            // Audit should never break business flow
            LOG.warning("[AUDIT] Failed to log: " + action + " - " + e.getMessage());
        }
    }

    /** Shortcut: login */
    public static void logLogin(String username, boolean success) {
        log("LOGIN", "NguoiDung", null,
            success ? "Đăng nhập thành công" : "Đăng nhập thất bại", username);
    }

    /** Shortcut: logout */
    public static void logLogout(String username) {
        log("LOGOUT", "NguoiDung", null, "Đăng xuất", username);
    }

    /** Shortcut: checkout */
    public static void logCheckout(int maHoaDon, String detail, String username) {
        log("CHECKOUT", "HoaDonBan", maHoaDon, detail, username);
    }

    /** Shortcut: cancel invoice */
    public static void logCancelInvoice(int maHoaDon, String username) {
        log("CANCEL_INVOICE", "HoaDonBan", maHoaDon, "Hủy hóa đơn HD" + String.format("%04d", maHoaDon), username);
    }

    /** Shortcut: import goods */
    public static void logImportGoods(int maPhieuNhap, String detail, String username) {
        log("IMPORT_GOODS", "PhieuNhap", maPhieuNhap, detail, username);
    }

    /** Shortcut: stock update */
    public static void logStockUpdate(int maSanPham, String detail, String username) {
        log("STOCK_UPDATE", "SanPham", maSanPham, detail, username);
    }

    // ── Query methods ──

    public static List<AuditLog> getRecent(int limit) {
        return dao.getRecent(limit);
    }

    public static List<AuditLog> search(String action, String fromDate, String toDate) {
        return dao.search(action, fromDate, toDate);
    }
}
