package cn.edu.xust.communication.remote;

import cn.edu.xust.bean.AmmeterParameter;

import java.util.List;

/**
 * 电表遥测接口
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/26 10:39
 */
public interface AmmeterRemoteReader {
    /**
     * 实时数据
     *
     * @param deviceNumberList 电表表号List
     * @return List
     */
    List<AmmeterParameter> realTelemetry(List<String> deviceNumberList);

    /**
     * 实时区域数据
     *
     * @param deviceNumberList 电表表号List
     * @return List
     */
    List<AmmeterParameter> realRegion(List<String> deviceNumberList);

    /**
     * 实时表数据
     *
     * @param deviceNumberList 电表表号List
     * @return List
     */
    List<AmmeterParameter> realMeter(List<String> deviceNumberList);

    /**
     * 实时状态数据
     *
     * @param deviceNumberList 电表表号List
     * @return List
     */
    List<AmmeterParameter> realState(List<String> deviceNumberList);
}
