package com.marco.awesomepizza.pizzachef;

import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

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
                .mapNotNull(this::preparePizza)
                .delayElement(Duration.ofMinutes(1))
                .mapNotNull(this::cooking)
                .delayElement(Duration.ofMinutes(1))
                .onErrorComplete(this::logOrderProcessingError)
                .doOnNext(this::completeOrder)
                .subscribe();
    }

    private OrderEntity preparePizza(OrderEntity orderEntity) {
        log.info("Preparing order {}", orderEntity.getId());


        return null;
    }

    private OrderEntity cooking(OrderEntity orderEntity) {
        log.info("Cooking order {}", orderEntity.getId());

        return null;
    }

    private void completeOrder(OrderEntity orderEntity) {
        log.info("Order {} completed", orderEntity.getId());
    }

    private boolean logOrderProcessingError(Throwable error) {
        switch (error) {
            case OrderNotFoundException orderNotFoundException -> log.info("{}", orderNotFoundException.getMessage());
            default -> log.error("Error processing order {}", error.getMessage());
        }

        return true;
    }
}