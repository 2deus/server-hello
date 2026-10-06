package com.dongseo.server_hello;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Demo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    private String  name;
    private int     capacity;

    protected Demo() {} //For Hibernate; JPA requires a no-arg constructor, it instantiates entities via reflection

    public Demo(Long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Long     getId()         { return id; }
    public String   getName()       { return name; }
    public int      getCapacity()   { return capacity; }
}