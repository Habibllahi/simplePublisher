package com.codetrik.simplePublisher.service;

import com.codetrik.Message;
import com.codetrik.dto.CarInsurance;
import com.codetrik.event.MQEvent;
import com.codetrik.simplePublisher.event.MQInsuranceMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
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
        channel.queueBind(INSURANCE_QUEUE,INSURANCE_EXCHANGE,"");
        var prop = new AMQP.BasicProperties().builder()
                .correlationId(CORRELATION_ID_2)
                .replyTo(INSURANCE_TEMP_QUEUE)
                .build();
        channel.basicPublish(INSURANCE_EXCHANGE,"",prop,mapper.writeValueAsBytes(carInsurance));
    }

    @Override
    public void consumeMessage(Channel channel) throws Exception {
        channel.queueDeclare(INSURANCE_TEMP_QUEUE,false,false,false,null);
        DeliverCallback deliverCallback = (String consumerTag, Delivery message)->{
            if(message.getProperties().getCorrelationId().equals(CORRELATION_ID_2)){
                var data = this.mapper.readValue(message.getBody(),CarInsurance.class);
                applicationEventPublisher.publishEvent(new MQInsuranceMessageEvent(this,
                        new MQEvent<>(data)));
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
        };
        CancelCallback cancelCallback = (String consumerTag)->{};
        channel.basicConsume(INSURANCE_TEMP_QUEUE,false,deliverCallback,cancelCallback);
    }
}
