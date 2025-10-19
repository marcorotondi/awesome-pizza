package com.marco.awesomepizza.order.service;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.order.model.Order;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrderService {


    public Mono<Order> createOrder(@NotNull @NotEmpty List<Pizza> requestOrder) {
        return null;
    }

    public Mono<Order> getOrder(String orderCode) {
        return null;
    }
}