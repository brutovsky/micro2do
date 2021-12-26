package com.brtvsk.todoservice.config;

import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;
import com.github.cloudyrock.spring.v5.MongockSpring5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
public class MigrationConfig {
    @Value("${mongock.change-logs-scan-package}")
    private String scanPackage;

    @Bean
    public MongockSpring5.MongockApplicationRunner mongockApplicationRunnerBean(ApplicationContext springContext, MongoTemplate mongoTemplate) {
        return MongockSpring5.builder()
                .setDriver(SpringDataMongoV3Driver.withDefaultLock(mongoTemplate))
                .addChangeLogsScanPackages(List.of(scanPackage))
                .setSpringContext(springContext)
                .buildApplicationRunner();
    }
}
