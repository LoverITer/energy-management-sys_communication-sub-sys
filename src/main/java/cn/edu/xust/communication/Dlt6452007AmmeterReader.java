package cn.edu.xust.communication;

import cn.edu.xust.communication.enums.AmmeterReader;
import cn.edu.xust.communication.protocol.Dlt645Frame;
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
    private String ammeterId;

    public Dlt6452007AmmeterReader(String ammeterChannelIp, String ammeterId) {
        this.ammeterChannelIp = ammeterChannelIp;
        this.ammeterId = ammeterId;
    }

    @Override
    public void readCurrentVA() {
       
    }

    @Override
    public void readCurrentVB() {
       
    }

    @Override
    public void readCurrentVC() {
       
    }

    @Override
    public void readCurrentIA() {
       
    }

    @Override
    public void readCurrentIB() {
       
    }

    @Override
    public void readCurrentIC() {
       
    }

    @Override
    public void readCurrentActivePower() {
       
    }

    @Override
    public void readCurrentReactivePower() {
       
    }

    @Override
    public void readCurrentPowerFactor() {
       
    }

    @Override
    public void readCurrentTotalApparentPower() {
       
    }

    @Override
    public void readCurrentTotalActiveEnergy() {
        Dlt645Frame frame = new Dlt645Frame(ammeterId, AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 34 33");
        NettyServer.writeCommand(ammeterChannelIp, frame.createFrame());
    }

    @Override
    public void readCurrentPositiveActiveEnergy() {
       
    }

    @Override
    public void readCurrentNegativeActiveEnergy() {
       
    }

    /**
     * 自动调用所有采集方法
     */
    public void start() {
        try {
            Class<? extends Dlt6452007AmmeterReader> clazz = this.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if(!"start".equalsIgnoreCase(method.getName())&&method.getName().startsWith("read")) {
                    method.invoke(this);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
