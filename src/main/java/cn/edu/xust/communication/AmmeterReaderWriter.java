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
     *
     * @param hexString 十六进制字符串
     * @return A相电压（V）
     */
    String readCurrentVA(String hexString);

    /**
     * 当前B相电压
     *
     * @param hexString 十六进制字符串
     * @return B相电压（V）
     */
    String readCurrentVB(String hexString);

    /**
     * 当前C相电压
     *
     * @param hexString 十六进制字符串
     * @return C相电压（V）
     */
    String readCurrentVC(String hexString);

    /**
     * 当前A相电流
     *
     * @param hexString 十六进制字符串
     * @return A相电流（A）
     */
    String readCurrentIA(String hexString);

    /**
     * 当前B相电压
     *
     * @param hexString 十六进制字符串
     * @return B相电流（A）
     */
    String readCurrentIB(String hexString);

    /**
     * 当前C相电压
     *
     * @param hexString 十六进制字符串
     * @return C相电流（A）
     */
    String readCurrentIC(String hexString);

    /**
     * 当前总有功功率
     *
     * @param hexString 十六进制字符串
     * @return 总有功功率(W)
     */
    String readCurrentActivePower(String hexString);

    /**
     * 当前总无用功率
     *
     * @param hexString 十六进制字符串
     * @return 总有功功率(Var)
     */
    String readCurrentReactivePower(String hexString);

    /**
     * 当前总功率因数
     *
     * @param hexString 十六进制字符串
     * @return 总功率因数
     */
    String readCurrentPowerFactor(String hexString);

    /**
     * 当前总视在功率
     *
     * @param hexString 十六进制字符串
     * @return 当前总视在功率（Var）
     */
    String readCurrentTotalApparentPower(String hexString);

    /**
     * 读取当前电表总有功电能
     *
     * @param hexString 十六进制字符串
     * @return 总有功电能（KWh）
     */
    String readCurrentTotalActiveEnergy(String hexString);

    /**
     * 读取当前电表正向有功电能
     *
     * @param hexString 十六进制字符串
     * @return 正向有功电能（KWh）
     */
    String readCurrentPositiveActiveEnergy(String hexString);


    /**
     * 读取当前电表正向有功电能
     *
     * @param hexString 十六进制字符串
     * @return 正向有功电能（KWh）
     */
    String readCurrentNegativeActiveEnergy(String hexString);

}
