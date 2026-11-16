package com.dongseo.server_hello.Service;

import com.dongseo.server_hello.Appointment;
import com.dongseo.server_hello.Demo;
import com.dongseo.server_hello.Exception.ConflictException;
import com.dongseo.server_hello.Exception.NotFoundException;
import com.dongseo.server_hello.Repository.AppointmentRepository;
import com.dongseo.server_hello.Repository.DemoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final DemoRepository demoRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(DemoRepository demoRepository, AppointmentRepository appointmentRepository) {
        this.demoRepository = demoRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findByRoom(Long demoId) {
        if (!demoRepository.existsById(demoId)) throw new NotFoundException("room " + demoId + " not found");
        return appointmentRepository.findByDemoIdOrderByStartTime(demoId);
    }

    public List<Appointment> findByAppointedBy(String name) {
        List<Appointment> result = appointmentRepository.findByAppointedByWithDemo(name);

        if (result.isEmpty()) throw new NotFoundException("No reservations found for " + name);
        return result;
    }

    @Transactional
    public Appointment create(Long demoId, String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {
        Demo demo = demoRepository.findById(demoId)
                .orElseThrow(() -> new NotFoundException("room " + demoId + " not found"));

        if (appointmentRepository.isOverlapping(demoId, startTime, endTime)) {
            throw new ConflictException("Room is already booked for that time");
        }

        return appointmentRepository.save(new Appointment(demo, appointedBy, startTime, endTime));
    }
}
