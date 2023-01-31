package com.codetrik.simplePublisher.controller;

import com.codetrik.Message;
import com.codetrik.request.UserServiceRequest;
import com.codetrik.response.UserServiceResponse;
import com.codetrik.simplePublisher.service.UserService;
import com.codetrik.simplePublisher.setup.SimplePublisherServiceBox;
import com.rabbitmq.client.Connection;
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
public class UserController {


    private final ExecutorService executorService;
    private final UserService userService;

    public UserController(@Qualifier("service-executor") ExecutorService executorService,@Qualifier("user-service") UserService userService) {
        this.executorService = executorService;
        this.userService = userService;
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
}
