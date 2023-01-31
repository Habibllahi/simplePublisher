package com.codetrik.simplePublisher.setup;

import com.codetrik.common.AbstractServiceBox;
import com.codetrik.request.UserServiceRequest;
import com.codetrik.response.UserServiceResponse;


public class SimplePublisherServiceBox extends
        AbstractServiceBox<UserServiceRequest, UserServiceResponse> {


    public SimplePublisherServiceBox(UserServiceRequest serviceRequest, UserServiceResponse serviceResponse) {
        super(serviceRequest, serviceResponse);
    }
}
