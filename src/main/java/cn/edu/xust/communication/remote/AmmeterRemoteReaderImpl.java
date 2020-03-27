package cn.edu.xust.communication.remote;

import cn.edu.xust.communication.AbstractAmmeterReaderWriterAdapter;
import cn.edu.xust.communication.AmmeterAutoReader;
import cn.edu.xust.communication.server.cache.ChannelMap;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void realTelemetry(List<String> deviceNumberList, long time) {
       deviceNumberList.forEach(deviceNum->{
           AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
           //电流
           reader.readCurrentIA();
           reader.readCurrentIB();
           reader.readCurrentIC();
           //电压
           reader.readCurrentVA();
           reader.readCurrentVB();
           reader.readCurrentVC();
           //有、无功功率
           reader.readCurrentActivePower();
           reader.readCurrentReactivePower();
           //功率因数
           reader.readCurrentPowerFactor();
       });
    }

    @Override
    public void realRegion(List<String> deviceNumberList, long time) {
        deviceNumberList.forEach(deviceNum-> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
            reader.readCurrentTotalActiveEnergy();
        });
    }

    @Override
    public void realMeter(List<String> deviceNumberList, long time) {
        deviceNumberList.forEach(deviceNum-> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
            reader.readCurrentPositiveActiveEnergy();
            reader.readCurrentNegativeActiveEnergy();
        });
    }

    @Override
    public void realState(List<String> deviceNumberList, long time) {
        deviceNumberList.forEach(deviceNum-> {
            AmmeterAutoReader reader = new AmmeterAutoReader(ChannelMap.getDeviceIp(deviceNum), deviceNum);
        });
    }
}
