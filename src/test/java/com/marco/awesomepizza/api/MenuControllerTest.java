package com.marco.awesomepizza.api;

import com.marco.awesomepizza.menu.controller.MenuController;
import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.service.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebFluxTest(controllers = {MenuController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class MenuControllerTest {

    @MockitoBean
    private MenuService menuService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getPizzas() {
        var margherita = new Pizza(1L, "Margherita", BigDecimal.valueOf(6.00), List.of("Formaggio", "Pomodoro", "Basilico"));

        when(menuService.getPizzas()).thenReturn(Flux.just(margherita));

        webTestClient
                .get()
                .uri("/api/v1/menu/pizzas")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Pizza.class)
                .hasSize(1);
    }
}