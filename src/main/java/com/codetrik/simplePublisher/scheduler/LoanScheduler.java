package com.codetrik.simplePublisher.scheduler;

import com.codetrik.simplePublisher.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final ExecutorService executorService;

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public LoanScheduler(@Qualifier(LOAN_SERVICE) LoanService loanService,
                         @Qualifier(SERVICE_EXECUTOR) ExecutorService executorService) {
        this.loanService = loanService;
        this.executorService = executorService;
    }

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.SECONDS)
    @Async
    public void processFeedBack(){
        logger.info("[INFO] feedback scheduler started successfully");
        this.loanService.consumeLoanApplicationFeedback();
    }
}
