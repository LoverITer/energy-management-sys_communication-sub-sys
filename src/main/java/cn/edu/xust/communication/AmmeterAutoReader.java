package cn.edu.xust.communication;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.enums.AmmeterReader;
import cn.edu.xust.communication.enums.AmmeterStatusEnum;
import cn.edu.xust.communication.model.Result;
import cn.edu.xust.communication.protocol.Dlt645Frame;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.communication.server.handler.NettyServerDefaultHandler;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 支持DLT645-2007协议数据解析的具体实现
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/19 11:21
 */
public class AmmeterAutoReader extends AbstractAmmeterReaderWriterAdapter {

    /**
     * 电表端的IP
     */
    private String ammeterChannelIp;
    /**
     * 电表编号
     */
    private String ammeterId;

    private final NettyServer server;

    private static Queue<String> executedMethodQueue = new ConcurrentLinkedQueue<>();

    public static Queue<String> getExecutedMethodQueue(){
        return executedMethodQueue;
    }

    public AmmeterAutoReader(String ammeterChannelIp, String ammeterId) {
        this.ammeterChannelIp = ammeterChannelIp;
        this.ammeterId = ammeterId;
        this.server=new NettyServer();
    }

    @Override
    public Double readCurrentVA() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 34 34 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            System.out.println(ammeterParameter.getCurrentAVoltage());
            return ammeterParameter.getCurrentAVoltage();
        }
        return null;
    }

    @Override
    public Double readCurrentVB() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 35 34 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentBVoltage();
        }
        return null;
    }

    @Override
    public Double readCurrentVC() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 36 34 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentCVoltage();
        }
        return null;
    }

    @Override
    public Double readCurrentIA() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 34 35 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            System.out.println(ammeterParameter.getCurrentACurrent());
            return ammeterParameter.getCurrentACurrent();
        }
        return null;
    }

    @Override
    public Double readCurrentIB() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 35 35 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentBCurrent();
        }
        return null;
    }

    @Override
    public Double readCurrentIC() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 36 35 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentCCurrent();
        }
        return null;
    }

    @Override
    public Double readCurrentActivePower() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 36 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentActivePower();
        }
        return null;
    }

    @Override
    public Double readCurrentReactivePower() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 37 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentReactivePower();
        }
        return null;
    }

    @Override
    public Double readCurrentPowerFactor() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 39 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentPowerFactor();
        }
        return null;
    }

    @Override
    public Double readCurrentTotalApparentPower() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 38 35");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentTotalActivePower();
        }
        return null;
    }


    @Override
    public Double readCurrentTotalActiveEnergy() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 33 33");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentTotalActivePower();
        }
        return null;
    }

    @Override
    public Double readCurrentPositiveActiveEnergy() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 34 33");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentPositiveActivePower();
        }
        return null;
    }

    @Override
    public Double readCurrentNegativeActiveEnergy() {
        Result result = this.sendCommand(AmmeterReader.MasterRequestFrame.getControlCode(),
                AmmeterReader.MasterRequestFrame.getBaseDataLen(), "33 33 35 33");
        if (Objects.nonNull(result)) {
            AmmeterParameter ammeterParameter = this.parseAndPersistenceData(result);
            return ammeterParameter.getCurrentNegtiveActivePower();
        }
        return null;
    }

    /**
     * 解析并持久化数据，之后再将数据返回给调用者
     */
    private AmmeterParameter parseAndPersistenceData(Result result) {
        Dlt645Frame frame = new Dlt645Frame();
        AmmeterParameter ammeterParameter = frame.analysis(result.getMessage());
        if (server != null) {
            server.flushData2DataBase(ammeterParameter);
        }
        return ammeterParameter;
    }

    /**
     * 发送指令
     *
     * @param controlCode        控制码
     * @param dataLen            数据长度
     * @param dataIdentification 数据标识
     */
    private Result sendCommand(String controlCode, String dataLen, String dataIdentification) {
        if (StringUtils.isEmpty(controlCode) || StringUtils.isEmpty(dataLen) || StringUtils.isEmpty(dataIdentification)
                || StringUtils.isEmpty(this.ammeterId) || StringUtils.isEmpty(this.ammeterChannelIp)) {
            throw new IllegalArgumentException("Message parameter can not be null");
        }
        try {
            Dlt645Frame frame = new Dlt645Frame(this.ammeterId, controlCode, dataLen, dataIdentification);
            return NettyServer.writeCommand(this.ammeterChannelIp, frame.createFrame(), UUID.randomUUID().toString());
        } catch (Exception e) {
            //记录故障
            NettyServerDefaultHandler.logException(e, this.ammeterId, AmmeterStatusEnum.NETWORK_ERROR.getMessage());
        }
        return null;
    }

    /**
     * 自动调用所有采集方法
     */
    public void start() {
        try {
            Class<? extends AmmeterAutoReader> clazz = this.getClass();
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!"start".equalsIgnoreCase(method.getName()) &&
                        method.getName().startsWith("read")) {
                    method.invoke(this);
                    //将执行过的方法入队
                    executedMethodQueue.offer(method.getName());
                    Thread.sleep(1000);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
