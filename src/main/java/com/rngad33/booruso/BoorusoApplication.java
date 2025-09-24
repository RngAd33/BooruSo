package com.rngad33.booruso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@SpringBootApplication
public class BoorusoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoorusoApplication.class, args);
        System.out.println("后端服务已启动>>>");
    }

}
