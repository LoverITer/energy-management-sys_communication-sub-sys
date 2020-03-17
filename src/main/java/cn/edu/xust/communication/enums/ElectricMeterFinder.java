package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 寻卡命令控制码
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 11:23
 */
public enum ElectricMeterFinder {

    /**
     * 电能表寻卡相关操作
     * 数据域长度：L=04H+m(数据长度)
     * 数据域：C0C1C2C3+DATA
     * 帧格式：
     * 68H	A0	…	A5	68H	09H	L	C0	…	C3	N1	…	Nm	CS	16H
     */
    MasterRequestFrame("09H"),

    /**
     * 从站响应数据
     * 数据域长度：L=00H
     * 帧格式：
     * 68H	A0	…	A5	68H	9CH	00	CS	16 H
     */
    SalveResponseFrame("89H"),


    /**
     * 从站异常应答
     * 数据长度：L=01H
     * 帧格式：
     * 68H	A0	…	A5	68H	DCH	01	ERR	CS	16 H
     */
    SlaveExceptionResponseFrame("C9H");

    private String controlCode;

    ElectricMeterFinder(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }
}
