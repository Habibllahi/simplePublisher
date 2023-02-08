package com.codetrik.simplePublisher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.codetrik.BeanQualifier.RABBIT_MQ_EXECUTOR;
import static com.codetrik.BeanQualifier.SERVICE_EXECUTOR;

@Configuration
public class ConcurrencyConfig {

    @Bean(RABBIT_MQ_EXECUTOR)
    @Lazy
    public ExecutorService newFixedThreadPool(){
        return Executors.newFixedThreadPool(10);
    }
    @Bean(SERVICE_EXECUTOR)
    @Lazy
    public ExecutorService newScheduledThreadPool(){
        return Executors.newScheduledThreadPool(10);
    }
}
