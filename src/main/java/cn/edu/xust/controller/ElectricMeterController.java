package cn.edu.xust.controller;

import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.service.ElectricMeterService;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 23:26
 */
@Controller()
public class ElectricMeterController {

    private final ElectricMeterService electricMeterService;

    public ElectricMeterController(ElectricMeterService electricMeterService) {
        this.electricMeterService = electricMeterService;
    }

    @RequestMapping(value = "/test")
    @ResponseBody
    public ElectricMeter equipment() {
        //入参 设备id   根据设备id  查询设备最后一次录入数据时候的 ip地址  实现下发

        //需要给设备发送的 16进制数据
        String msg = " 16 27 88 90 12 45 31 15 41 ";

        ByteBuf message = Unpooled.copiedBuffer(msg.getBytes());
        ElectricMeter electricMeter = new ElectricMeter();
        electricMeter.setUserId(1);
        electricMeter.setElectricMeterId(2323);
        electricMeter.setCurrentTotalElectricity(353.7);
        electricMeterService.add(electricMeter);
        return electricMeter;
    }

}
