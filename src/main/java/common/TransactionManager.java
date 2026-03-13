package common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import common.exception.DataAccessException;

/**
 * TransactionManager – Quản lý transaction cho JDBC.
 * Service dùng class này để bao nghiệp vụ nhiều bước trong 1 transaction.
 *
 * Cách dùng:
 *   TransactionManager.execute(conn -> {
 *       dao1.insertWithConn(conn, ...);
 *       dao2.updateWithConn(conn, ...);
 *       return result;
 *   });
 */
public final class TransactionManager {

    private static final Logger LOG = AppLogger.get(TransactionManager.class);

    private TransactionManager() {}

    /**
     * Thực thi logic trong 1 transaction.
     * Auto commit=false, commit khi done, rollback khi exception.
     *
     * @param action logic cần thực thi (nhận Connection, trả kết quả)
     * @return kết quả từ action
     * @throws DataAccessException nếu lỗi DB
     */
    public static <T> T execute(TransactionAction<T> action) {
        Connection conn = ConnectDB.getCon();
        if (conn == null) throw new DataAccessException("Không thể lấy connection từ pool!");

        try {
            conn.setAutoCommit(false);
            T result = action.run(conn);
            conn.commit();
            LOG.fine("Transaction committed successfully");
            return result;

        } catch (DataAccessException e) {
            rollback(conn);
            throw e;
        } catch (Exception e) {
            rollback(conn);
            throw new DataAccessException("Transaction failed: " + e.getMessage(), e);
        } finally {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                LOG.warning("Error closing connection after transaction: " + ex.getMessage());
            }
        }
    }

    /** Variant trả void */
    public static void executeVoid(TransactionVoidAction action) {
        execute(conn -> { action.run(conn); return null; });
    }

    private static void rollback(Connection conn) {
        try {
            conn.rollback();
            LOG.warning("Transaction ROLLED BACK");
        } catch (SQLException ex) {
            LOG.severe("Rollback failed: " + ex.getMessage());
        }
    }

    // ── Functional interfaces ──

    @FunctionalInterface
    public interface TransactionAction<T> {
        T run(Connection conn) throws Exception;
    }

    @FunctionalInterface
    public interface TransactionVoidAction {
        void run(Connection conn) throws Exception;
    }
}
