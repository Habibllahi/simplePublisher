package com.codetrik.simplePublisher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ConcurrencyConfig {

    @Bean("rabbitmq-executor")
    public ExecutorService newFixedThreadPool(){
        return Executors.newFixedThreadPool(10);
    }
    @Bean("service-executor")
    public ExecutorService newScheduledThreadPool(){
        return Executors.newScheduledThreadPool(10);
    }
}
