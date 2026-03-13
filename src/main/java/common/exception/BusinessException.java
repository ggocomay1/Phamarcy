package common.exception;

/**
 * BusinessException – Lỗi nghiệp vụ (không đủ tồn kho, hóa đơn không hợp lệ, v.v.).
 */
public class BusinessException extends AppException {
    public BusinessException(String message) { super(message); }
    public BusinessException(String message, Throwable cause) { super(message, cause); }
}
