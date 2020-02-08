package com.pl.indexserver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement //启注解事务管理
public class IndexServerApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(IndexServerApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);

    }
}