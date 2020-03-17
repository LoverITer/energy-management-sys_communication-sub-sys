package cn.edu.xust.communication.exception;

/**
 * 未发现目标设备异常
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/20 16:02
 */

public class NotFoundDeviceException extends RuntimeException {

    public NotFoundDeviceException() {
        super("未发现目标设备");
    }

    public NotFoundDeviceException(String message, Throwable cause) {
        super(message, cause);
    }


}
