package cn.edu.xust.service.impl;

import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.mapper.ElectricMeterMapper;
import cn.edu.xust.service.ElectricMeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


/**
 * @author Huangxin
 * @since ：2020/02/18 18:29
 */
@Service
public class ElectricMeterServiceImpl implements ElectricMeterService {

    @Autowired
    private ElectricMeterMapper electricMeterMapper;

    public ElectricMeterServiceImpl() {
        System.out.println(electricMeterMapper);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int add(ElectricMeter electricMeter) {
        if (Objects.nonNull(electricMeter)) {
            try {
                return electricMeterMapper.insert(electricMeter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public int updateByElectricMeterIdSelective(ElectricMeter electricMeter) {
        if (Objects.nonNull(electricMeter)) {
            try {
                return electricMeterMapper.updateByPrimaryKeySelective(electricMeter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return 0;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    @Override
    public ElectricMeter getElectricMeterById(int deviceId) {
        ElectricMeter electricMeter = null;
        if (deviceId > 0) {
            try {
                electricMeter = electricMeterMapper.selectByPrimaryKey(deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return electricMeter;
    }
}
