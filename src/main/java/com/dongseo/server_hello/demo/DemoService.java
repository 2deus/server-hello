package com.dongseo.server_hello.demo;

import com.dongseo.server_hello.Exception.ConflictException;
import com.dongseo.server_hello.Exception.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void delete(Long id) {
        // findByIdForUpdate makes sure a DELETE and a booking of one room can't mix
        Demo demo = demoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
        try {
            demoRepository.delete(demo);
            demoRepository.flush(); // run DELETE now so I can go to sleep
        }
        catch (DataIntegrityViolationException e) {
            throw new ConflictException("room " + id + " has reservations");
        }
    }
}
