package com.marco.awesomepizza.pizzachef;

import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class PizzaChefWorker {

    private final OrderService orderService;

    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public PizzaChefWorker(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 1000)
    public void doPizza() {

        if (!isProcessing.compareAndSet(false, true)) {
            log.debug("Already processing an order, skipping this execution");
            return;
        }
        orderService.getOrderToProcessing()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException("No order to processing!"))))
                .mapNotNull(this::prepareOrder)
                .delayElement(Duration.ofSeconds(20))
                .mapNotNull(this::cooking)
                .delayElement(Duration.ofSeconds(10))
                .onErrorComplete(this::logOrderProcessingError)
                .subscribe(this::completeOrder);
    }

    private OrderEntity prepareOrder(OrderEntity orderEntity) {
        log.info("Preparing order {}", orderEntity.getId());


        return orderEntity;
    }

    private OrderEntity cooking(OrderEntity orderEntity) {
        log.info("Cooking order {}", orderEntity.getId());

        return orderEntity;
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