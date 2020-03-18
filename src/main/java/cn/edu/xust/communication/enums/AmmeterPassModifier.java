package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 修改密码控制码
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 10:53
 */
public enum AmmeterPassModifier {
    /**
     * 主站请求修改从站密码设置
     * 数据域长度：L=0CH
     * 数据域：DIODI1DI2DI3＋PAOP0OP1OP2O＋PANP0NP1NP2N
     * 注1：P00P10P20 为原密码或更高权限的密码，PA0 表示该密码权限。P0NP1NP2N 为新密码或需设置的密码，PAN为新密码的权限。PA0、PAN 的取值范围为 00～09，00 为最高权限，数值越大权限越低。权限级别分为：03 级电量清零、事件清零；04 级写数据、最大需量清零。
     * 注2：该命令必须与编程键配合使用；如无编程键，不支持该命令。
     * 注3：修改密码时，需PAN≥PA0。
     */
    MasterRequestFrame("18"),

    /**
     * 从站响应数据
     * 数据域长度：L=04H
     * 数据域：PANP0NP1NP2N（新编入的密码权限及密码）
     */
    SalveResponseFrame("98"),

    /**
     * 从站异常应答
     * 数据长度：L=01H
     */
    SlaveExceptionResponseFrame("D8");


    private final String ControlCode;

    AmmeterPassModifier(String controlCode) {
        ControlCode = controlCode;
    }

    public String getControlCode() {
        return ControlCode;
    }
}
