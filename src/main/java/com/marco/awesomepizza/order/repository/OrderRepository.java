package com.marco.awesomepizza.order.repository;

import com.marco.awesomepizza.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query("""
                SELECT o from OrderEntity o
                    JOIN FETCH o.pizzas p
                    JOIN FETCH p.ingredients
                WHERE o.id = :orderId
            """)
    Optional<OrderEntity> findByOrderCode(@Param("orderId") Long orderId);

    @Query(value = """
                SELECT DISTINCT o
                FROM OrderEntity o
                WHERE o.status = 'RECEIVED'
                AND o.createAt = (
                    SELECT MIN(o1.createAt)
                    FROM OrderEntity o1
                    WHERE o1.status = 'RECEIVED'
                )
            """)
    Optional<OrderEntity> findOrderToProcess();
}