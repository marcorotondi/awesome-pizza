package com.marco.awesomepizza.order.entity;

import com.marco.awesomepizza.menu.entity.PizzaEntity;
import com.marco.awesomepizza.order.model.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(schema = "pizza", name = "ordini")
@Getter
@Setter
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_code")
    private String orderCode;

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @Column(name = "createAt")
    private LocalDateTime createAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(schema = "pizza", name = "order_pizzas",
            joinColumns = @JoinColumn(name = "order_entity_order_code"),
            inverseJoinColumns = @JoinColumn(name = "pizzas_id"))
    private List<PizzaEntity> pizzas = new ArrayList<>();

    public static OrderEntity of(@NotNull @NotEmpty List<PizzaEntity> pizzas) {
        var orderEntity = new OrderEntity();
        orderEntity.setOrderCode(UUID.randomUUID().toString());
        orderEntity.setTotalCost(pizzas.stream().map(PizzaEntity::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        orderEntity.setCreateAt(LocalDateTime.now());
        orderEntity.setStatus(OrderStatus.RECEIVED);
        orderEntity.getPizzas().addAll(pizzas);

        return orderEntity;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderEntity that = (OrderEntity) o;
        return getOrderCode() != null && Objects.equals(getOrderCode(), that.getOrderCode());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(orderCode);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderEntity.class.getSimpleName() + "[", "]")
                .add("orderCode='" + orderCode + "'")
                .add("createAt=" + createAt)
                .add("status=" + status)
                .toString();
    }
}