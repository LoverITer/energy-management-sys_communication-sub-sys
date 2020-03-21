package cn.edu.xust.communication.server;

import cn.edu.xust.communication.Dlt6452007AmmeterReader;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * 定时发送读取指令定时器
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/20 11:33
 */
public class HashedWheelReader {

    /**
     * 定时默认时间，单位分钟
     */
    private static final long DEFAULT_TICK_DURATION = 5L;
    private final Timer timer = new HashedWheelTimer();


    /**
     * 每5分钟执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer5Min(String remoteAddress, String addressField) {
        this.executeScheduleTask(DEFAULT_TICK_DURATION, TimeUnit.SECONDS, remoteAddress, addressField);
    }

    /**
     * 每15分钟执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer15Min(String remoteAddress, String addressField) {
        this.executeScheduleTask(15, TimeUnit.MINUTES, remoteAddress, addressField);
    }

    /**
     * 每30分钟执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer30Min(String remoteAddress, String addressField) {
        this.executeScheduleTask(30, TimeUnit.MINUTES, remoteAddress, addressField);
    }

    /**
     * 每1小时执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer1Hour(String remoteAddress, String addressField) {
        this.executeScheduleTask(1, TimeUnit.HOURS, remoteAddress, addressField);
    }

    /**
     * 每12小时执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer12Hour(String remoteAddress, String addressField) {
        this.executeScheduleTask(12, TimeUnit.HOURS, remoteAddress, addressField);
    }

    /**
     * 每24小时执行一次
     *
     * @param remoteAddress 通道IP
     * @param addressField  电表编号
     */
    public void executePer24Hour(String remoteAddress, String addressField) {
        this.executeScheduleTask(24, TimeUnit.HOURS, remoteAddress, addressField);
    }

    /**
     * 执行定时任务
     */
    private void executeScheduleTask(long time, TimeUnit timeUnit, String remoteAddress, String addressField) {
        TimerTask task = new TimerTask() {
            @Override
            public void run(Timeout timeout) {
                Dlt6452007AmmeterReader reader = new Dlt6452007AmmeterReader(remoteAddress, addressField);
                //解析数据
                reader.start();
                timer.newTimeout(this, time, timeUnit);
            }
        };
        timer.newTimeout(task,time,timeUnit);
    }


}
