package common.exception;

/**
 * DataAccessException – Lỗi tầng data access (SQL, connection, v.v.).
 */
public class DataAccessException extends AppException {
    public DataAccessException(String message) { super(message); }
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
