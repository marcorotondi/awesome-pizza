package com.marco.awesomepizza.order.service;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.repository.PizzaRepository;
import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.model.Order;
import com.marco.awesomepizza.order.repository.OrderRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final PizzaRepository pizzaRepository;

    public OrderService(OrderRepository orderRepository,
                        PizzaRepository pizzaRepository) {
        this.orderRepository = orderRepository;
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional
    public Mono<Order> createOrder(@NotNull @NotEmpty List<Pizza> pizzas) {
        return Mono.fromCallable(() -> {
                    var pizzaToOrders = pizzas.stream().map(Pizza::code).toList();
                    var allPizzaEntity = pizzaRepository.findAllById(pizzaToOrders);
                    // Validate all pizzas exist
                    if (allPizzaEntity.size() != pizzaToOrders.size()) {
                        throw new IllegalArgumentException(
                                "Some pizzas not found. Requested: " + pizzaToOrders.size() +
                                ", Found: " + allPizzaEntity.size());
                    }

                    var newOrder = OrderEntity.of(allPizzaEntity);
                    OrderEntity savedOrder = orderRepository.save(newOrder);
                    return Order.of(savedOrder);
                }).subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new RuntimeException("Failed to create order", e));
    }

    @Transactional
    public Mono<Order> getOrder(Long orderId) {
        return Mono.fromCallable(() ->
                        orderRepository.findByOrderCode(orderId)
                                .map(Order::of)
                                .orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> new RuntimeException("Failed to get order", e));
    }

    public Mono<OrderEntity> getOrderToProcessing() {
        return Mono.empty();
    }
}