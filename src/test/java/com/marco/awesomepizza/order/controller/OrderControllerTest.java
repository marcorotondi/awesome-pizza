package com.marco.awesomepizza.order.controller;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.order.model.Order;
import com.marco.awesomepizza.order.model.OrderStatus;
import com.marco.awesomepizza.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebFluxTest(controllers = {OrderController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class OrderControllerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createOrder() {
        var orderId = UUID.randomUUID().toString();

        List<Pizza> pizzas = List.of(new Pizza(1L, "Margherita", List.of("Pomodoro", "Formaggio")));
        var expectedOrder = new Order(
                orderId,
                pizzas,
                OrderStatus.RECEIVED,
                new BigDecimal("6.00"),
                LocalDateTime.of(2025, 10, 10, 10, 10, 0)
        );

        when(orderService.createOrder(pizzas)).thenReturn(Mono.just(expectedOrder));

        webTestClient.post()
                .uri("/api/v1/orders/create")
                .body(Mono.just(pizzas), new ParameterizedTypeReference<List<Pizza>>() {
                })
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);

    }

    @Test
    void order_without_pizza() {
        var orderId = UUID.randomUUID().toString();
        List<Pizza> pizzas = List.of(new Pizza(1L, "Margherita", List.of("Pomodoro", "Formaggio")));

        var expectedOrder = new Order(
                orderId,
                pizzas,
                OrderStatus.RECEIVED,
                new BigDecimal("6.00"),
                LocalDateTime.of(2025, 10, 10, 10, 10, 0)
        );

        webTestClient.post()
                .uri("/api/v1/orders/create")
                .body(Mono.just(List.of()), new ParameterizedTypeReference<List<Pizza>>() {
                })
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @Test
    void getOrder() {
        var orderId = UUID.randomUUID().toString();
        var expectedOrder = new Order(
                orderId,
                List.of(new Pizza(1L, "Margherita", List.of("Pomodoro", "Formaggio"))),
                OrderStatus.RECEIVED,
                new BigDecimal("6.00"),
                LocalDateTime.of(2025, 10, 10, 10, 10, 0)
        );

        when(orderService.getOrder(orderId)).thenReturn(Mono.just(expectedOrder));

        webTestClient.get()
                .uri("/api/v1/orders/" + orderId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Order.class)
                .isEqualTo(expectedOrder);

    }
}