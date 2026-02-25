package com.juanlopezaranzazu.springboot_grpc.service;


import org.springframework.grpc.server.service.GrpcService;

import com.juanlopezaranzazu.grpc.PingRequest;
import com.juanlopezaranzazu.grpc.PingResponse;
import com.juanlopezaranzazu.grpc.TestServiceGrpc;
import io.grpc.stub.StreamObserver;

@GrpcService
public class TestGrpcService extends TestServiceGrpc.TestServiceImplBase {

    @Override
    public void ping(PingRequest request,
                     StreamObserver<PingResponse> responseObserver) {

        PingResponse response = PingResponse.newBuilder()
                .setMessage(request.getMessage())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
