package com.codetrik.simplePublisher.service;

import com.codetrik.Message;
import com.codetrik.dto.CarInsurance;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.codetrik.BeanQualifier.CAR_INSURANCE_MESSAGE;
import static com.codetrik.BeanQualifier.FASTER_XML_MAPPER;
import static com.codetrik.Constants.CORRELATION_ID_2;
import static com.codetrik.Constants.INSURANCE_EXCHANGE;
import static com.codetrik.Constants.INSURANCE_QUEUE;
import static com.codetrik.Constants.INSURANCE_TEMP_QUEUE;

@Service
@Getter
@Setter
@Qualifier(CAR_INSURANCE_MESSAGE)
@Lazy
public class CarInsuranceMessage implements Message<CarInsurance> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper mapper;

    public CarInsuranceMessage(ApplicationEventPublisher applicationEventPublisher,
                               @Qualifier(FASTER_XML_MAPPER) ObjectMapper mapper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.mapper = mapper;
    }

    @Override
    public void publishMessage(Channel channel, CarInsurance carInsurance) throws Exception {
        channel.exchangeDeclare(INSURANCE_EXCHANGE, BuiltinExchangeType.FANOUT);
        channel.queueDeclare(INSURANCE_QUEUE,false,false,false,null);
        var prop = new AMQP.BasicProperties().builder()
                .correlationId(CORRELATION_ID_2)
                .replyTo(INSURANCE_TEMP_QUEUE)
                .build();
        channel.basicPublish(INSURANCE_EXCHANGE,"",prop,mapper.writeValueAsBytes(carInsurance));
    }

    @Override
    public void consumeMessage(Channel channel) throws Exception {

    }
}
