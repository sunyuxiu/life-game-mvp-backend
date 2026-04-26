package com.lifegame.mvp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lifegame.mvp.mapper")
@EnableScheduling
public class LifeGameApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifeGameApplication.class, args);
    }
}
