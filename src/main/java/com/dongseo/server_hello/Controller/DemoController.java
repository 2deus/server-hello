package com.dongseo.server_hello.Controller;

import com.dongseo.server_hello.Demo;
import com.dongseo.server_hello.DemoCreateRequest;
import com.dongseo.server_hello.DemoResponse;
import com.dongseo.server_hello.Service.DemoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public long countRooms(@RequestParam(defaultValue = "0") int minCapacity) {
        return demoService.count(minCapacity);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String findOneDemo(@PathVariable Long id) {
        return demoService.getNameFromId(id);
    }

    @GetMapping("/rooms")
    public List<DemoResponse> all(@RequestParam(defaultValue = "0") int minCapacity, @RequestParam(defaultValue = "") String keyword) {
        return demoService.search(minCapacity, keyword).stream()
                .map(DemoResponse::from)
                .toList();
    }

    @GetMapping("/rooms/{id}")
    public DemoResponse getDemoById(@PathVariable Long id) {
        return DemoResponse.from(demoService.getDemoFromId(id));
    }

    @PostMapping("/rooms")
    public ResponseEntity<DemoResponse> create(@Valid @RequestBody DemoCreateRequest req) {
        Demo demo = demoService.createDemo(req.name(), req.capacity());
        //return ResponseEntity.status(HttpStatus.CREATED).body(DemoResponse.from(demo));
        return ResponseEntity
                        .created(URI.create("/api/rooms/" + demo.getId())) //for location header
                        .body(DemoResponse.from(demo));
    }

    @PutMapping("/rooms/{id}")
    public DemoResponse replace(@PathVariable Long id, @Valid @RequestBody DemoCreateRequest request) {
        return DemoResponse.from(
                        demoService.updateDemo(id, request.name(), request.capacity())
        );
    }

    @DeleteMapping("/rooms/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        demoService.delete(id);
    }
}
