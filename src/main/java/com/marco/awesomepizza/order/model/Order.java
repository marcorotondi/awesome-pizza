package com.marco.awesomepizza.order.model;

import com.marco.awesomepizza.menu.model.Pizza;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Order(String orderCode,
                    List<Pizza> pizzas,
                    OrderStatus status,
                    BigDecimal cost,
                    LocalDateTime createAt) implements Serializable {
}