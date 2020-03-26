package cn.edu.xust.communication.remote;

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
     * @param time             时间戳
     */
    void realTelemetry(List<String> deviceNumberList, long time);

    /**
     * 实时区域数据
     *
     * @param deviceNumberList 电表表号List
     * @param time             时间戳
     */
    void realRegion(List<String> deviceNumberList, long time);

    /**
     * 实时表数据
     *
     * @param deviceNumberList 电表表号List
     * @param time             时间戳
     */
    void realMeter(List<String> deviceNumberList, long time);

    /**
     * 实时状态数据
     *
     * @param deviceNumberList 电表表号List
     * @param time             时间戳
     */
    void realState(List<String> deviceNumberList, long time);
}
