package com.codetrik.simplePublisher.event;

import com.codetrik.dto.LoanApplication;
import com.codetrik.event.MQEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class MQLoanMessageEvent extends ApplicationEvent {
    MQEvent<LoanApplication> loanMQMessage;
    public MQLoanMessageEvent(Object source, MQEvent<LoanApplication> loanMQMessage) {
        super(source);
        this.loanMQMessage = loanMQMessage;
    }
}
