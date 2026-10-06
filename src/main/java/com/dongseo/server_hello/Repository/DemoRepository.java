package com.dongseo.server_hello.Repository;

import com.dongseo.server_hello.Demo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoRepository
        extends JpaRepository<Demo, Long> {
}
