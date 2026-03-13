package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import common.AppLogger;
import common.ConnectDB;
import entity.AuditLog;

/**
 * AuditLogDao – Persistence cho AuditLog.
 * Tự tạo bảng nếu chưa tồn tại.
 *
 * @version 1.0
 */
public class AuditLogDao {

    private static final Logger LOG = AppLogger.get(AuditLogDao.class);
    private static boolean tableChecked = false;

    private void ensureTable() {
        if (tableChecked) return;
        try (var con = ConnectDB.getCon(); var stmt = con.createStatement()) {
            stmt.executeUpdate(
                "IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'AuditLog') " +
                "CREATE TABLE AuditLog (" +
                "MaAuditLog INT IDENTITY(1,1) PRIMARY KEY, " +
                "Action NVARCHAR(50) NOT NULL, " +
                "Entity NVARCHAR(50), " +
                "EntityId INT, " +
                "Detail NVARCHAR(500), " +
                "Username NVARCHAR(100), " +
                "CreatedAt DATETIME2 DEFAULT GETDATE())"
            );
            tableChecked = true;
            LOG.fine("AuditLog table ensured");
        } catch (Exception e) {
            LOG.warning("AuditLog table check failed: " + e.getMessage());
        }
    }

    /**
     * Ghi 1 dòng audit log.
     */
    public boolean insert(AuditLog log) {
        ensureTable();
        String sql = "INSERT INTO AuditLog (Action, Entity, EntityId, Detail, Username) VALUES (?, ?, ?, ?, ?)";
        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql)) {
            ps.setNString(1, log.getAction());
            ps.setNString(2, log.getEntity());
            if (log.getEntityId() != null) ps.setInt(3, log.getEntityId());
            else ps.setNull(3, java.sql.Types.INTEGER);
            ps.setNString(4, log.getDetail());
            ps.setNString(5, log.getUsername());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LOG.warning("AuditLog insert failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy danh sách audit log gần đây (top N).
     */
    public List<AuditLog> getRecent(int limit) {
        ensureTable();
        var list = new ArrayList<AuditLog>();
        String sql = "SELECT TOP (?) * FROM AuditLog ORDER BY CreatedAt DESC";
        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            var rs = ps.executeQuery();
            while (rs.next()) {
                var a = new AuditLog();
                a.setMaAuditLog(rs.getInt("MaAuditLog"));
                a.setAction(rs.getString("Action"));
                a.setEntity(rs.getString("Entity"));
                var eid = rs.getObject("EntityId");
                if (eid != null) a.setEntityId((Integer) eid);
                a.setDetail(rs.getString("Detail"));
                a.setUsername(rs.getString("Username"));
                Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
                list.add(a);
            }
        } catch (Exception e) {
            LOG.warning("AuditLog query failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lọc audit log theo action và khoảng thời gian.
     */
    public List<AuditLog> search(String action, String fromDate, String toDate) {
        ensureTable();
        var list = new ArrayList<AuditLog>();
        var sql = new StringBuilder("SELECT * FROM AuditLog WHERE 1=1 ");
        var params = new ArrayList<Object>();

        if (action != null && !action.isEmpty()) { sql.append("AND Action = ? "); params.add(action); }
        if (fromDate != null) { sql.append("AND CreatedAt >= ? "); params.add(fromDate); }
        if (toDate != null) { sql.append("AND CreatedAt < DATEADD(DAY,1,CAST(? AS DATE)) "); params.add(toDate); }
        sql.append("ORDER BY CreatedAt DESC");

        try (var con = ConnectDB.getCon(); var ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            var rs = ps.executeQuery();
            while (rs.next()) {
                var a = new AuditLog();
                a.setMaAuditLog(rs.getInt("MaAuditLog"));
                a.setAction(rs.getString("Action"));
                a.setEntity(rs.getString("Entity"));
                var eid = rs.getObject("EntityId");
                if (eid != null) a.setEntityId((Integer) eid);
                a.setDetail(rs.getString("Detail"));
                a.setUsername(rs.getString("Username"));
                Timestamp ts = rs.getTimestamp("CreatedAt");
                if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
                list.add(a);
            }
        } catch (Exception e) {
            LOG.warning("AuditLog search failed: " + e.getMessage());
        }
        return list;
    }
}
