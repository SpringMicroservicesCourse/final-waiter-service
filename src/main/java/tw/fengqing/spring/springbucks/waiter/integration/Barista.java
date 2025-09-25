package tw.fengqing.spring.springbucks.waiter.integration;

import org.springframework.cloud.stream.function.StreamBridge;

public interface Barista {
    String NEW_ORDERS = "newOrders";
    String FINISHED_ORDERS = "finishedOrders";
    
    StreamBridge newOrders();
}
