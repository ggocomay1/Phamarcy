package common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * ConnectDB – HikariCP connection pool, đọc cấu hình từ application.properties.
 * Không còn hard-code server/user/password.
 *
 * @version 3.0
 */
public class ConnectDB {

    private static final Logger LOG = AppLogger.get(ConnectDB.class);
    private static HikariDataSource dataSource;

    static {
        initializeConnectionPool();
    }

    /**
     * Khởi tạo HikariCP Connection Pool – thử từng serverName trong config.
     */
    private static void initializeConnectionPool() {
        String[] serverNames = AppConfig.dbServerNames();
        String dbUser = AppConfig.dbUser();
        String dbPassword = AppConfig.dbPassword();
        int port = AppConfig.getInt("db.port", 1433);
        String dbName = AppConfig.get("db.name", "CuaHangThuoc_Batch");

        // Nếu có db.url đầy đủ, thử dùng trực tiếp
        String directUrl = AppConfig.dbUrl();
        if (directUrl != null && !directUrl.isBlank()) {
            if (tryConnect(directUrl, dbUser, dbPassword)) return;
        }

        // Fallback: thử từng serverName
        for (String serverName : serverNames) {
            String sn = serverName.trim();
            if (sn.isEmpty()) continue;
            String jdbcUrl = String.format(
                "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;trustServerCertificate=true;" +
                "characterEncoding=UTF-8;sendStringParametersAsUnicode=true;useUnicode=true;",
                sn, port, dbName
            );
            if (tryConnect(jdbcUrl, dbUser, dbPassword)) return;
        }

        LOG.severe("KHÔNG THỂ KẾT NỐI TỚI BẤT KỲ SQL SERVER NÀO!");
        LOG.severe("Kiểm tra application.properties: db.url, db.serverNames, db.user, db.password");
    }

    private static boolean tryConnect(String jdbcUrl, String user, String password) {
        try {
            var config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName(AppConfig.dbDriver());
            config.setMinimumIdle(AppConfig.poolMinIdle());
            config.setMaximumPoolSize(AppConfig.poolMaxSize());
            config.setConnectionTimeout(AppConfig.poolConnTimeout());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            var ds = new HikariDataSource(config);
            // Test connection
            try (var testCon = ds.getConnection()) {
                LOG.info("✓ Kết nối DB thành công: " + jdbcUrl.substring(0, Math.min(60, jdbcUrl.length())) + "...");
                if (dataSource != null && !dataSource.isClosed()) dataSource.close();
                dataSource = ds;
                return true;
            }
        } catch (Exception e) {
            LOG.warning("✗ Không thể kết nối: " + jdbcUrl.substring(0, Math.min(50, jdbcUrl.length())) + " - " + e.getMessage());
            return false;
        }
    }

    /** Lấy connection từ pool */
    public static Connection getCon() {
        try {
            if (dataSource == null || dataSource.isClosed()) return null;
            return dataSource.getConnection();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Lỗi lấy connection từ pool", e);
            return null;
        }
    }

    /** Kiểm tra pool active */
    public static boolean isConnectionPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }

    /** Thông tin pool */
    public static String getPoolInfo() {
        if (dataSource == null || dataSource.isClosed()) return "Pool chưa khởi tạo";
        return String.format("Pool [Active: %d, Idle: %d, Total: %d]",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections());
    }

    /** Đóng pool */
    public static void closeConnectionPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOG.info("Connection pool closed");
        }
    }
}