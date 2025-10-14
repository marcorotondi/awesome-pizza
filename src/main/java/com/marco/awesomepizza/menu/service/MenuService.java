package com.marco.awesomepizza.menu.service;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.repository.PizzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.Serializable;

@Service
public class MenuService implements Serializable {

    private final PizzaRepository pizzaRepository;

    public MenuService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional
    public Flux<Pizza> getPizzas() {
        var pizzas = pizzaRepository.loadPizzas();
        return Flux.fromIterable(pizzas)
                .mapNotNull(Pizza::of);
    }
}