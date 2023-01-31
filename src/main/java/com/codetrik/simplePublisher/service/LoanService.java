package com.codetrik.simplePublisher.service;

import com.codetrik.dto.LoanApplication;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Qualifier("loan-service")
public class LoanService {
    private final LoanMessage loanMessage;
    private final Connection connection;

    private Logger logger = LoggerFactory.getLogger("UserService");

    public LoanService(@Qualifier("loan-message") LoanMessage loanMessage, @Qualifier("rabbit-mq-connection") Connection connection) {
        this.loanMessage = loanMessage;
        this.connection = connection;
    }

    public void publishLoanApplication(SimplePublisherServiceBox box, LoanApplication loanApplication){
        try {
            var recoverableChannel = this.connection.createChannel();
            box.setChannel(recoverableChannel);
            this.loanMessage.publishMessage(box.getChannel(),loanApplication);
            box.getServiceResponse().setLoanApplication(loanApplication);
        } catch (IOException e) {
            box.getServiceResponse().setErrorMessage(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }


    public void consumeLoanApplicationFeedback(SimplePublisherServiceBox box){
        try {
            var recoverableChannel = this.connection.createChannel();
            box.setChannel(recoverableChannel);
            this.loanMessage.consumeMessage(box.getChannel());
        } catch (IOException e) {
            box.getServiceResponse().setErrorMessage(e.getMessage());
            logger.error(e.getMessage(),e);
        }

    }
}
