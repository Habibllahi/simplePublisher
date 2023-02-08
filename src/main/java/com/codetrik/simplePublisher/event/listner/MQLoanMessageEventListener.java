package com.codetrik.simplePublisher.event.listner;

import com.codetrik.simplePublisher.event.MQLoanMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class MQLoanMessageEventListener implements ApplicationListener<MQLoanMessageEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public void onApplicationEvent(MQLoanMessageEvent event) {
        logger.info("[LOAN MESSAGE ACKNOWLEDGE] loan acknowledge status is " + event.getLoanMQMessage().getMessage().getResponse().getOk());
    }
}
