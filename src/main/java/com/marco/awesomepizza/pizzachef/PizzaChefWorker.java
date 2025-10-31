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
import java.util.Objects;
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
                .flatMap(this::prepareOrder)
                .delayElement(Duration.ofSeconds(20))
                .mapNotNull(this::cooking)
                .delayElement(Duration.ofSeconds(10))
                .flatMap(this::completeOrder)
                .doOnError(this::logOrderProcessingError)
                .doFinally(_ -> isProcessing.set(false))
                .subscribe();
    }

    private Mono<OrderEntity> prepareOrder(OrderEntity orderEntity) {
        return orderService.updateOrder(orderEntity, OrderStatus.IN_PREPARATION)
                .doOnNext(updatedOrder -> log.info("Preparing order {} status: {}", updatedOrder.getId(), updatedOrder.getStatus()))
                .doOnNext(updatedOrder -> updatedOrder.getPizzas().forEach(pizza -> {
                    log.info("Prepare pizza: {}", pizza.getName());
                    pizza.getIngredients().forEach(ingredient -> log.info("Take ingredient: {}", ingredient.getName()));
                }));
    }

    private OrderEntity cooking(OrderEntity orderEntity) {
        log.info("Cooking order {} status: {}", orderEntity.getId(), orderEntity.getStatus());
        return orderEntity;
    }

    private Mono<OrderEntity> completeOrder(OrderEntity orderEntity) {
        return orderService.updateOrder(orderEntity, OrderStatus.READY)
                .doOnNext(updatedOrder -> {
                    log.info("Order {} completed, status: {}", updatedOrder.getId(), updatedOrder.getStatus());
                    log.info("\n\n");
                });
    }

    private void logOrderProcessingError(Throwable error) {
        if (Objects.requireNonNull(error) instanceof OrderNotFoundException orderNotFoundException) {
            log.info("{}", orderNotFoundException.getMessage());
        } else {
            log.error("Error processing order {}", error.getMessage());
        }
    }
}