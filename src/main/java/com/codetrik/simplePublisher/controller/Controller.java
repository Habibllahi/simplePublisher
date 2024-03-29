package com.codetrik.simplePublisher.controller;

import com.codetrik.request.UserServiceRequest;
import com.codetrik.response.UserServiceResponse;
import com.codetrik.simplePublisher.service.InsuranceService;
import com.codetrik.simplePublisher.service.LoanService;
import com.codetrik.simplePublisher.service.UserService;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;

import static com.codetrik.BeanQualifier.CAR_INSURANCE_SERVICE;
import static com.codetrik.BeanQualifier.LOAN_SERVICE;
import static com.codetrik.BeanQualifier.SERVICE_EXECUTOR;
import static com.codetrik.BeanQualifier.USER_SERVICE;
import static com.codetrik.Constants.DEFAULT;
import static com.codetrik.Constants.DIRECT;

@RestController
@RequestMapping("/api/v1")
public class Controller {


    private final ExecutorService executorService;
    private final UserService userService;
    private final LoanService loanService;
    private final InsuranceService insuranceService;

    public Controller(@Qualifier(SERVICE_EXECUTOR) ExecutorService executorService, @Qualifier(USER_SERVICE) UserService userService,
                      @Qualifier(LOAN_SERVICE) LoanService loanService,@Qualifier(CAR_INSURANCE_SERVICE) InsuranceService insuranceService) {
        this.executorService = executorService;
        this.userService = userService;
        this.loanService = loanService;
        this.insuranceService = insuranceService;
    }

    @PostMapping("/user")
    public DeferredResult<ResponseEntity<UserServiceResponse>> postUser(@RequestBody UserServiceRequest requestBody,
                                                                        @RequestParam(name="mode", required = false, defaultValue = DEFAULT)String mode){
        var result = new DeferredResult<ResponseEntity<UserServiceResponse>>();
        var box = new SimplePublisherServiceBox(requestBody, new UserServiceResponse());
        box.setExecutorService(this.executorService);
        box.asyncSubmitProcess(()->{
            //call service function
            switch (mode){
                case DEFAULT -> this.userService.publishUser(box,requestBody.getUser());
                case DIRECT -> this.userService.publishUserDirect(box,requestBody.getUser());
                default -> box.getServiceResponse().setErrorMessage("No mode match, hence cant publish message");
            }
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

    @PostMapping("/car-insurance")
    public DeferredResult<ResponseEntity<UserServiceResponse>> postCarInsurance(@RequestBody UserServiceRequest requestBody){
        var result = new DeferredResult<ResponseEntity<UserServiceResponse>>();
        var box = new SimplePublisherServiceBox(requestBody, new UserServiceResponse());
        box.setExecutorService(this.executorService);
        box.asyncSubmitProcess(()->{
            //call service function
            this.insuranceService.publishCarInsurance(box,requestBody.getCarInsurance());
            result.setResult(new ResponseEntity<>(box.getServiceResponse(),HttpStatus.CREATED));
        });
        result.onCompletion(()-> box.doPostProcessing());
        return result;
    }
}
