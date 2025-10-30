package com.marco.awesomepizza.menu.service;

import com.marco.awesomepizza.menu.entity.PizzaEntity;
import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.repository.PizzaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

@Service
public class PizzaService implements Serializable {

    private final PizzaRepository pizzaRepository;

    public PizzaService(PizzaRepository pizzaRepository) {
        this.pizzaRepository = pizzaRepository;
    }

    @Transactional(readOnly = true)
    public Flux<Pizza> getPizzas() {
        var pizzas = pizzaRepository.findAllPizzas();
        return Flux.fromIterable(pizzas)
                .mapNotNull(Pizza::of);
    }

    @Transactional
    public List<PizzaEntity> findPizzaById(List<Long> pizzaToOrders) {
        return pizzaRepository.findAllByIdWithIngredients(pizzaToOrders);
    }
}