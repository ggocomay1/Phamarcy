package common.exception;

/**
 * AppException – Base exception cho toàn bộ ứng dụng MEPHAR.
 */
public class AppException extends RuntimeException {
    public AppException(String message) { super(message); }
    public AppException(String message, Throwable cause) { super(message, cause); }
}
