package com.tuogo.cicd;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.tuogo.store" })
@MapperScan(basePackages = { "com.tuogo.**.dao", "com.tuogo.**.mapper" })
public class TuogoDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuogoDemoApplication.class, args);
    }

}
