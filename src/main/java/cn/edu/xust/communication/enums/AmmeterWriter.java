package cn.edu.xust.communication.enums;

/**
 * DL/T645-2007多功能电能表通信协议的扩展协议指定的多功能电能控制码
 * 写数据控制码
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/02 10:28
 */
public enum AmmeterWriter {
    /**
     * 主站向从站请求设置数据(或编程)
     * 数据域长度：L =04H+04H（密码）+04H（操作者代码）+m(数据长度)
     * 数据域：DIODI1DI2DI3+PAP0P1P2+C0C1C2C3+DATA
     * 注	1： PA表示密码权限，P0P1P2为该权限对应的密码。
     * 注	2： 96H级密码权限代表通过密文+MAC的方式进行数据传输，不需要进行密码验证，也不需要编程键配合，二类参数采用此种更新方式。
     * 注	3： 97H级密码权限代表通过明文+MAC的方式进行数据传输，不需要进行密码验证，也不需要编程键配合，一类参数采用此种更新方式。
     * 注	4：密码权限03、04代表通过明文的方式进行数据传输，需要进行密码验证，同时要有编程键配合。
     * 注	5：C0C1C2C3是操作者代码，为要求记录操作人员信息的项目提供数据。
     * 注	6：写数据时数据域的字节数 L≤200。
     * 注	7：对于安全模块参数文件中定义的参数按一类参数更新，其它参数按二类参数更新。
     */
    MasterRequestFrame("14"),

    /**
     * 从站响应数据
     * 数据域长度：L=00H
     */
    SalveResponseFrame("94"),


    /**
     * 从站异常应答
     * 数据长度：L=01H
     */
    SlaveExceptionResponseFrame("D4");


    private final String ControlCode;

    AmmeterWriter(String controlCode) {
        ControlCode = controlCode;
    }

    public String getControlCode() {
        return ControlCode;
    }
}
