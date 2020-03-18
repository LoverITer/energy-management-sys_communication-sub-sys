package cn.edu.xust.controller;

import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.service.ElectricMeterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试示例
 *
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

    /**
     * 控制器层控制设备示例
     * <strong>NettyServer.writeCommand()</strong>用于给设备写命令
     *
     * @param electricMeterId
     * @return
     */
    @RequestMapping(value = "/control/{electricMeterId}")
    @ResponseBody
    public ElectricMeter equipment(@PathVariable String electricMeterId) {
        //需要给设备发送的 16进制数据
        String cmd = "68 37 03 00 92 81 39 68 11 04 33 33 34 33 38 16";

        ElectricMeter electricMeter = electricMeterService.getElectricMeterById(electricMeterId);

        /**11
         * 执行设备控制
         * 入参: 设备ip和命令
         * 根据写入map 的key（以ip为key）取到map中的SocketChannel 然后执行writeAndFlush发送指令数据数据
         */
        NettyServer.writeCommand(electricMeter.getElectricityIp(), cmd);
        return electricMeter;
    }

}
