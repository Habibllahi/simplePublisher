package com.codetrik.simplePublisher.event.listner;

import com.codetrik.simplePublisher.event.MQInsuranceMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class MQCarInsuranceMessageEventListener implements ApplicationListener<MQInsuranceMessageEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void onApplicationEvent(MQInsuranceMessageEvent event) {
        logger.info("[CAR INSURANCE MESSAGE ACKNOWLEDGE] insurance with registration " +
                event.getCarInsuranceMQMessage().getMessage().getRegistration() + " is processed");
    }
}
