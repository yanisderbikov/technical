package com.example.technical.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import numbers.NumberGeneratorGrpc;
import numbers.ProtoNums;

import java.util.concurrent.atomic.AtomicInteger;

public class NumberGeneratorClient {

    private static final int firstVal = 0;
    private static final int lastVal = 30;
    private static final AtomicInteger lastNumberFromServer = new AtomicInteger(0);
    // Флаг для отслеживания, было ли последнее число от сервера уже использовано
    private static volatile boolean numberUsed = true;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();

        NumberGeneratorGrpc.NumberGeneratorStub asyncClient = NumberGeneratorGrpc.newStub(channel);

        ProtoNums.NumberRange request = ProtoNums.NumberRange.newBuilder().setFirstValue(firstVal).setLastValue(lastVal).build();

        asyncClient.generateNumbers(request, new StreamObserver<ProtoNums.NumberResponse>() {
            @Override
            public void onNext(ProtoNums.NumberResponse value) {
                System.out.println("Received number from server: " + value.getValue());
                lastNumberFromServer.set(value.getValue());
                numberUsed = false; // Помечаем число как неиспользованное
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed.");
                channel.shutdownNow();
            }
        });

        int currentValue = 0;
        int lastGot = -1;
        for (int i = 0; i <= 50; i++) {
            int lastNum = lastNumberFromServer.get();
            if (lastGot == lastNum) {
                currentValue = currentValue + 1;
            } else {
                lastGot = lastNum;
                currentValue = currentValue + lastNum + 1;
            }
            System.out.println("currentValue: " + currentValue);
            Thread.sleep(1000); // Пауза на 1 секунду
        }
    }
}
