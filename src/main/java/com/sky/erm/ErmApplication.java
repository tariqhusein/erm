package com.sky.erm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.sky.erm")
@EnableScheduling
public class ErmApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErmApplication.class, args);
    }

}
