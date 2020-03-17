package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 通信心跳帧控制码
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 11:18
 */
public enum ElectricMeterHeartBeat {

    /**
     * 辅助电能表检测通信信道
     * 控制码：C=1EH
     * 数据域长度：L =01H
     * 数据域：TO （下一帧超时时间）
     * 帧格式：
     * 68H	99	…	99	68H	1EH	L	TO	CS	16H
     * 注1: 数据域TO表示下一帧心跳帧延时时间，范围6～90min，单位：分钟，默认30分钟。
     * 注2：心跳帧广播发送。
     */
    MasterRequestFrame("1EH");

    private String controlCode;

    ElectricMeterHeartBeat(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }
}
