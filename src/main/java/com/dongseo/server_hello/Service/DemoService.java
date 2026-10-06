package com.dongseo.server_hello.Service;

import com.dongseo.server_hello.Demo;
import com.dongseo.server_hello.Repository.DemoRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class DemoService {
    private final DemoRepository demoRepository;
    public DemoService(DemoRepository demoRepository) { this.demoRepository = demoRepository; }

    public Collection<Demo> search(Integer minCapacity, String keyword) {
        return demoRepository.findAll().stream()
                .filter(d -> d.getCapacity() >= minCapacity)
                .filter(d -> d.getName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    public Optional<Demo> searchOne(String keyword) {
        return demoRepository.findAll().stream()
                .filter(d -> d.getName().toLowerCase().contains(keyword.toLowerCase()))
                .findFirst();
    }

    public String getNameFromId(Long id) {
        return demoRepository.findById(id).map(Demo::getName).orElse(null);
    }

    // Wrapper
    public Optional<Demo> getDemoFromId(Long id) {
        return demoRepository.findById(id);
    }

    public Demo createDemo(String name, Integer capacity) {
        Demo entry = new Demo(null, name, capacity);
        return demoRepository.save(entry);
    }

    public Optional<Demo> updateDemo(Long id, String name, Integer capacity) {
        if (!demoRepository.existsById(id)) return Optional.empty();
        Demo entry = new Demo(id, name, capacity);
        return Optional.of(demoRepository.save(entry));
    }

    // Wrapper
    public boolean delete(Long id) {
        if (!demoRepository.existsById(id)) return false;
        demoRepository.deleteById(id);
        return true;
    }
}
