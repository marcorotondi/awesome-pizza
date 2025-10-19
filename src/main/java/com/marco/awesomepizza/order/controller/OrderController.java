package com.marco.awesomepizza.order.controller;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.order.model.Order;
import com.marco.awesomepizza.order.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements Serializable {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Mono<Order> createOrder(@Valid @RequestBody @NotNull @NotEmpty List<Pizza> pizzas) {
        return orderService.createOrder(pizzas);
    }

    @GetMapping("/{orderCode}")
    public Mono<Order> getOrder(@PathVariable("orderCode") String orderCode) {
        return orderService.getOrder(orderCode);
    }
}