package com.dongseo.server_hello.Service;

import com.dongseo.server_hello.Appointment;
import com.dongseo.server_hello.Demo;
import com.dongseo.server_hello.Exceptions.ConflictException;
import com.dongseo.server_hello.Repository.AppointmentRepository;
import com.dongseo.server_hello.Repository.DemoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    private final DemoRepository demoRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(DemoRepository demoRepository, AppointmentRepository appointmentRepository) {
        this.demoRepository = demoRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Optional<List<Appointment>> findByRoom(Long demoId) {
        if(!demoRepository.existsById(demoId)) return Optional.empty();
        return Optional.of(appointmentRepository.findByDemoIdOrderByStartTime(demoId));
    }

    public Optional<List<Appointment>> findByAppointedBy(String name) {
        return Optional.of(appointmentRepository.findByAppointedByOrderByStartTime(name));
    }

    public List<Appointment> findByReservedBy(String name) {
        return appointmentRepository.findByAppointedByWithDemo(name);
    }

    //TODO: move to AppointmentExceptionHandler
    private void checkRequest(String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {
        if (appointedBy == null || appointedBy.isBlank()) throw new IllegalArgumentException("appointedBy is required");
        if (startTime == null || endTime == null) throw new IllegalArgumentException("Both startTime and endTime are required");
        if (endTime.isBefore(startTime)) throw new IllegalArgumentException("startTime is before endTime");
    }

    @Transactional
    public Optional<Appointment> create(Long demoId, String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {
        checkRequest(appointedBy, startTime, endTime);
        Optional<Demo> demo = demoRepository.findById(demoId);
        if (demo.isEmpty()) return Optional.empty();

        if (appointmentRepository.isOverlapping(demoId, endTime, startTime)) {
            throw new ConflictException("Room is already booked for that time");
        }

        return Optional.of(appointmentRepository.save(new Appointment(demo.get(), appointedBy, startTime, endTime)));
    }
}
