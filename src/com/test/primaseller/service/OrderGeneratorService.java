package com.test.primaseller.service;

import java.util.concurrent.atomic.AtomicInteger;

public class OrderGeneratorService {

    private static OrderGeneratorService instance;
    AtomicInteger counter = new AtomicInteger();

    private OrderGeneratorService() {

    }

    public static OrderGeneratorService getInstance() {
        if (instance == null) {
            synchronized (OrderGeneratorService.class) {
                if (instance == null)
                    instance = new OrderGeneratorService();
            }
        }
        return instance;
    }

    public Integer getOrderNumber() {
        return this.counter.incrementAndGet();
    }
}
