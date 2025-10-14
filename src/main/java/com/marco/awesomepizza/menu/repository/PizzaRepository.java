package com.marco.awesomepizza.menu.repository;

import com.marco.awesomepizza.menu.entity.PizzaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<PizzaEntity, Long> {

    @Query("""
                SELECT p from PizzaEntity p JOIN FETCH p.ingredients
            """)
    List<PizzaEntity> loadPizzas();
}