package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("/api")
public class DemoController {

    private final Map<Long, Demo> rooms = new ConcurrentHashMap<>(Map.of(
            1L, new Demo(1L, "Seminar A", 8),
            2L, new Demo(2L, "Study Pod", 4),
            3L, new Demo(3L, "Hallway", 50)
    ));
    private final AtomicLong nextId = new AtomicLong(rooms.size() + 1);

    @GetMapping("/{id}")
    public String findOneRoom(@PathVariable Long id) {
        if (rooms.containsKey(id)) return rooms.get(id).name();
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
    }

    @GetMapping("/search")
    public Demo search(@RequestParam(defaultValue = "") String keyword) {
        for  (Demo room : rooms.values())
            if (room.name().toLowerCase().contains(keyword.toLowerCase())) return room;
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found matching keyword" + keyword);
    }

    @GetMapping("/rooms")
    @ResponseStatus(HttpStatus.OK)
    public List<Demo> all(@RequestParam(defaultValue = "0") int minCapacity, @RequestParam(defaultValue = "") String keyword) {
        List<Demo> temp = new ArrayList<>();
        if (!keyword.isEmpty())
            for (Demo room : rooms.values()) {
                if (room.name().toLowerCase().contains(keyword.toLowerCase()))
                    temp.add(room);
            }
        else if (minCapacity > 0)
            for (Demo room : rooms.values()) {
                if (room.size() >= minCapacity)
                    temp.add(room);
            }
        else return new ArrayList<>(rooms.values());
        if (temp.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No rooms found matching query");
        return temp;
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Demo getRoomByID(@PathVariable long id) {
        if (rooms.containsKey(id)) return rooms.get(id);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
    }

    @PostMapping("/rooms")
    public ResponseEntity<Demo> create(@RequestBody DemoCreateRequest request) {
        Demo entry = new Demo(nextId.getAndIncrement(), request.name(), request.capacity());
        rooms.put(entry.id(), entry);
        return ResponseEntity.created(URI.create("/api/rooms/" + entry.id())).body(entry);
    }

    @PostMapping("/rooms/{id}")
    public ResponseEntity<Demo> createAtId(@PathVariable Long id, @RequestBody DemoCreateRequest request) {
        Demo entry = new Demo(id, request.name(), request.capacity());
        if (rooms.putIfAbsent(id, entry) != null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already exists at " + id);
        return ResponseEntity.created(URI.create("/api/rooms/" + id)).body(entry);
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Demo> replace(@PathVariable long id, @RequestBody DemoReplaceRequest request) {
        Demo entry = new Demo(id, request.name(), request.capacity());
        if (rooms.replace(id, entry) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
        return ResponseEntity.ok(entry);
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Demo> delete(@PathVariable long id) {
        if (rooms.remove(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
        return ResponseEntity.noContent().build();
    }
}
