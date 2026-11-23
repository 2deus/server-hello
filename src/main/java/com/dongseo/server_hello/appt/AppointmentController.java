package com.dongseo.server_hello.appt;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) { this.appointmentService = appointmentService; }

    private static boolean isAdmin(Authentication a) {
        return a.getAuthorities().stream()
                .anyMatch(g -> Objects.equals(g.getAuthority(), "ROLE_ADMIN"));
    }

    @GetMapping("/rooms/{demoId}/reservations")
    public List<AppointmentResponse> listForRoom(@PathVariable Long demoId) {
        return appointmentService.findByRoom(demoId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @GetMapping("/reservations")
    public List<AppointmentResponse> search(@RequestParam String owner) {
        return appointmentService.findByOwnerId(owner).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @GetMapping("/reservations/mine")
    public List<AppointmentResponse> mine(Authentication auth) {
        return appointmentService.findByOwnerId(auth.getName()).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public AppointmentResponse findAppointment(@PathVariable Long id, Authentication auth) {
        return AppointmentResponse.from(appointmentService.find(id, auth.getName(), isAdmin(auth)));
    }

    @PostMapping("/rooms/{demoId}/reservations")
    public ResponseEntity<AppointmentResponse> create(@PathVariable Long demoId, @Valid @RequestBody AppointmentCreateRequest req, Authentication auth) {
        Appointment a = appointmentService.create(demoId, auth.getName(), req.startTime(), req.endTime());
        return ResponseEntity
                .created(URI.create("/api/reservations/" + a.getId()))
                .body(AppointmentResponse.from(a));
    }

    @PutMapping("/reservations/{id}")
    public AppointmentResponse reschedule(@PathVariable Long id, @Valid @RequestBody AppointmentCreateRequest req, Authentication auth) {
        return AppointmentResponse.from(appointmentService.reschedule(id, auth.getName(), req.startTime(), req.endTime()));
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) {
        appointmentService.delete(id, auth.getName(), isAdmin(auth));
    }
}
