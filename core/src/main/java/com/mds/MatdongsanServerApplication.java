package com.mds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
@EnableMongoRepositories(basePackages = "com.mds.domain.story.repository.mongo")
public class MatdongsanServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatdongsanServerApplication.class, args);
    }
}
