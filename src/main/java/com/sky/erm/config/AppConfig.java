package com.sky.erm.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.sky.erm.repository")
@EntityScan(basePackages = "com.sky.erm.domain")
@EnableAutoConfiguration
public class AppConfig {
}
