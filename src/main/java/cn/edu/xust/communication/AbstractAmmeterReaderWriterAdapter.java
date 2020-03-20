package cn.edu.xust.communication;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/20 00:17
 */
public abstract class AbstractAmmeterReaderWriterAdapter implements AmmeterReaderWriter {

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
}
