package service;

/**
 * ServiceResult – Generic result wrapper cho service layer.
 * Panel nhận ServiceResult để hiển thị kết quả mà không cần suy luận từ boolean rời rạc.
 *
 * @param <T> kiểu dữ liệu trả về
 * @version 1.0
 */
public class ServiceResult<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private ServiceResult(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ServiceResult<T> ok(T data) {
        return new ServiceResult<>(true, null, data);
    }

    public static <T> ServiceResult<T> ok(String message, T data) {
        return new ServiceResult<>(true, message, data);
    }

    public static <T> ServiceResult<T> ok(String message) {
        return new ServiceResult<>(true, message, null);
    }

    public static <T> ServiceResult<T> fail(String message) {
        return new ServiceResult<>(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public boolean isFail() { return !success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
