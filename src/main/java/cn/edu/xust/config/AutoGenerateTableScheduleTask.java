package cn.edu.xust.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 自动生成表定时任务，每月的最后一天的23:00执行这个任务
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 22:30
 */
@Slf4j
@Configuration
public class AutoGenerateTableScheduleTask {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 失败后重试的次数
     */
    private static int RETRY = 5;

    /**
     * <p>
     * cron: 秒 分 时 日 月 星期
     * * 匹配任意值 ？ 只能用在月和星期两个域。它也匹配域的任意值
     * </p>
     * 每月的最后一天的23:00执行任务
     */
    @Scheduled(cron = "0 0 23 28-31 * ?")
    public void create() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DATE) == calendar.getActualMaximum(Calendar.DATE)) {
            //判断如果确实是本月的最后一天
            //创建表：CREATE TABLE `electric_meter_202002`
            String table = "electric_meter_" + new SimpleDateFormat("yyyyMM").format(calendar.getTime());
            log.info("开始创建{}", table);
            try {
                ClassPathResource resource = new ClassPathResource("electric_meter.sql");
                InputStream inputStream = resource.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                //10k
                byte[] bytes = new byte[1024 * 10];
                StringBuilder sb = new StringBuilder();
                while (bis.read(bytes) != -1) {
                    sb.append(new String(bytes, Charset.defaultCharset()));
                }
                String cSql = sb.toString().replaceFirst("electric_meter", table);
                jdbcTemplate.execute(cSql);
                log.info("创建{}成功", table);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("创建表" + table + "失败");
                while (RETRY > 0) {
                    log.info("准备重试创建"+table);
                    RETRY--;
                    create();
                }
            }
        }
    }


}
