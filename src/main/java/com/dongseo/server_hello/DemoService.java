package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Optional;

@Service
public class DemoService {
    private final InMemoryDemoRepository demoRepository;
    public DemoService(InMemoryDemoRepository demoRepository) { this.demoRepository = demoRepository; }

    public Collection<Demo> searchAll(Integer minCapacity, String keyword) {
        return demoRepository.findAll().stream()
                .filter(r -> r.capacity() >= minCapacity)
                .filter(r -> r.name().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public Optional<Demo> searchOne(String keyword) {
        return demoRepository.findAll().stream()
                .filter(room -> room.name().toLowerCase().contains(keyword.toLowerCase()))
                .findFirst();
    }

    public String getNameFromId(Long id) {
        return demoRepository.findById(id).map(Demo::name).orElse(null);
    }

    // Wrapper
    public Optional<Demo> getDemoFromId(Long id) {
        return demoRepository.findById(id);
    }

    public Demo createDemo(DemoCreateRequest request) {
        checkCapacity(request.capacity());
        Demo entry = new Demo(null, request.name(), request.capacity());
        return demoRepository.save(entry).orElseThrow();
    }

    public Optional<Demo> updateDemo(Long id, DemoCreateRequest request) {
        checkCapacity(request.capacity());
        Demo entry = new Demo(id, request.name(), request.capacity());
        return demoRepository.update(entry);
    }

    // Wrapper
    public boolean deleteDemo(Long id) {
        return demoRepository.deleteById(id);
    }

    public void checkCapacity(int capacity) {
        int minCapacity = 1;
        int maxCapacity = 20;
        if (capacity < minCapacity || capacity > maxCapacity)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity " + capacity + " out of bounds (must be between " + minCapacity + " and " + maxCapacity + ")");
    }
}
