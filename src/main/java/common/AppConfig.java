package common;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * AppConfig – Đọc cấu hình từ application.properties.
 * Singleton, load 1 lần khi class được khởi tạo.
 *
 * @version 1.0
 */
public final class AppConfig {

    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());
    private static final Properties props = new Properties();
    private static boolean loaded = false;

    static {
        load();
    }

    private AppConfig() {}

    private static void load() {
        try (InputStream is = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is == null) {
                LOG.warning("application.properties không tìm thấy trên classpath! Dùng giá trị mặc định.");
                return;
            }
            props.load(is);
            loaded = true;
            LOG.info("Loaded application.properties (" + props.size() + " keys)");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Không thể đọc application.properties", e);
        }
    }

    // ── Getters ──

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String get(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }

    public static int getInt(String key, int defaultVal) {
        try {
            String v = props.getProperty(key);
            return v != null ? Integer.parseInt(v.trim()) : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static boolean getBool(String key, boolean defaultVal) {
        String v = props.getProperty(key);
        return v != null ? Boolean.parseBoolean(v.trim()) : defaultVal;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    // ── Convenience shortcuts ──

    public static String dbUrl()       { return get("db.url", ""); }
    public static String dbUser()      { return get("db.user", "sa"); }
    public static String dbPassword()  { return get("db.password", ""); }
    public static String dbDriver()    { return get("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver"); }

    public static String[] dbServerNames() {
        String val = get("db.serverNames", "localhost");
        return val.split(",");
    }

    public static int poolMinIdle()         { return getInt("db.pool.minimumIdle", 2); }
    public static int poolMaxSize()         { return getInt("db.pool.maximumPoolSize", 10); }
    public static int poolConnTimeout()     { return getInt("db.pool.connectionTimeout", 5000); }

    public static String appEnv()    { return get("app.env", "dev"); }
    public static String appName()   { return get("app.name", "MEPHAR"); }
    public static String logLevel()  { return get("log.level", "INFO"); }
}
