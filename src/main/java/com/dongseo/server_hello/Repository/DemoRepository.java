package com.dongseo.server_hello.Repository;

import com.dongseo.server_hello.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemoRepository
    extends JpaRepository<Demo, Long> {
        List<Demo> findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(int capacity, String name);
        long countByCapacityGreaterThanEqual(int capacity);
}
