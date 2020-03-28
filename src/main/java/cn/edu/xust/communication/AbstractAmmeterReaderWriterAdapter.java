package cn.edu.xust.communication;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.remote.AmmeterRemoteReader;

import java.util.List;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/20 00:17
 */
public abstract class AbstractAmmeterReaderWriterAdapter
        implements AmmeterReaderWriter, AmmeterRemoteReader {

    @Override
    public Double readCurrentVA() {
        return null;
    }

    @Override
    public Double readCurrentVB() {
        return null;
    }

    @Override
    public Double readCurrentVC() {
        return null;
    }

    @Override
    public Double readCurrentIA() {
        return null;
    }

    @Override
    public Double readCurrentIB() {
        return null;
    }

    @Override
    public Double readCurrentIC() {
        return null;
    }

    @Override
    public Double readCurrentActivePower() {
        return null;
    }

    @Override
    public Double readCurrentReactivePower() {
        return null;
    }

    @Override
    public Double readCurrentPowerFactor() {
        return null;
    }

    @Override
    public Double readCurrentTotalApparentPower() {
        return null;
    }

    @Override
    public Double readCurrentTotalActiveEnergy() {
        return null;
    }

    @Override
    public Double readCurrentPositiveActiveEnergy() {
        return null;
    }

    @Override
    public Double readCurrentNegativeActiveEnergy() {
       return null;
    }

    @Override
    public List<AmmeterParameter> realTelemetry(List<String> deviceNumberList, long time) {
       return null;
    }

    @Override
    public List<AmmeterParameter> realRegion(List<String> deviceNumberList, long time) {
        return null;
    }

    @Override
    public List<AmmeterParameter> realMeter(List<String> deviceNumberList, long time) {
        return null;
    }

    @Override
    public List<AmmeterParameter> realState(List<String> deviceNumberList, long time) {
        return null;
    }


   
}
