package com.codetrik.simplePublisher.service;

import com.codetrik.dto.User;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.codetrik.BeanQualifier.RABBIT_MQ_CONNECTION;
import static com.codetrik.BeanQualifier.USER_DIRECT_MESSAGE;
import static com.codetrik.BeanQualifier.USER_MESSAGE;
import static com.codetrik.BeanQualifier.USER_SERVICE;

@Service
@Qualifier(USER_SERVICE)
@Lazy
public class UserService {
    private final UserMessage userMessage;
    private final UserDirectMessage userDirectMessage;
    private final Connection connection;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public UserService(@Qualifier(USER_MESSAGE) UserMessage userMessage,
                       @Qualifier(USER_DIRECT_MESSAGE) UserDirectMessage userDirectMessage,
                       @Qualifier(RABBIT_MQ_CONNECTION) Connection connection) {
        this.userMessage = userMessage;
        this.userDirectMessage = userDirectMessage;
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

    public void publishUserDirect(SimplePublisherServiceBox box, User  user){
        try {
            var recoverableChannel = this.connection.createChannel();
            box.setChannel(recoverableChannel);
            this.userDirectMessage.publishMessage(box.getChannel(), user);
            box.getServiceResponse().setUser(user);
        } catch (IOException e) {
            box.getServiceResponse().setErrorMessage(e.getMessage());
            this.logger.error(e.getMessage(),e);
        }
    }
}
