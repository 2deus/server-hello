package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final InMemoryRoomRepository roomRepository;

    public DemoController(InMemoryRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping("/{id}")
    public String findOneRoom(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(Demo::name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id));
    }

    @GetMapping("/search")
    public Demo search(@RequestParam(defaultValue = "") String keyword) {
        return roomRepository.findAll().stream()
                .filter(room -> room.name().toLowerCase().contains(keyword.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found matching keyword " + keyword));
    }

    @GetMapping("/rooms")
    public List<Demo> all(@RequestParam(defaultValue = "0") int minCapacity, @RequestParam(defaultValue = "") String keyword) {
        List<Demo> result = roomRepository.findAll().stream()
                .filter(r -> r.capacity() >= minCapacity)
                .filter(r -> r.name().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        if (result.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No rooms found matching query");
        return result;
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Demo getRoomByID(@PathVariable Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Demo> create(@RequestBody DemoCreateRequest request) {
        Demo entry = new Demo(null, request.name(), request.capacity());
        Demo created = roomRepository.save(entry).orElseThrow();
        return ResponseEntity.created(URI.create("/api/rooms/" + created.id())).body(created);
    }

    @PostMapping("/rooms/{id}")
    public ResponseEntity<Demo> createAtId(@PathVariable Long id, @RequestBody DemoCreateRequest request) {
        Demo entry = new Demo(id, request.name(), request.capacity());
        return roomRepository.save(entry)
                .map(created -> ResponseEntity.created(URI.create("/api/rooms/" + id)).body(created))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Room already exists at " + id));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Demo> replace(@PathVariable Long id, @RequestBody DemoReplaceRequest request) {
        Demo entry = new Demo(id, request.name(), request.capacity());
        return roomRepository.update(entry)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Demo> delete(@PathVariable long id) {
        if (!roomRepository.deleteById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
        return ResponseEntity.noContent().build();
    }
}
