package cn.edu.xust.communication;

/**
 * 电表读写接口-通过解析电表返回的16进制获得数据
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/17 19:48
 */
public interface AmmeterReaderWriter {

    /**
     * 当前A相电压
     * @return A相电压（V）
     */
    Double readCurrentVA();

    /**
     * 当前B相电压
     * @return B相电压（V）
     */
    Double readCurrentVB();

    /**
     * 当前C相电压
     * @return C相电压（V）
     */
    Double readCurrentVC();

    /**
     * 当前A相电流
     *
     *
     * @return A相电流（A）
     */
    Double readCurrentIA();

    /**
     * 当前B相电压
     *
     *
     * @return B相电流（A）
     */
    Double readCurrentIB();

    /**
     * 当前C相电压
     *
     *
     * @return C相电流（A）
     */
    Double readCurrentIC();

    /**
     * 当前总有功功率
     *
     *
     * @return 总有功功率(W)
     */
    Double readCurrentActivePower();

    /**
     * 当前总无用功率
     *
     *
     * @return 总有功功率(Var)
     */
    Double readCurrentReactivePower();

    /**
     * 当前总功率因数
     *
     *
     * @return 总功率因数
     */
    Double readCurrentPowerFactor();

    /**
     * 当前总视在功率
     *
     *
     * @return 当前总视在功率（Var）
     */
    Double readCurrentTotalApparentPower();

    /**
     * 读取当前电表总有功电能
     *
     *
     * @return 总有功电能（KWh）
     */
    Double readCurrentTotalActiveEnergy();

    /**
     * 读取当前电表正向有功电能
     *
     *
     * @return 正向有功电能（KWh）
     */
    Double readCurrentPositiveActiveEnergy();


    /**
     * 读取当前电表正向有功电能
     *
     *
     * @return 反向有功电能（KWh）
     */
    Double readCurrentNegativeActiveEnergy();

}
