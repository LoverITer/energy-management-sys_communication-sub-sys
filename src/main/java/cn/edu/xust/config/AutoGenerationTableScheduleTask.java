package cn.edu.xust.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 自动生成表定时任务，每月的最后一天的23:00执行这个任务
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 22:30
 */
@Component
public class AutoGenerationTableScheduleTask {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * cron: 秒 分 时 日 月 星期
     * * 匹配任意值 ？ 只能用在月和星期两个域。它也匹配域的任意值
     *
     * 每月的最后一天的23:00执行任务
     */
    @Scheduled(cron="0 23 0 * * ?")
    public void create(){
        Calendar calendar=Calendar.getInstance();
       /* if(calendar.get(Calendar.DATE)==calendar.getActualMaximum(Calendar.DATE)) {*/
            //判断如果确实是本月的最后一天
            try {
                //创建表
                String table = "electric_meter_202003";
                String c_sql = "CREATE TABLE " + table + "( id varchar(100) NOT NULL,  gmt_create datetime DEFAULT NULL,"
                        + "  gmt_modified datetime DEFAULT NULL,  app varchar(100) DEFAULT NULL,  _timestamp datetime DEFAULT NULL,"
                        + " resource varchar(500) DEFAULT NULL, pass_qps bigint(11) DEFAULT '0',  success_qps bigint(11) DEFAULT '0',"
                        + " block_qps bigint(11) DEFAULT '0',  exception_qps int(11) DEFAULT '0',  rt double DEFAULT NULL, _count int(11) DEFAULT '0',"
                        + "  resource_code int(11) DEFAULT NULL,  PRIMARY KEY (`id`),  KEY `INDEX_TIMESTAMP` (`_timestamp`),"
                        + "  KEY `INDEX_TSP_RESOURCE` (`_timestamp`,`resource`)) ";
                jdbcTemplate.execute(c_sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        /*}*/
    }


}
