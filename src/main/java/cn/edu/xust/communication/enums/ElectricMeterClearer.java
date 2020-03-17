package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 电表清零控制码
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 10:58
 */
public enum ElectricMeterClearer {

    /**
     * 清空电能表内电能量、最大需量及发生时间、冻结量、事件记录、负荷记录等数据
     * 主站请求读电能表数据
     * 数据域长度：L=08H+m(数据长度)
     */
    MasterRequestFrame("1AH"),

    /**
     * 从站响应数据
     * 数据域长度：L=00H
     */
    SalveResponseFrame("9AH"),


    /**
     * 从站异常应答
     * 数据长度：L=01H
     */
    SlaveExceptionResponseFrame("DAH");

    private final String controlCode;

    ElectricMeterClearer(String controlCode) {
        this.controlCode = controlCode;
    }


    public String getControlCode() {
        return controlCode;
    }

}
