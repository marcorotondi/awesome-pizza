
package com.marco.awesomepizza.pizzachef;

import com.marco.awesomepizza.menu.entity.IngredientEntity;
import com.marco.awesomepizza.menu.entity.PizzaEntity;
import com.marco.awesomepizza.order.entity.OrderEntity;
import com.marco.awesomepizza.order.model.OrderStatus;
import com.marco.awesomepizza.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
public class PizzaChefWorkerTest {

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private PizzaChefWorker pizzaChefWorker;

    @Test
    public void doPizza_ShouldNotProcessIfAlreadyProcessing() {
        // Arrange
        when(orderService.getOrderToProcessing())
                .thenReturn(Mono.just(createOrderEntity()))
                .thenReturn(Mono.error(new OrderNotFoundException("No order to processing!")));

        // Act & Assert
        pizzaChefWorker.doPizza();
        // Assuming that the method logs if already processing, no exception should be thrown.
    }

    @Test
    public void doPizza_ShouldPrepareOrders() {
        // Arrange
        OrderEntity orderEntity = createOrderEntity();
        when(orderService.getOrderToProcessing())
                .thenReturn(Mono.just(orderEntity));

        when(orderService.updateOrder(any(OrderEntity.class), any(OrderStatus.class)))
                .thenAnswer(invocation -> {
                    OrderEntity order = invocation.getArgument(0);
                    OrderStatus status = invocation.getArgument(1);

                    OrderEntity updatedOrder = new OrderEntity();
                    if (OrderStatus.IN_PREPARATION.equals(status)) {
                        updatedOrder.setStatus(OrderStatus.IN_PREPARATION);
                        // Attach pizzas and ingredients
                        for (PizzaEntity pizza : order.getPizzas()) {
                            IngredientEntity ingredient1 = new IngredientEntity();
                            ingredient1.setName("Ingredient 1");
                            pizza.getIngredients().add(ingredient1);
                        }
                        for (PizzaEntity pizza : order.getPizzas()) {
                            IngredientEntity ingredient2 = new IngredientEntity();
                            ingredient2.setName("Ingredient 2");
                            pizza.getIngredients().add(ingredient2);
                        }
                    }
                    return updatedOrder;
                });

        // Act
        pizzaChefWorker.doPizza();

        // Assert
        log.info("Preparing order {} status: {}", orderEntity.getId(), orderEntity.getStatus());
    }

    private OrderEntity createOrderEntity() {
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
        orderEntity.setCreateAt(LocalDateTime.now());
        orderEntity.setStatus(OrderStatus.RECEIVED);

        return orderEntity;
    }
}