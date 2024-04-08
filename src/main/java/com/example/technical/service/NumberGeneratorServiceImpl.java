package com.example.technical.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import numbers.NumberGeneratorGrpc;
import numbers.ProtoNums;

@GrpcService
public class NumberGeneratorServiceImpl extends NumberGeneratorGrpc.NumberGeneratorImplBase {

    public void generateNumbers(ProtoNums.NumberRange request, StreamObserver<ProtoNums.NumberResponse> responseObserver) {
        int firstValue = request.getFirstValue();
        int lastValue = request.getLastValue();

        for (int i = firstValue + 1; i <= lastValue; i++) {
            ProtoNums.NumberResponse response = ProtoNums.NumberResponse.newBuilder().setValue(i).build();
            responseObserver.onNext(response);

            try {
                Thread.sleep(2000); // Задержка в 2 секунды перед отправкой следующего числа
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        responseObserver.onCompleted();
    }
}
