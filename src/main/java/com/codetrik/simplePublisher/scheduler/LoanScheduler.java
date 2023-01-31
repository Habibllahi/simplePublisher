package com.codetrik.simplePublisher.scheduler;

import com.codetrik.simplePublisher.service.LoanService;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class LoanScheduler {
    private final LoanService loanService;
    private final ExecutorService executorService;

    private Logger logger = LoggerFactory.getLogger("LoanScheduler");

    public LoanScheduler(@Qualifier("loan-service") LoanService loanService,
                         @Qualifier("service-executor") ExecutorService executorService) {
        this.loanService = loanService;
        this.executorService = executorService;
    }

    @Scheduled(fixedDelay = 2L, timeUnit = TimeUnit.SECONDS)
    public void processFeedBack(){
        logger.info("[INFO] feedback scheduler started successfully");
        var box = new SimplePublisherServiceBox(null, null);
        box.setExecutorService(this.executorService);
        box.asyncSubmitProcess(()->{
           this.loanService.consumeLoanApplicationFeedback(box);
           box.doPostProcessing();
        });
    }
}
