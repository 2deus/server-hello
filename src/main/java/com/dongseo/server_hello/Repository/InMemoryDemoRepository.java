package com.dongseo.server_hello.Repository;

import com.dongseo.server_hello.Demo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryDemoRepository {
    private final Map<Long, Demo> demos = new ConcurrentHashMap<>(Map.of(
            1L, new Demo(1L, "Seminar A", 8),
            2L, new Demo(2L, "Study Pod", 4),
            3L, new Demo(3L, "Hallway", 20)
    ));
    public final AtomicLong nextId = new AtomicLong(demos.size() + 1);

    public Collection<Demo> findAll() {
        return demos.values();
    }

    public Optional <Demo> findById(Long id) {
        return Optional.ofNullable(demos.get(id));
    }

    public boolean deleteById(Long id) {
        return demos.remove(id) != null;
    }

    public Optional<Demo> save(Demo demo) {
        if (demo.id() == null) { //if demo has no id, means it is new
            Demo created = new Demo(nextId.getAndIncrement(), demo.name(), demo.capacity());
            demos.put(created.id(), created);
            return Optional.of(created);
        } else {                //demo has id, means we're replacing
            Demo previous = demos.putIfAbsent(demo.id(), demo);
            return previous == null ? Optional.of(demo) : Optional.empty();
        }
    }

    public Optional<Demo> update(Demo demo) {
        Demo result = demos.computeIfPresent(demo.id(), (id, existing) -> demo);
        return Optional.ofNullable(result);
    }
}
