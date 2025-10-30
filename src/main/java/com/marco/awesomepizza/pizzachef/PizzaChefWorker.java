package com.marco.awesomepizza.pizzachef;

import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.model.OrderStatus;
import com.marco.awesomepizza.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public void doPizza() {

        if (!isProcessing.compareAndSet(false, true)) {
            log.debug("Already processing an order, skipping this execution");
            return;
        }

        log.info("#######################################");
        log.info("###       Starting pizza chef       ###");
        log.info("#######################################");

        orderService.getOrderToProcessing()
                .switchIfEmpty(Mono.defer(() -> Mono.error(new OrderNotFoundException("No order to processing!"))))
                .mapNotNull(this::prepareOrder)
                .delayElement(Duration.ofSeconds(20))
                .mapNotNull(this::cooking)
                .delayElement(Duration.ofSeconds(10))
                .doOnError(this::logOrderProcessingError)
                .doFinally(_ -> isProcessing.set(false))
                .subscribe(this::completeOrder);
    }

    private OrderEntity prepareOrder(OrderEntity orderEntity) {
        orderService.updateOrder(orderEntity, OrderStatus.IN_PREPARATION);
        log.info("Preparing order {} status: {}", orderEntity.getId(), orderEntity.getStatus());

        orderEntity.getPizzas().forEach(pizza -> {
            log.info("Prepare pizza: {}", pizza.getName());
            pizza.getIngredients().forEach(ingredient -> {
                log.info("Take ingredient: {}", ingredient.getName());
            });
        });

        return orderEntity;
    }

    private OrderEntity cooking(OrderEntity orderEntity) {
        log.info("Cooking order {} status: {}", orderEntity.getId(), orderEntity.getStatus());

        return orderEntity;
    }

    private void completeOrder(OrderEntity orderEntity) {
        orderService.updateOrder(orderEntity, OrderStatus.READY);
        log.info("Order {} completed, status: {}", orderEntity.getId(), orderEntity.getStatus());
        log.info("\n\n");
    }

    private void logOrderProcessingError(Throwable error) {
        switch (error) {
            case OrderNotFoundException orderNotFoundException -> log.info("{}", orderNotFoundException.getMessage());
            default -> log.error("Error processing order {}", error.getMessage());
        }
    }
}