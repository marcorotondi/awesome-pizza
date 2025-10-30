package com.marco.awesomepizza.menu.controller;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.service.PizzaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final PizzaService pizzaService;

    public MenuController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }

    @GetMapping("/pizzas")
    public Flux<Pizza> getPizzas() {
        return pizzaService.getPizzas();
    }
}