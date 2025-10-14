package com.marco.awesomepizza.menu.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(schema = "pizza", name = "menu")
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;


}