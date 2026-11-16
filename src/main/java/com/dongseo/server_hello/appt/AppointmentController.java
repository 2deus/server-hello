package com.dongseo.server_hello.appt;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) { this.appointmentService = appointmentService; }

    @GetMapping("/rooms/{demoId}/reservations")
    public List<AppointmentResponse> listForRoom(@PathVariable Long demoId) {
        return appointmentService.findByRoom(demoId).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @GetMapping("/reservations")
    public List<AppointmentResponse> search(@RequestParam String appointedBy) {
        return appointmentService.findByAppointedBy(appointedBy).stream()
                .map(AppointmentResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public AppointmentResponse findAppointment(@PathVariable Long id) {
        return AppointmentResponse.from(appointmentService.find(id));
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        appointmentService.delete(id);
    }

    @PostMapping("/rooms/{demoId}/reservations")
    public ResponseEntity<AppointmentResponse> create(@PathVariable Long demoId, @Valid @RequestBody AppointmentCreateRequest req) {
        Appointment appt = appointmentService.create(demoId, req.appointedBy(), req.startTime(), req.endTime());
        return ResponseEntity
                .created(URI.create("/api/reservations/" + appt.getId()))
                .body(AppointmentResponse.from(appt));
    }
}
