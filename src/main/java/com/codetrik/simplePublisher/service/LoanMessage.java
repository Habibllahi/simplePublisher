package com.codetrik.simplePublisher.service;

import com.codetrik.Message;
import com.codetrik.dto.LoanApplication;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.codetrik.Constants.*;

@Service
@Getter
@Setter
@Qualifier("loan-message")
public class LoanMessage implements Message<LoanApplication> {
    private Logger logger = LoggerFactory.getLogger("LoanMessage");
    @Override
    public void publishMessage(Channel channel, LoanApplication loanApplication) throws IOException {
            var mapper = new ObjectMapper();

            var loanQueueDeclare = channel.queueDeclare(LOAN_QUEUE,false,
                    false,false,null);

            var propertiesBuilder = new AMQP.BasicProperties().builder();

            var properties = propertiesBuilder
                    .correlationId(CORRELATION_ID_1)
                    .replyTo(LOAN_TEMP_QUEUE)
                    .build();

            channel.basicPublish("",LOAN_QUEUE,properties,mapper.writeValueAsBytes(loanApplication));
    }

    @Override
    public LoanApplication consumeMessage(Channel channel) throws IOException {
            var mapper = new ObjectMapper();

            AtomicReference<LoanApplication> atomicLoanApplication = new AtomicReference<>();

            var replyToQueueDeclare = channel.queueDeclare(LOAN_TEMP_QUEUE,false,
                    false,false,null); //declare a server-named Queue and get the Queue name

            DeliverCallback deliveryCallback = (consumerTag, message)->{
                if(message.getProperties().getCorrelationId().equals(CORRELATION_ID_1)){
                    var data = mapper.readValue(message.getBody(),LoanApplication.class);
                    atomicLoanApplication.set(data);
                    this.logger.info("[FEEDBACK] message got processed is: "+ data.getResponse().getOk().booleanValue());
                }
            };

            CancelCallback cancelCallback = (consumerTag)->{};

            //This call is async, you are rest assure it will do the job (deliveryCallback or cancelCallBack)
            // later in future when message become available
            channel.basicConsume(LOAN_TEMP_QUEUE,true,deliveryCallback,cancelCallback);
            return null;
    }
}