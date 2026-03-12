package common;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Class quản lý kết nối database với HikariCP Connection Pooling Đã cập nhật
 * đầy đủ các hàm để tương thích với ConnectDBTest * @author Vương Ngọc Gia Bảo
 *
 * @version 2.1
 */
public class ConnectDB {
	// Thông tin kết nối - Ưu tiên localhost, fallback sang server name cụ thể
	// *** THAY ĐỔI SERVER_NAME và thông tin đăng nhập cho phù hợp với máy của bạn
	// ***
	private static final String[] SERVER_NAMES = {
			"localhost", // Thử localhost trước (default instance)
			"localhost\\MSSQLSERVER02", // Named instance phổ biến
			"GIABAO123\\MSSQLSERVER02" // Server gốc của developer
	};
	private static final String PORT = "1433";
	private static final String DATABASE_NAME = "CuaHangThuoc_Batch";
	private static final String USER = "sa";
	private static final String PASSWORD = "123456";
	private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=CuaHangThuoc_Batch;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8;sendStringParametersAsUnicode=true;useUnicode=true;";

	// HikariCP DataSource (Connection Pool)
	private static HikariDataSource dataSource;

	// Khởi tạo connection pool ngay khi class được load
	static {
		initializeConnectionPool();
	}

	/**
	 * Khởi tạo HikariCP Connection Pool - thử nhiều server name
	 */
	private static void initializeConnectionPool() {
		for (String serverName : SERVER_NAMES) {
			try {
				var config = new HikariConfig();

				// Format JDBC URL chuẩn cho SQL Server
				var jdbcUrl = String.format(
						"jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8;sendStringParametersAsUnicode=true;useUnicode=true;",
						serverName, PORT, DATABASE_NAME);

				config.setJdbcUrl(jdbcUrl);
				config.setUsername(USER);
				config.setPassword(PASSWORD);

				// Cấu hình tối ưu hiệu suất
				config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				config.setMinimumIdle(2);
				config.setMaximumPoolSize(10);
				config.setConnectionTimeout(5000); // 5 giây timeout cho mỗi lần thử

				// Các thuộc tính bổ sung để tăng tốc độ query
				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

				dataSource = new HikariDataSource(config);

				// Test connection
				try (var testCon = dataSource.getConnection()) {
					System.out.println("✓ Kết nối thành công tới: " + serverName + ":" + PORT + "/" + DATABASE_NAME);
					return; // Thành công, thoát vòng lặp
				}

			} catch (Exception e) {
				System.err.println("✗ Không thể kết nối tới: " + serverName + " - " + e.getMessage());
				if (dataSource != null && !dataSource.isClosed()) {
					dataSource.close();
					dataSource = null;
				}
			}
		}
		System.err.println("✗ KHÔNG THỂ KẾT NỐI TỚI BẤT KỲ SQL SERVER NÀO!");
		System.err.println("  → Kiểm tra SQL Server đã chạy chưa");
		System.err.println("  → Kiểm tra database '" + DATABASE_NAME + "' đã tồn tại chưa");
		System.err.println("  → Kiểm tra user/password: " + USER + "/" + PASSWORD);
	}

	/**
	 * Lấy connection từ pool (Dùng cho ConnectDBTest - Test 2, 3, 4)
	 */
	public static Connection getCon() {
		try {
			if (dataSource == null || dataSource.isClosed()) {
				return null;
			}
			return dataSource.getConnection();
		} catch (SQLException e) {
			return null;
		}
	}

	/**
	 * Kiểm tra trạng thái pool (Dùng cho ConnectDBTest - Test 1)
	 */
	public static boolean isConnectionPoolActive() {
		return dataSource != null && !dataSource.isClosed();
	}

	/**
	 * Lấy thông tin chi tiết của pool (Dùng cho ConnectDBTest - Test 5)
	 */
	public static String getPoolInfo() {
		if (dataSource == null || dataSource.isClosed()) {
			return "Connection Pool chưa được khởi tạo";
		}

		// Trả về thông tin trạng thái các kết nối trong Pool
		return String.format("Pool Status [Active: %d, Idle: %d, Total: %d]",
				dataSource.getHikariPoolMXBean().getActiveConnections(),
				dataSource.getHikariPoolMXBean().getIdleConnections(),
				dataSource.getHikariPoolMXBean().getTotalConnections());
	}

	/**
	 * Đóng pool khi shutdown ứng dụng
	 */
	public static void closeConnectionPool() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}
}