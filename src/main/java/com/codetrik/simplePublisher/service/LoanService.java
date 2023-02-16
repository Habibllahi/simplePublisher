package com.codetrik.simplePublisher.service;

import com.codetrik.dto.LoanApplication;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.rabbitmq.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.io.IOException;

import static com.codetrik.BeanQualifier.LOAN_MESSAGE;
import static com.codetrik.BeanQualifier.LOAN_SERVICE;
import static com.codetrik.BeanQualifier.RABBIT_MQ_CONNECTION;

@Service
@Qualifier(LOAN_SERVICE)
@Lazy
public class LoanService {
    private final LoanMessage loanMessage;
    private final Connection connection;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public LoanService(@Qualifier(LOAN_MESSAGE) LoanMessage loanMessage, @Qualifier(RABBIT_MQ_CONNECTION) Connection connection) {
        this.loanMessage = loanMessage;
        this.connection = connection;
    }

    public void publishLoanApplication(SimplePublisherServiceBox box, LoanApplication loanApplication){
        try {
            var recoverableChannel = this.connection.createChannel();
            box.setChannel(recoverableChannel);
            this.loanMessage.publishMessage(box.getChannel(),loanApplication);
            box.getServiceResponse().setLoanApplication(loanApplication);
            box.doPostProcessing();
        } catch (IOException e) {
            box.getServiceResponse().setErrorMessage(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }


    public void consumeLoanApplicationFeedback(){
        try {
            var recoverableChannel = this.connection.openChannel();
            if(recoverableChannel.isPresent()){
                this.loanMessage.consumeMessage(recoverableChannel.get());
            }else{
                logger.info("[LOAN CHANNEL] MQ channel 100 creation failed ");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }
}
