package cn.edu.xust.communication.remote;

import cn.edu.xust.communication.AbstractAmmeterReaderWriterAdapter;

import java.util.List;

/**
 * 电表遥测实现类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/26 10:33
 */
public class AmmeterRemoteReaderImpl extends AbstractAmmeterReaderWriterAdapter {



    @Override
    public void realTelemetry(List<String> deviceNumberList, long time) {
       deviceNumberList.forEach(deviceIp->{

       });
    }

    @Override
    public void realRegion(List<String> deviceNumberList, long time) {

    }

    @Override
    public void realMeter(List<String> deviceNumberList, long time) {

    }

    @Override
    public void realState(List<String> deviceNumberList, long time) {

    }
}
