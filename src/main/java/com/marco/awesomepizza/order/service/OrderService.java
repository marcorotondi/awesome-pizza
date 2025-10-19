package com.marco.awesomepizza.order.service;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.repository.PizzaRepository;
import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.model.Order;
import com.marco.awesomepizza.order.repository.OrderRepository;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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

    public Mono<Order> createOrder(@NotNull @NotEmpty List<Pizza> pizzas) {
        var pizzaToOrders = pizzas.stream().map(Pizza::code).toList();
        var allPizzaEntity = pizzaRepository.findAllById(pizzaToOrders);
        var newOrder = OrderEntity.of(allPizzaEntity);

        orderRepository.save(newOrder);
        return Mono.just(Order.of(newOrder));
    }

    public Mono<Order> getOrder(String orderCode) {
        var optionalOrder = orderRepository.findByOrderCode(orderCode);

        return optionalOrder
                .map(orderEntity -> Mono.just(Order.of(orderEntity)))
                .orElse(Mono.empty());
    }
}