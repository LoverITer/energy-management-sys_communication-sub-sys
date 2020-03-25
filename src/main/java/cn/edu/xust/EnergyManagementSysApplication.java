package cn.edu.xust;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Huangxin
 */
@EnableScheduling
@MapperScan(value = "cn.edu.xust.mapper")
@SpringBootApplication
@EnableWebMvc
public class EnergyManagementSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyManagementSysApplication.class, args);
    }

}
