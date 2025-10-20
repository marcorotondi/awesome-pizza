package com.marco.awesomepizza.model;

import com.marco.awesomepizza.menu.entity.IngredientEntity;
import com.marco.awesomepizza.menu.entity.PizzaEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record Pizza(
        Long code,
        String name,
        BigDecimal price,
        List<String> ingredients)
        implements Serializable {

    public static Pizza of(PizzaEntity entity) {
        var ingredients = entity.getIngredients()
                .stream()
                .map(IngredientEntity::getName)
                .toList();
        return new Pizza(entity.getId(), entity.getName(), entity.getPrice(), ingredients);
    }
}