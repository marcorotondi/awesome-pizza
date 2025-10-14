package com.marco.awesomepizza.api;

import com.marco.awesomepizza.menu.controller.MenuController;
import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.service.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = {MenuController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@Import({ MenuService.class })
class MenuControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getPizzas() {
        webTestClient
                .get()
                .uri("/api/v1/menu/pizzas")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Pizza.class)
                .hasSize(9);
    }
}