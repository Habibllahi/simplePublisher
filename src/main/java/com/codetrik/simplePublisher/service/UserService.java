package com.codetrik.simplePublisher.service;

import com.codetrik.dto.User;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.codetrik.Constants.*;

@Service
@Qualifier("user-service")
public class UserService {
    private final UserMessage userMessage;
    private final Connection connection;

    private Logger logger = LoggerFactory.getLogger("UserService");

    public UserService(@Qualifier("user-message") UserMessage userMessage, @Qualifier("rabbit-mq-connection") Connection connection) {
        this.userMessage = userMessage;
        this.connection = connection;
    }

    public void publishUser(SimplePublisherServiceBox box, User  user){
        try {
            var recoverableChannel = this.connection.createChannel();
            box.setChannel(recoverableChannel);
            this.userMessage.publishMessage(box.getChannel(), user);
            box.getServiceResponse().setUser(user);
        } catch (IOException e) {
            box.getServiceResponse().setErrorMessage(e.getMessage());
            this.logger.error(e.getMessage(),e);
        }
    }
}
