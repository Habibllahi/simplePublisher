package com.codetrik.simplePublisher.service;

import com.codetrik.Message;
import com.codetrik.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.codetrik.Constants.*;

@Service
@Getter
@Setter
@Qualifier("user-message")
public class UserMessage implements Message<User> {
    private Logger logger = LoggerFactory.getLogger("UserMessage");
    @Override
    public void publishMessage(Channel channel, User user) {
        try {
            var mapper = new ObjectMapper();
            channel.queueDeclare(USER_QUEUE,false,false,false,null);
            channel.basicPublish("",USER_QUEUE,null,mapper.writeValueAsBytes(user));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    @Override
    public void consumeMessage(Channel channel) {

    }
}
