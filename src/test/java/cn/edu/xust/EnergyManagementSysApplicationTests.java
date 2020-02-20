package cn.edu.xust;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class EnergyManagementSysApplicationTests {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {

    }


    @Test
    public void test(){
        try {
            //创建表
            String table = "electric_meter_202002";
            String c_sql = "CREATE TABLE " + table + "( id varchar(100) NOT NULL,  gmt_create datetime DEFAULT NULL,"
                    + "  gmt_modified datetime DEFAULT NULL,  app varchar(100) DEFAULT NULL,  _timestamp datetime DEFAULT NULL,"
                    + " resource varchar(500) DEFAULT NULL, pass_qps bigint(11) DEFAULT '0',  success_qps bigint(11) DEFAULT '0',"
                    + " block_qps bigint(11) DEFAULT '0',  exception_qps int(11) DEFAULT '0',  rt double DEFAULT NULL, _count int(11) DEFAULT '0',"
                    + "  resource_code int(11) DEFAULT NULL,  PRIMARY KEY (`id`),  KEY `INDEX_TIMESTAMP` (`_timestamp`),"
                    + "  KEY `INDEX_TSP_RESOURCE` (`_timestamp`,`resource`)) ";
            jdbcTemplate.execute(c_sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
