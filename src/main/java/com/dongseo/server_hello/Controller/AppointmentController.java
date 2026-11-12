package com.dongseo.server_hello.Controller;

import com.dongseo.server_hello.AppointmentCreateRequest;
import com.dongseo.server_hello.AppointmentResponse;
import com.dongseo.server_hello.Exception.ConflictException;
import com.dongseo.server_hello.Service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public ResponseEntity<List<AppointmentResponse>> listForRoom(@PathVariable Long demoId) {
        return appointmentService.findByRoom(demoId)                    //gives Optional<List<Appointment>>
                .map(                                                   //Optional is not empty
                    list -> ResponseEntity.ok(             //processes 'list' by item
                        list.stream().map(AppointmentResponse::from)        //converts each Appointment to AppointmentResponse
                            .toList()                                       //returns List<AppointmentResponse>
                    )                                                       //returns ResponseEntity<List<AppointmentResponse>>
                )
                .orElseGet(                                             //Optional is empty
                        () -> ResponseEntity.notFound().build()             //returns ResponseEntity
                );
    }

    @PostMapping("/rooms/{demoId}/reservations")
    public ResponseEntity<AppointmentResponse> create(@PathVariable Long demoId, @RequestBody AppointmentCreateRequest request) {
        //TODO: move to AppointmentExceptionHandler and remove try/catch
        try {
            return appointmentService.create(demoId, request.appointedBy(), request.startTime(), request.endTime())
                    .map(a -> ResponseEntity
                            .created(URI.create("/api/reservations/" + a.getId()))
                            .body(AppointmentResponse.from(a)))
                    .orElseGet(
                            () -> ResponseEntity.notFound().build());
        } catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch(ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/rooms/{demoId}/reservations")
    public ResponseEntity<List<AppointmentResponse>> search(@RequestParam String appointedBy) {
        return appointmentService.findUsingAppointedBy(appointedBy)
                .map(
                        list -> ResponseEntity.ok(
                            list.stream().map(AppointmentResponse::from)
                                    .toList()
                        )
                )
                .orElseGet(
                        () -> ResponseEntity.notFound().build()
                );
    }
}
