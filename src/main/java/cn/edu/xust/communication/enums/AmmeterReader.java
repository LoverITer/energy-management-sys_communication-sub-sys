package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 读数据控制码
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 10:46
 */
public enum AmmeterReader {

    /**主站请求读电能表数据<br/>
     * 数据域长度：L=04H+m(数据长度)
     * <pre>
     * 帧格式1（m=0）：
     * 68H	A0	…	A5	68H	11H	04H	      DI0	…	DI3	CS	16H
     *        ╲   ╱          ╲   ╲         ╲       ╱
     *        地址域         控制码 数据域长度 数据标识
     * 帧格式2（m=1,读给定块数的负荷记录）：
     * 68H	A0	…	A5	68H	11H	05H	DI0	…	DI3	N CS	16H
     *                                          ╲
     *                                    负荷记录块数
     * 帧格式3（m=6，读给定时间、块数的负荷记录）：
     * 68H	A0	…	A5	68H	11H	0AH	DI0	…	DI3	N	mm	hh	DD	MM	YY	CS	16H
     *                                                     分   时  日   月   年
     * 帧格式4（液晶查看命令）：
     * 68H	A0	…	A5	68H	11H	L	DI0	…	DI3	N1	…	Nm	CS	16H
     * </pre>
     */
    MasterRequestFrame("11", "04"),

    /**
     * 从站响应数据
     * 数据域长度：L=04H+m(数据长度)
     * 无后续数据帧格式：
     * 68H	A0	…	A5	68H	91H	L  DI0	…	DI3	N1	…	Nm	CS	16H
     *                                           ╲    ╱
     *                                             数据
     */
    SalveResponseFrame("91", "04"),

    /**
     * 从站响应数据
     * 数据域长度：L=04H+m(数据长度)
     * 有后续数据帧格式：
     * 68H	A0	…	A5	68H	B1H	L	DI0	…	DI3	N1	…	Nm	CS	16H
     *                                           ╲    ╱
     *                                             数据
     */
    SalveResponseFrameWithMore("B1", "04"),

    /**
     * 从站异常应答
     * 数据长度：L=01H
     */
    SlaveExceptionResponseFrame("D1", "01");


    private final String controlCode;
    
    private final String baseDataLen;

    private AmmeterReader(String controlCode, String baseDataLen) {
        this.controlCode = controlCode;
        this.baseDataLen = baseDataLen;
    }

    public String getControlCode() {
        return controlCode;
    }

    public String getBaseDataLen() {
        return baseDataLen;
    }
}
