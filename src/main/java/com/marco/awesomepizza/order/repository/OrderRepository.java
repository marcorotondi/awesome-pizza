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
                WHERE o.orderCode = :orderCode
            """)
    Optional<OrderEntity> findByOrderCode(@Param("orderCode") String orderCode);
}