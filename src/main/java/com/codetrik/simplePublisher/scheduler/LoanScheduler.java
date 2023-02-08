package com.codetrik.simplePublisher.scheduler;

import com.codetrik.simplePublisher.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.codetrik.BeanQualifier.LOAN_SERVICE;
import static com.codetrik.BeanQualifier.SERVICE_EXECUTOR;

@Component
public class LoanScheduler {
    private final LoanService loanService;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public LoanScheduler(@Qualifier(LOAN_SERVICE) LoanService loanService) {
        this.loanService = loanService;
    }

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.SECONDS)
    @Async
    public void processFeedBack(){
        logger.info("[INFO] feedback scheduler started successfully");
        this.loanService.consumeLoanApplicationFeedback();
    }
}
