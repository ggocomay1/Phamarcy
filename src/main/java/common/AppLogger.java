package common;

import java.io.File;
import java.util.logging.*;

/**
 * AppLogger – Logging nhất quán cho toàn bộ ứng dụng MEPHAR.
 * Dùng java.util.logging để giữ gọn dependency.
 *
 * Cách dùng:
 *   private static final Logger log = AppLogger.get(MyClass.class);
 *   log.info("User admin created invoice HD0012");
 *   log.warning("Low stock for SP001");
 *   log.severe("DB connection failed");
 */
public final class AppLogger {

    private static boolean initialized = false;

    private AppLogger() {}

    /** Lấy Logger cho class cụ thể */
    public static Logger get(Class<?> clazz) {
        if (!initialized) init();
        return Logger.getLogger(clazz.getName());
    }

    /** Lấy Logger theo tên */
    public static Logger get(String name) {
        if (!initialized) init();
        return Logger.getLogger(name);
    }

    private static synchronized void init() {
        if (initialized) return;
        try {
            Logger root = Logger.getLogger("");

            // Set level from config
            String levelStr = AppConfig.get("log.level", "INFO");
            Level level = Level.parse(levelStr.toUpperCase());
            root.setLevel(level);

            // Console handler
            for (Handler h : root.getHandlers()) {
                if (h instanceof ConsoleHandler) {
                    h.setLevel(level);
                    h.setFormatter(new CompactFormatter());
                }
            }

            // File handler
            String logFile = AppConfig.get("log.file", "logs/mephar.log");
            File logDir = new File(logFile).getParentFile();
            if (logDir != null && !logDir.exists()) logDir.mkdirs();

            FileHandler fh = new FileHandler(logFile, 5_000_000, 3, true); // 5MB, 3 files
            fh.setLevel(level);
            fh.setFormatter(new CompactFormatter());
            fh.setEncoding("UTF-8");
            root.addHandler(fh);

            initialized = true;
        } catch (Exception e) {
            System.err.println("AppLogger init failed: " + e.getMessage());
        }
    }

    /** Compact formatter: [TIME] LEVEL CLASS - MESSAGE */
    private static class CompactFormatter extends Formatter {
        private static final java.time.format.DateTimeFormatter TF =
            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        @Override
        public String format(LogRecord r) {
            String time = java.time.LocalTime.now().format(TF);
            String cls = r.getSourceClassName();
            if (cls != null && cls.contains("."))
                cls = cls.substring(cls.lastIndexOf('.') + 1);
            String msg = formatMessage(r);
            var sb = new StringBuilder();
            sb.append('[').append(time).append("] ")
              .append(r.getLevel().getName()).append(' ')
              .append(cls != null ? cls : "").append(" - ")
              .append(msg).append(System.lineSeparator());
            if (r.getThrown() != null) {
                var sw = new java.io.StringWriter();
                r.getThrown().printStackTrace(new java.io.PrintWriter(sw));
                sb.append(sw);
            }
            return sb.toString();
        }
    }
}
