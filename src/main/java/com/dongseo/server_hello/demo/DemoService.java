package com.dongseo.server_hello.demo;

import com.dongseo.server_hello.Exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {
    private final DemoRepository demoRepository;
    public DemoService(DemoRepository demoRepository) { this.demoRepository = demoRepository; }

    public List<Demo> search(Integer minCapacity, String keyword) {
        List<Demo> result = demoRepository.findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword);

        if (result.isEmpty()) {throw new NotFoundException("No rooms found matching query");}
        return result;
    }

    public long count(int capacity) { return demoRepository.countByCapacityGreaterThanEqual(capacity); }

    public String getNameFromId(Long id) {
        return demoRepository.findById(id).map(Demo::getName)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    // Wrapper
    public Demo getDemoFromId(Long id) {
        return demoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    @Transactional
    public Demo createDemo(String name, Integer capacity) {
        Demo entry = new Demo(null, name, capacity);
        return demoRepository.save(entry);
    }

    @Transactional
    public Demo updateDemo(Long id, String name, int capacity) {
        Demo demo = demoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
        demo.update(name, capacity);
        return demo;
    }

    public void delete(Long id) {
        if (!demoRepository.existsById(id)) throw new NotFoundException("room " + id + " not found");
        demoRepository.deleteById(id);
    }
}
