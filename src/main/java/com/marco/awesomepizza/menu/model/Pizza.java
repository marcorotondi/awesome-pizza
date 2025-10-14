package com.marco.awesomepizza.menu.model;

import java.io.Serializable;
import java.util.List;

public record Pizza(
        String code,
        String name,
        List<Ingredient> ingredients)
        implements Serializable {
}