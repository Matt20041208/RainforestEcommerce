package com.yulin.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class YulinEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YulinEcommerceApplication.class, args);
    }

}