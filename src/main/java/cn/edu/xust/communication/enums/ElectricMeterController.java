package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 拉合闸、报警、保电控制码
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 11:11
 */
public enum ElectricMeterController {

    /**
     * 清空电能表内电能量、最大需量及发生时间、冻结量、事件记录、负荷记录等数据
     * 主站请求读电能表数据
     * 数据域长度：L=08H+m(数据长度)
     * 帧格式：
     * 68H	A0	…	A5	68H	1CH	L	PA	P0	P1	P2	C0	…	C3	N1	…	Nm	CS	16H
     * 注1： 本命令无须硬件配合。
     * 注2：不带安全认证密级为03H, N1～Nm为明文。带安全认证密级为96H，N1～Nm为密文。
     * 注3： 96H级密码权限应校验密文，不验证密码，其它密码权限应验证密码。
     * 注4： 96H级密码权限下，P0P1P2保留，默认为000000H。
     * 注5： 96H级密码权限下，N1～Nm为密文；其它密码权限下，N1～Nm为明文数据。
     * 注6： N1～Nm为密文时，N1～Nm解密后的明文数据为R1～R8，其中R1为控制命令类型，R1=1AH代表拉闸，R1=1BH代表合闸允许，R1=1CH代表直接合闸，R1=2AH代表报警，R1=2BH代表报警解除，R1=3AH代表保电，R1=3BH代表保电解除；R2保留，默认为00H；R3～R8代表命令有效截止时间，数据格式为YYMMDDhhmmss。
     * 注7： N1～Nm为明文数据时，N1为控制命令类型，N1=1AH代表拉闸，N1=1BH代表合闸允许，N1=1CH代表直接合闸，N1=2AH代表报警，N1=2BH代表报警解除，N1=3AH代表保电，N1=3BH代表保电解除；N2保留，默认为00H；N3～N8代表命令有效截止时间，数据格式为ssmmhhDDMMYY。
     */
    MasterRequestFrame("1CH"),

    /**
     * 从站响应数据
     * 数据域长度：L=00H
     * 帧格式：
     * 68H	A0	…	A5	68H	9CH	00	CS	16 H
     */
    SalveResponseFrame("9CH"),


    /**
     * 从站异常应答
     * 数据长度：L=01H
     * 帧格式：
     * 68H	A0	…	A5	68H	DCH	01	ERR	CS	16 H
     */
    SlaveExceptionResponseFrame("DCH");

    private String controlCode;

    ElectricMeterController(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }
}
