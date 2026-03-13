package common.exception;

/**
 * ValidationException – Lỗi validation nghiệp vụ (dữ liệu đầu vào không hợp lệ).
 */
public class ValidationException extends AppException {
    public ValidationException(String message) { super(message); }
}
