package cn.edu.xust.communication;

import cn.edu.xust.communication.remote.AmmeterRemoteReader;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/20 00:17
 */
public abstract class AbstractAmmeterReaderWriterAdapter
        implements AmmeterReaderWriter, AmmeterRemoteReader {

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
       
    }

    @Override
    public void readCurrentPositiveActiveEnergy() {
       
    }

    @Override
    public void readCurrentNegativeActiveEnergy() {
       
    }
}
