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

    @Query("""
                SELECT o FROM OrderEntity o
                        JOIN FETCH o.pizzas p
                        JOIN FETCH p.ingredients
                WHERE o.id in (
                    SELECT pr.id
                    FROM (
                        SELECT o1.id as id,
                        dense_rank() over (
                                order by o1.createAt ASC
                        ) as ranking
                        FROM OrderEntity o1
                        WHERE o1.status = 'RECEIVED'
                    ) pr
                    WHERE pr.ranking = 1
                )
            """)
    Optional<OrderEntity> findOrderToProcess();
}