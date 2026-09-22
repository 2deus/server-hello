package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryRoomRepository {
    private final Map<Long, Demo> rooms = new ConcurrentHashMap<>(Map.of(
            1L, new Demo(1L, "Seminar A", 8),
            2L, new Demo(2L, "Study Pod", 4),
            3L, new Demo(3L, "Hallway", 20)
    ));
    public final AtomicLong nextId = new AtomicLong(rooms.size() + 1);

    public Collection<Demo> findAll() {
        return rooms.values();
    }

    public Optional <Demo> findById(Long id) {
        return Optional.ofNullable(rooms.get(id));
    }

    public boolean deleteById(Long id) {
        return rooms.remove(id) != null;
    }

    public Optional<Demo> save(Demo demo) {
        checkCapacity(demo.capacity());
        if (demo.id() == null) {
            Demo created = new Demo(nextId.getAndIncrement(), demo.name(), demo.capacity());
            rooms.put(created.id(), created);
            return Optional.of(created);
        } else {
            Demo previous = rooms.putIfAbsent(demo.id(), demo);
            return previous == null ? Optional.of(demo) : Optional.empty();
        }
    }

    // InMemoryRoomRepository
    public Optional<Demo> update(Demo demo) {
        checkCapacity(demo.capacity());
        Demo result = rooms.computeIfPresent(demo.id(), (id, existing) -> demo);
        return Optional.ofNullable(result);
    }

    public void checkCapacity(int capacity) {
        int minCapacity = 1;
        int maxCapacity = 20;
        if (capacity < minCapacity || capacity > maxCapacity)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity " + capacity + " out of bounds (must be between " + minCapacity + " and " + maxCapacity + ")");
    }
}
