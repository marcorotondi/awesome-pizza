package com.marco.awesomepizza.pizzachef;

import com.marco.awesomepizza.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PizzaChefWorker {

    private final OrderService orderService;

    public PizzaChefWorker(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 1000)
    public void doPizza() {
        orderService.getOrderToProcessing()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException("No order to processing!"))))
                .mapNotNull(orderEntity -> {
                    log.info("Processing order {}", orderEntity.getId());
                    return null;
                })
                .onErrorComplete(this::logOrderProcessingError)
                .subscribe();
    }

    private boolean logOrderProcessingError(Throwable error) {
        switch (error) {
            case OrderNotFoundException orderNotFoundException -> log.info("{}", orderNotFoundException.getMessage());
            default -> log.error("Error processing order {}", error.getMessage());
        }

        return true;
    }
}