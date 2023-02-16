package com.codetrik.simplePublisher.service;

import com.codetrik.BeanQualifier;
import com.codetrik.Message;
import com.codetrik.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.codetrik.BeanQualifier.FASTER_XML_MAPPER;
import static com.codetrik.BeanQualifier.USER_DIRECT_MESSAGE;
import static com.codetrik.Constants.USER_DIRECT_1;
import static com.codetrik.Constants.USER_EXCHANGE;
import static com.codetrik.Constants.USER_QUEUE_1;
import static com.codetrik.Constants.USER_QUEUE_1_KEY_1;

@Component
@Qualifier(USER_DIRECT_MESSAGE)
public class UserDirectMessage implements Message<User>{
    private final ObjectMapper mapper;

    public UserDirectMessage(@Qualifier(FASTER_XML_MAPPER) ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void publishMessage(Channel channel, User user) throws IOException {
        channel.exchangeDeclare(USER_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(USER_QUEUE_1,false,false,false,null);
        channel.queueBind(USER_QUEUE_1,USER_EXCHANGE,USER_QUEUE_1_KEY_1);
        channel.basicPublish(USER_EXCHANGE,USER_QUEUE_1_KEY_1,null,mapper.writeValueAsBytes(user));
    }

    @Override
    public void consumeMessage(Channel channel) throws Exception {

    }
}
