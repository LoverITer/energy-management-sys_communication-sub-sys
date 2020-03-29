package cn.edu.xust.communication.remote;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.AbstractAmmeterReaderWriterAdapter;
import cn.edu.xust.communication.AmmeterAutoReader;
import cn.edu.xust.communication.model.Result;
import cn.edu.xust.communication.protocol.Dlt645Frame;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.communication.server.cache.ChannelMap;
import cn.edu.xust.communication.util.Dlt645FrameUtils;
import cn.edu.xust.communication.util.FileUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 电表遥测实现类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/26 10:33
 */
@Component
public class AmmeterRemoteReaderImpl extends AbstractAmmeterReaderWriterAdapter {



    @Override
    public List<AmmeterParameter> realTelemetry(List<String> deviceNumberList) {
        ArrayList<AmmeterParameter> list = new ArrayList<>();
        deviceNumberList.forEach(deviceNum -> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
            AmmeterParameter ammeterParameter = new AmmeterParameter();
            ammeterParameter.setDeviceNumber(deviceNum);
            //电流
            ammeterParameter.setCurrentACurrent(reader.readCurrentIA());
            ammeterParameter.setCurrentBCurrent(reader.readCurrentIB());
            ammeterParameter.setCurrentCCurrent(reader.readCurrentIC());
            //电压
            ammeterParameter.setCurrentAVoltage(reader.readCurrentVA());
            ammeterParameter.setCurrentBVoltage(reader.readCurrentVB());
            ammeterParameter.setCurrentCVoltage(reader.readCurrentVC());
           //有、无功功率
            ammeterParameter.setCurrentActivePower(reader.readCurrentActivePower());
            ammeterParameter.setCurrentReactivePower(reader.readCurrentReactivePower());
           //功率因数
            ammeterParameter.setCurrentPowerFactor(reader.readCurrentPowerFactor());
            list.add(ammeterParameter);
       });
        return list;
    }

    @Override
    public List<AmmeterParameter> realRegion(List<String> deviceNumberList) {
        ArrayList<AmmeterParameter> list = new ArrayList<>();
        deviceNumberList.forEach(deviceNum-> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
            AmmeterParameter ammeterParameter = new AmmeterParameter();
            ammeterParameter.setCurrentTotalActivePower( reader.readCurrentTotalActiveEnergy());
            list.add(ammeterParameter);
        });
        return list;
    }

    @Override
    public List<AmmeterParameter> realMeter(List<String> deviceNumberList) {
        ArrayList<AmmeterParameter> list = new ArrayList<>();
        deviceNumberList.forEach(deviceNum-> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
            AmmeterParameter ammeterParameter = new AmmeterParameter();
            ammeterParameter.setCurrentPositiveActivePower(reader.readCurrentPositiveActiveEnergy());
            ammeterParameter.setCurrentNegtiveActivePower(reader.readCurrentNegativeActiveEnergy());
            list.add(ammeterParameter);
        });
        return list;
    }

    @Override
    public List<AmmeterParameter> realState(List<String> deviceNumberList) {
        ArrayList<AmmeterParameter> list = new ArrayList<>();
        deviceNumberList.forEach(deviceNum-> {
            AmmeterParameter ammeterParameter = new AmmeterParameter();
            Result result = NettyServer.writeCommand(ChannelMap.getDeviceIp(deviceNum), Dlt645Frame.BROADCAST_FRAME, UUID.randomUUID().toString());
            if(result.getCode()!=1){
                Map<String,String> properties= FileUtils.getPropertiesMap();
                String status=properties.get("ERR_"+ Dlt645FrameUtils.getData(result.getMessage()));
                ammeterParameter.setAmmeterStatus(status);
                list.add(ammeterParameter);
            }else{
                ammeterParameter.setAmmeterStatus("Equipment is running normally");
            }
        });
        return list;
    }
}
