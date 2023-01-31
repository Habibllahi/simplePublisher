package com.codetrik.simplePublisher.controller;

import com.codetrik.request.UserServiceRequest;
import com.codetrik.response.UserServiceResponse;
import com.codetrik.simplePublisher.service.LoanService;
import com.codetrik.simplePublisher.service.UserService;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/api/v1")
public class Controller {


    private final ExecutorService executorService;
    private final UserService userService;
    private final LoanService loanService;

    public Controller(@Qualifier("service-executor") ExecutorService executorService, @Qualifier("user-service") UserService userService,
                      @Qualifier("loan-service") LoanService loanService) {
        this.executorService = executorService;
        this.userService = userService;
        this.loanService = loanService;
    }

    @PostMapping("/user")
    public DeferredResult<ResponseEntity<UserServiceResponse>> postUser(@RequestBody UserServiceRequest requestBody){
        var result = new DeferredResult<ResponseEntity<UserServiceResponse>>();
        var box = new SimplePublisherServiceBox(requestBody, new UserServiceResponse());
        box.setExecutorService(this.executorService);
        box.asyncSubmitProcess(()->{
            //call service function
            this.userService.publishUser(box,requestBody.getUser());
            result.setResult(new ResponseEntity<>(box.getServiceResponse(),HttpStatus.CREATED));
        });
        result.onCompletion(()-> box.doPostProcessing());
        return result;
    }

    @PostMapping("/loan")
    public DeferredResult<ResponseEntity<UserServiceResponse>> postLoan(@RequestBody UserServiceRequest requestBody){
        var result = new DeferredResult<ResponseEntity<UserServiceResponse>>();
        var box = new SimplePublisherServiceBox(requestBody, new UserServiceResponse());
        box.setExecutorService(this.executorService);
        box.asyncSubmitProcess(()->{
            //call service function
            this.loanService.publishLoanApplication(box,requestBody.getLoanApplication());
            result.setResult(new ResponseEntity<>(box.getServiceResponse(),HttpStatus.CREATED));
        });
        result.onCompletion(()-> box.doPostProcessing());
        return result;
    }
}
