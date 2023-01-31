package com.codetrik.simplePublisher;

import com.codetrik.SharedConnectionFactory;
import com.rabbitmq.client.Connection;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.util.concurrent.ExecutorService;


@Configuration
public class RabbitMqConfig {
    private Connection openedConnection;

    private Logger logger = LoggerFactory.getLogger("RabbitMqConfig");

    @Bean("rabbit-mq-connection")
    @DependsOn("rabbitmq-executor")
    @Scope("singleton")
    public Connection connection(@Autowired @Qualifier("rabbitmq-executor") ExecutorService executorService){
        var optionalConnection =  sharedConnectionFactory(executorService).createConnection();
        this.openedConnection = optionalConnection.get();
        return this.openedConnection;
    }
    private SharedConnectionFactory sharedConnectionFactory(ExecutorService executorService){
        return new SharedConnectionFactory(executorService,"localhost",
                "guest","guest", "conn:publisher:simple",
                "/",5672);
    }

    @PreDestroy
    public void releaseResources(){
        try{
            if(this.openedConnection != null && this.openedConnection.isOpen()){
                this.openedConnection.close();
                logger.info("Closes all rabbitMQ resources");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }
}
