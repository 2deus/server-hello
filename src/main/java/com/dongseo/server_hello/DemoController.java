package com.dongseo.server_hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/{id}")
    public String findOneDemo(@PathVariable Long id) {
        String result = demoService.getNameFromId(id);

        if (result == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found with id " + id);
        return result;
    }

    @GetMapping("/search")
    public Demo search(@RequestParam(defaultValue = "") String keyword) {
        return demoService.searchOne(keyword)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + keyword));
    }

    @GetMapping("/rooms")
    public Collection<Demo> all(@RequestParam(defaultValue = "0") int minCapacity, @RequestParam(defaultValue = "") String keyword) {
        Collection<Demo> result = demoService.searchAll(minCapacity, keyword);

        if (result.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No rooms found matching query");
        return result;
    }

    @GetMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Demo getDemoByID(@PathVariable Long id) {
        return demoService.getDemoFromId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Demo> create(@RequestBody DemoCreateRequest request) {
        Demo result = demoService.createDemo(request);
        return ResponseEntity.created(URI.create("/api/rooms/" + result.id())).body(result);
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<Demo> replace(@PathVariable Long id, @RequestBody DemoCreateRequest request) {
        return demoService.updateDemo(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Demo> delete(@PathVariable long id) {
        if (!demoService.deleteDemo(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No room found at " + id);
        return ResponseEntity.noContent().build();
    }
}
