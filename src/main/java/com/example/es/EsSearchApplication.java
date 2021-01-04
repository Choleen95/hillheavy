package com.example.es;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Choleen
 */
@MapperScan("com.example.es.mapper")
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
public class EsSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsSearchApplication.class, args);
    }

}
