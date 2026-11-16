package com.dongseo.server_hello.appt;

import com.dongseo.server_hello.demo.Demo;
import com.dongseo.server_hello.Exception.ConflictException;
import com.dongseo.server_hello.Exception.NotFoundException;
import com.dongseo.server_hello.demo.DemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Appointment find(Long id) {
        return appointmentRepository.findByIdWithDemo(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) throw new NotFoundException("reservation " + id + " not found");
        appointmentRepository.deleteById(id);
    }

    @Transactional
    public Appointment create(Long demoId, String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {
        // lock room row so concurrent bookings and deletes of the same room run one at a time
        Demo demo = demoRepository.findByIdForUpdate(demoId)
                .orElseThrow(() -> new NotFoundException("room " + demoId + " not found"));

        if (appointmentRepository.isOverlapping(demoId, startTime, endTime)) {
            throw new ConflictException("room " + demoId +" is already booked for that time");
        }

        return appointmentRepository.save(new Appointment(demo, appointedBy, startTime, endTime));
    }
}
