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

import static com.codetrik.BeanQualifier.FASTER_XML_MAPPER;
import static com.codetrik.BeanQualifier.USER_MESSAGE;
import static com.codetrik.Constants.USER_QUEUE;


@Service
@Getter
@Setter
@Qualifier(USER_MESSAGE)
public class UserMessage implements Message<User> {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final ObjectMapper mapper;

    public UserMessage(@Qualifier(FASTER_XML_MAPPER) ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void publishMessage(Channel channel, User user) throws IOException {
            channel.queueDeclare(USER_QUEUE,false,false,false,null);
            channel.basicPublish("",USER_QUEUE,null, this.mapper.writeValueAsBytes(user));
    }

    @Override
    public void consumeMessage(Channel channel) {

    }
}
