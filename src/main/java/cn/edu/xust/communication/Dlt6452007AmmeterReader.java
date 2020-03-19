package cn.edu.xust.communication;

import cn.edu.xust.communication.server.NettyServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 支持DLT645-2007协议的电对数据解析的具体实现
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/19 11:21
 */
public class Dlt6452007AmmeterReader extends AbstractAmmeterReaderWriterAdapter {

    /**
     * 电表端的IP
     */
    private String ammeterChannelIp;
    /**
     * 电表编号
     */
    private String ammeterID;

    public Dlt6452007AmmeterReader(String ammeterChannelIp, String ammeterID) {
        this.ammeterChannelIp = ammeterChannelIp;
        this.ammeterID = ammeterID;
    }

    @Override
    public String readCurrentVA() {
        return null;
    }

    @Override
    public String readCurrentVB() {
        return null;
    }

    @Override
    public String readCurrentVC() {
        return null;
    }

    @Override
    public String readCurrentIA() {
        return null;
    }

    @Override
    public String readCurrentIB() {
        return null;
    }

    @Override
    public String readCurrentIC() {
        return null;
    }

    @Override
    public String readCurrentActivePower() {
        return null;
    }

    @Override
    public String readCurrentReactivePower() {
        return null;
    }

    @Override
    public String readCurrentPowerFactor() {
        return null;
    }

    @Override
    public String readCurrentTotalApparentPower() {
        return null;
    }

    @Override
    public String readCurrentTotalActiveEnergy() {
        String cmd = "";
        NettyServer.writeCommand(ammeterChannelIp, cmd);
        return null;
    }

    @Override
    public String readCurrentPositiveActiveEnergy() {
        return null;
    }

    @Override
    public String readCurrentNegativeActiveEnergy() {
        return null;
    }

    public void start() {
        try {
            Class clazz = this.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                method.invoke(clazz.getDeclaredConstructor().newInstance());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
