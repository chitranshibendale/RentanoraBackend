package com.rentanora.backend.handlers;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.spring.SpringDelegatingLambdaContainerHandler;
import com.rentanora.backend.RentanoraBackendApplication;

public class StreamLambdaHandler
        extends SpringDelegatingLambdaContainerHandler {

    public StreamLambdaHandler() throws ContainerInitializationException {
        super(RentanoraBackendApplication.class);
    }
}