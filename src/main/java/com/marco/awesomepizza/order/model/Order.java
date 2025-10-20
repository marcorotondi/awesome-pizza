package com.marco.awesomepizza.order.model;

import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.order.entity.OrderEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Order(Long orderId,
                    List<Pizza> pizzas,
                    OrderStatus status,
                    BigDecimal cost,
                    LocalDateTime createAt) implements Serializable {

    public static Order of(OrderEntity entity) {
        return new Order(entity.getId(),
                entity.getPizzas().stream().map(Pizza::of).toList(),
                entity.getStatus(),
                entity.getTotalCost(),
                entity.getCreateAt()
        );
    }
}