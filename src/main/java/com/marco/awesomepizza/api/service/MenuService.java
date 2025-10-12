package com.marco.awesomepizza.api.service;

import com.marco.awesomepizza.api.model.Ingredient;
import com.marco.awesomepizza.api.model.Pizza;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.List;

import static com.marco.awesomepizza.api.model.Ingredient.*;

@Service
public record MenuService() implements Serializable {

    public Flux<Pizza> getPizzas() {
        return Flux.just(
                buildPizza(1, "Marinara", POMODORO, ACCIUGHE),
                buildPizza(2, "Margherita", POMODORO, FORMAGGIO),
                buildPizza(3, "Funghi", POMODORO, FORMAGGIO, FUNGHI),
                buildPizza(4, "Tonno", POMODORO, FORMAGGIO, TONNO),
                buildPizza(5, "Prosciutto Cotto", POMODORO, FORMAGGIO, PROSCIUTTO_COTTO),
                buildPizza(6, "Prosciutto Crudo", POMODORO, FORMAGGIO, PROSCIUTTO_CRUDO),
                buildPizza(7, "Cipolle", POMODORO, FORMAGGIO, CIPOLLE),
                buildPizza(8, "Quattro Stagioni", POMODORO, FORMAGGIO,
                        OLIVE, CARCIOFI, PROSCIUTTO_COTTO, FUNGHI),
                buildPizza(9, "Diavola", POMODORO, FORMAGGIO, SALAME_PICCANTE)
        );
    }

    private Pizza buildPizza(final int code, final String name, Ingredient... ingredients) {
        return new Pizza(String.format("%03d", code), name, List.of(ingredients));
    }
}