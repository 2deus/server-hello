package com.dongseo.server_hello.demo;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DemoRepository
    extends JpaRepository<Demo, Long> {
        List<Demo> findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(int capacity, String name);
        long countByCapacityGreaterThanEqual(int capacity);

        // any other transaction locking same room will wait until this one commits
        // Only works inside @Transactional !!
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT d FROM Demo d WHERE d.id = :id")
        Optional<Demo> findByIdForUpdate(@Param("id") Long id);
}
