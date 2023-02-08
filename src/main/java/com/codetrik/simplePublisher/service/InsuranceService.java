package com.codetrik.simplePublisher.service;

import com.codetrik.dto.CarInsurance;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.rabbitmq.client.Connection;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import static com.codetrik.BeanQualifier.CAR_INSURANCE_MESSAGE;
import static com.codetrik.BeanQualifier.CAR_INSURANCE_SERVICE;
import static com.codetrik.BeanQualifier.RABBIT_MQ_CONNECTION;

@Service
@Getter
@Setter
@Qualifier(CAR_INSURANCE_SERVICE)
@Lazy
public class InsuranceService {
    private final CarInsuranceMessage carInsuranceMessage;
    private final Connection connection;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public InsuranceService(@Qualifier(CAR_INSURANCE_MESSAGE) CarInsuranceMessage carInsuranceMessage,
                            @Qualifier(RABBIT_MQ_CONNECTION) Connection connection) {
        this.carInsuranceMessage = carInsuranceMessage;
        this.connection = connection;
    }

    public void publishCarInsurance(SimplePublisherServiceBox box, CarInsurance carInsurance){
        try{
            var recoverableChanel = this.connection.createChannel();
            box.setChannel(recoverableChanel);
            this.carInsuranceMessage.publishMessage(recoverableChanel,carInsurance);
            box.getServiceResponse().setCarInsurance(carInsurance);
        }catch (Exception e){
            box.getServiceResponse().setErrorMessage(e.getMessage());
            logger.error(e.getMessage(),e);
        }
    }

}
