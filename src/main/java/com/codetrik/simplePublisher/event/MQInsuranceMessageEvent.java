package com.codetrik.simplePublisher.event;

import com.codetrik.dto.CarInsurance;
import com.codetrik.event.MQEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class MQInsuranceMessageEvent extends ApplicationEvent {
    MQEvent<CarInsurance> carInsuranceMQMessage;
    public MQInsuranceMessageEvent(Object source, MQEvent<CarInsurance> carInsuranceMQMessage) {
        super(source);
        this.carInsuranceMQMessage = carInsuranceMQMessage;
    }
}
