package cn.edu.xust.service;


import cn.edu.xust.bean.ElectricMeter;

/**
 * @author ：huangxin
 * @since ：2020/02/18 18:28
 */
public interface ElectricMeterService {

    /**
     * 新增一条电表数据记录
     *
     * @param electricMeter 电表数据对象
     * @return 返回新增的记录ID
     */
    int add(ElectricMeter electricMeter);

    /**
     * 非全字段更新电表数据
     *
     * @param electricMeter
     * @return 更新操作影响到的记录数： 0 表示更新失败 1表示更新成功
     */
    int updateByElectricMeterIdSelective(ElectricMeter electricMeter);

    /**
     * 根据设备Id查询设备信息
     *
     * @param deviceId 设备Id
     * @return ElectricMeter
     */
    ElectricMeter getElectricMeterById(int deviceId);
}
