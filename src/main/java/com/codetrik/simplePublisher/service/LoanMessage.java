package com.codetrik.simplePublisher.service;

import com.codetrik.Message;
import com.codetrik.SharedConnectionFactory;
import com.codetrik.dto.LoanApplication;
import com.codetrik.event.MQEvent;
import com.codetrik.simplePublisher.event.MQLoanMessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.codetrik.BeanQualifier.FASTER_XML_MAPPER;
import static com.codetrik.BeanQualifier.LOAN_MESSAGE;
import static com.codetrik.Constants.CORRELATION_ID_1;
import static com.codetrik.Constants.LOAN_QUEUE;
import static com.codetrik.Constants.LOAN_TEMP_QUEUE;


@Service
@Getter
@Setter
@Qualifier(LOAN_MESSAGE)
public class LoanMessage implements Message<LoanApplication> {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper mapper;



    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public LoanMessage(ApplicationEventPublisher applicationEventPublisher,
                       @Qualifier(FASTER_XML_MAPPER) ObjectMapper mapper) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.mapper = mapper;
    }

    @Override
    public void publishMessage(Channel channel, LoanApplication loanApplication) throws IOException {
            var loanQueueDeclare = channel.queueDeclare(LOAN_QUEUE,false,
                    false,false,null);

            var propertiesBuilder = new AMQP.BasicProperties().builder();

            var properties = propertiesBuilder
                    .correlationId(CORRELATION_ID_1)
                    .replyTo(LOAN_TEMP_QUEUE)
                    .build();

            channel.basicPublish("",LOAN_QUEUE,properties,this.mapper.writeValueAsBytes(loanApplication));
    }

    @Override
    public void consumeMessage(Channel channel) throws Exception {
            var replyToQueueDeclare = channel.queueDeclare(LOAN_TEMP_QUEUE,false,
                    false,false,null); //declare a server-named Queue and get the Queue name
            DeliverCallback deliveryCallback = (consumerTag, message)->{
                if(message.getProperties().getCorrelationId().equals(CORRELATION_ID_1)){
                    var data = this.mapper.readValue(message.getBody(),LoanApplication.class);
                    applicationEventPublisher.publishEvent(new MQLoanMessageEvent(this,new MQEvent<>(data)));
                    channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                }
                if(!(replyToQueueDeclare.getMessageCount() > 0)){
                    SharedConnectionFactory.closeChannel(channel);
                }
            };
            CancelCallback cancelCallback = (consumerTag)->{};
            //This call is async, you are rest assure it will do the job (deliveryCallback or cancelCallBack)
            // later in future when message become available
            channel.basicConsume(LOAN_TEMP_QUEUE,false,deliveryCallback,cancelCallback);
    }
}