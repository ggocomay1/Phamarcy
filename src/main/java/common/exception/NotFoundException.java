package common.exception;

/**
 * NotFoundException – Không tìm thấy entity/resource.
 */
public class NotFoundException extends AppException {
    public NotFoundException(String message) { super(message); }
}
