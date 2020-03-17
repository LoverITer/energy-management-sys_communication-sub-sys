package cn.edu.xust.communication.exception;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/16 09:16
 */
public class CustomException extends RuntimeException {

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}
