package com.dongseo.server_hello.appt;

import com.dongseo.server_hello.AppUser.AppUser;
import com.dongseo.server_hello.AppUser.AppUserRepository;
import com.dongseo.server_hello.Exception.ForbiddenException;
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
    private final DemoRepository        demoRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppUserRepository     appUserRepository;

    public AppointmentService(DemoRepository demoRepository, AppointmentRepository appointmentRepository, AppUserRepository appUserRepository) {
        this.demoRepository        = demoRepository;
        this.appointmentRepository = appointmentRepository;
        this.appUserRepository     = appUserRepository;
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findByIdWithDemo(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    public List<Appointment> findByRoom(Long demoId) {
        if (!demoRepository.existsById(demoId))
            throw new NotFoundException("room " + demoId + " not found");
        return appointmentRepository.findByDemoIdOrderByStartTime(demoId);
    }

    public List<Appointment> findByOwnerId(String username) {
        AppUser owner = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user " + username + " not found"));

        List<Appointment> result = appointmentRepository.findByOwnerIdWithDemo(owner.getId());

        if (result.isEmpty())
            throw new NotFoundException("No reservations found for " + username);
        return result;
    }

    // 404 first (does it exist?), then 403 (is it yours?)
    public Appointment find(Long id, String username, boolean admin) {
        Appointment a = findById(id);

        if (!admin && !a.isOwnedBy(username))
            throw new ForbiddenException("reservation " + id + " belongs to another user");
        return a;
    }

    // 400: checked before anything is loaded
    private static void requireStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end))
            throw new IllegalArgumentException("startTime must be before endTime");
    }

    @Transactional
    public Appointment create(Long demoId, String username, LocalDateTime startTime, LocalDateTime endTime) {
        requireStartBeforeEnd(startTime, endTime);

        AppUser owner = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("user " + username + " not found"));

        // lock room row so concurrent bookings and deletes of the same room run one at a time
        Demo demo = demoRepository.findByIdForUpdate(demoId)
                .orElseThrow(() -> new NotFoundException("room " + demoId + " not found"));

        if (appointmentRepository.isOverlapping(demoId, startTime, endTime))
            throw new ConflictException("room " + demoId +" is already booked for that time");

        return appointmentRepository.save(new Appointment(demo, owner, startTime, endTime));
    }

    @Transactional
    public Appointment reschedule(Long id, String username, LocalDateTime start, LocalDateTime end) {
        requireStartBeforeEnd(start, end);

        Appointment a = findById(id);
        if (!a.isOwnedBy(username))
            throw new ForbiddenException("reservation " + id + " belongs to another user");

        if (appointmentRepository.isOverlappingExcept(a.getDemo().getId(), id, start, end))
            throw new ConflictException("room " + a.getDemo().getId() + " is already booked for that time");
        a.reschedule(start, end);
        return a;
    }

    @Transactional
    public void delete(Long id, String username, boolean admin) {
        appointmentRepository.delete(find(id, username, admin));
    }
}
