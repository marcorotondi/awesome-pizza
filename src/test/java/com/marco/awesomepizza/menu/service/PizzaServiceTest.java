package com.marco.awesomepizza.menu.service;

import com.marco.awesomepizza.menu.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

@ExtendWith({SpringExtension.class})
@DataJpaTest
class PizzaServiceTest {

    @Autowired
    private PizzaRepository pizzaRepository;

    private PizzaService pizzaService;

    @BeforeEach
    void setUp() {
        pizzaService = new PizzaService(pizzaRepository);
    }

    @Test
    void getPizzas() {

        var pizzas = pizzaService.getPizzas();

        StepVerifier.create(pizzas)
                .expectNextCount(14)
                .verifyComplete();
    }
}