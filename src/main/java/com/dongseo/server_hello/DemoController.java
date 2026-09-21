package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final List<Demo> rooms = new ArrayList<>(List.of(
            new Demo(1L, "Seminar A", 8),
            new Demo(2L, "Study Pod", 4),
            new Demo(3L, "Hallway", 50)
    ));

    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    public List<Demo> all(@RequestParam(defaultValue = "0") int minCapacity, @RequestParam(defaultValue = "") String keyword) {
        List<Demo> temp = new ArrayList<>();
        if (!keyword.isEmpty())
            for (Demo room : rooms) {
                if (room.name().toLowerCase().contains(keyword.toLowerCase())) {
                    temp.add(room);
                }
            }
        else if (minCapacity > 0)
            for (Demo room : rooms) {
                if (room.size() >= minCapacity) {
                    temp.add(room);
                }
            }
        else return rooms;
        if (temp.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No rooms found matching query");
        }
        return temp;
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Demo getRoomByID(@PathVariable long id) {
        for (Demo room : rooms) {
            if (room.id().equals(id)) {
                return room;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
    }

    @GetMapping("/{id}")
    public String findOneRoom(@PathVariable Long id) {
        return rooms.stream()
                .filter(d -> d.id().equals(id))
                .findFirst().orElseThrow().name();
    }

    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String keyword) {
        return "keyword = " + keyword;
    }

    @PostMapping("/rooms")
    public ResponseEntity<Demo> create(@RequestBody DemoCreateRequest request) {
        Demo entry = new Demo((long) rooms.size(), request.name(), request.capacity());
        rooms.add(entry);
        return ResponseEntity.created(URI.create("/rooms/"+entry.id())).body(entry);
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Demo> replace(@RequestBody DemoReplaceRequest request) {
        Demo entry = new Demo(request.id(), request.name(), request.capacity());
        rooms.set(request.id().intValue(), entry);
        return ResponseEntity.created(URI.create("/rooms/"+entry.id())).body(entry);
    }
}
