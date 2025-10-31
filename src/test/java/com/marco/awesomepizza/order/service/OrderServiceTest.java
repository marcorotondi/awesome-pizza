package com.marco.awesomepizza.order.service;

import com.marco.awesomepizza.menu.entity.IngredientEntity;
import com.marco.awesomepizza.menu.entity.PizzaEntity;
import com.marco.awesomepizza.menu.model.Pizza;
import com.marco.awesomepizza.menu.service.PizzaService;
import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.model.Order;
import com.marco.awesomepizza.order.model.OrderStatus;
import com.marco.awesomepizza.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = OrderService.class)
class OrderServiceTest {

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private PizzaService pizzaService;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrder() {
        var createAt = LocalDateTime.of(2025, Month.OCTOBER, 28, 12, 30);
        Pizza pizza = new Pizza(1L, "Pizza Margherita", BigDecimal.valueOf(6.00), List.of("Pomodoro", "Mozzarella"));
        Order expected = new Order(1L, List.of(pizza), OrderStatus.RECEIVED, BigDecimal.valueOf(6.00), createAt);

        var pomodoro = new IngredientEntity();
        pomodoro.setId(1L);
        pomodoro.setName("Pomodoro");
        var mozzarella = new IngredientEntity();
        mozzarella.setId(2L);
        mozzarella.setName("Mozzarella");

        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setId(1L);
        pizzaEntity.setName("Pizza Margherita");
        pizzaEntity.setPrice(BigDecimal.valueOf(6.00));
        pizzaEntity.setIngredients(Set.of(pomodoro, mozzarella));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setPizzas(List.of(pizzaEntity));
        orderEntity.setTotalCost(BigDecimal.valueOf(6.00));
        orderEntity.setCreateAt(createAt);
        orderEntity.setStatus(OrderStatus.RECEIVED);

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenReturn(orderEntity);
        Mockito.when(pizzaService.findPizzaById(Mockito.anyList()))
                .thenReturn(List.of(pizzaEntity));

        var order = orderService.createOrder(List.of(pizza));

        StepVerifier.create(order)
                .expectNextMatches(o -> {
                    // Compare orders by their properties rather than exact equality
                    return o.orderId().equals(expected.orderId()) &&
                           o.status().equals(expected.status()) &&
                           o.cost().equals(expected.cost());
                })
                .verifyComplete();
    }

    @Test
    void getOrder() {
        var createAt = LocalDateTime.of(2025, Month.OCTOBER, 28, 12, 30);
        Pizza pizza = new Pizza(1L, "Pizza Margherita", BigDecimal.valueOf(6.00), List.of("Pomodoro", "Mozzarella"));
        Order expected = new Order(1L, List.of(pizza), OrderStatus.RECEIVED, BigDecimal.valueOf(6.00), createAt);

        var pomodoro = new IngredientEntity();
        pomodoro.setId(1L);
        pomodoro.setName("Pomodoro");
        var mozzarella = new IngredientEntity();
        mozzarella.setId(2L);
        mozzarella.setName("Mozzarella");

        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setId(1L);
        pizzaEntity.setName("Pizza Margherita");
        pizzaEntity.setPrice(BigDecimal.valueOf(6.00));
        pizzaEntity.setIngredients(Set.of(pomodoro, mozzarella));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setPizzas(List.of(pizzaEntity));
        orderEntity.setTotalCost(BigDecimal.valueOf(6.00));
        orderEntity.setCreateAt(createAt);
        orderEntity.setStatus(OrderStatus.RECEIVED);

        Mockito.when(orderRepository.findByOrderCode(1L))
                .thenReturn(Optional.of(orderEntity));

        var orders = orderService.getOrder(1L);

        StepVerifier.create(orders)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getOrderToProcessing() {
        var createAt = LocalDateTime.of(2025, Month.OCTOBER, 28, 12, 30);

        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setId(1L);
        pizzaEntity.setName("Pizza Margherita");
        pizzaEntity.setPrice(BigDecimal.valueOf(6.00));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setPizzas(List.of(pizzaEntity));
        orderEntity.setTotalCost(BigDecimal.valueOf(6.00));
        orderEntity.setCreateAt(createAt);
        orderEntity.setStatus(OrderStatus.RECEIVED);

        Mockito.when(orderRepository.findOrderToProcess())
                .thenReturn(Optional.of(orderEntity));

        var result = orderService.getOrderToProcessing();

        StepVerifier.create(result)
                .expectNext(orderEntity)
                .verifyComplete();
    }

    @Test
    void getOrderToProcessing_whenNoOrder() {
        Mockito.when(orderRepository.findOrderToProcess())
                .thenReturn(Optional.empty());

        var result = orderService.getOrderToProcessing();

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void updateOrder() {
        var createAt = LocalDateTime.of(2025, Month.OCTOBER, 28, 12, 30);

        PizzaEntity pizzaEntity = new PizzaEntity();
        pizzaEntity.setId(1L);
        pizzaEntity.setName("Pizza Margherita");
        pizzaEntity.setPrice(BigDecimal.valueOf(6.00));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setPizzas(List.of(pizzaEntity));
        orderEntity.setTotalCost(BigDecimal.valueOf(6.00));
        orderEntity.setCreateAt(createAt);
        orderEntity.setStatus(OrderStatus.RECEIVED);

        Mockito.when(orderRepository.save(Mockito.any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity savedEntity = invocation.getArgument(0);
                    savedEntity.setStatus(OrderStatus.IN_PREPARATION);
                    return savedEntity;
                });

        var result = orderService.updateOrder(orderEntity, OrderStatus.IN_PREPARATION);

        StepVerifier.create(result)
                .expectNextMatches(entity -> entity.getStatus() == OrderStatus.IN_PREPARATION)
                .verifyComplete();

        Mockito.verify(orderRepository, Mockito.times(1)).save(Mockito.any(OrderEntity.class));
    }
}