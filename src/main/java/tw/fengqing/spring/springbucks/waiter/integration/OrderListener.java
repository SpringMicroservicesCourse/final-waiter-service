package tw.fengqing.spring.springbucks.waiter.integration;

import tw.fengqing.spring.springbucks.waiter.model.CoffeeOrder;
import tw.fengqing.spring.springbucks.waiter.service.CoffeeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

/**
 * 訂單監聽器 - 處理訂單完成消息
 * 使用 Spring Cloud Stream 函數式編程模型
 */
@Component
@Slf4j
public class OrderListener {
    @Autowired
    private StreamBridge streamBridge;
    @Autowired
    private CoffeeOrderService orderService;
    
    @Value("${stream.bindings.notify-orders-binding}")
    private String notifyOrdersBindingFromConfig;
    
    /**
     * 處理訂單完成消息的函數式 Bean
     * 接收訂單 ID，通知客戶
     * @return 訂單完成處理函數
     */
    @Bean
    public Consumer<Long> finishedOrders() {
        return id -> {
            log.info("We've finished an order [{}].", id);
            CoffeeOrder order = orderService.get(id);
            Message<Long> message = MessageBuilder.withPayload(id)
                    .setHeader("customer", order.getCustomer())
                    .build();
            log.info("Notify the customer: {}", order.getCustomer());
            // 使用StreamBridge發送完成訂單消息
            streamBridge.send(notifyOrdersBindingFromConfig, message);
        };
    }
}
