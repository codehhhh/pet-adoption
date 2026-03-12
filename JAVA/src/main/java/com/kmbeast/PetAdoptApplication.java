package com.kmbeast;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动类
 */
@MapperScan("com.kmbeast.mapper")
@SpringBootApplication
@EnableScheduling
public class PetAdoptApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetAdoptApplication.class, args);
    }
}
