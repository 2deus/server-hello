package com.dongseo.server_hello.appt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dongseo.server_hello.AppUser.AppUser;
import com.dongseo.server_hello.AppUser.AppUserRepository;
import com.dongseo.server_hello.Exception.ConflictException;
import com.dongseo.server_hello.Exception.ForbiddenException;
import com.dongseo.server_hello.Exception.NotFoundException;
import com.dongseo.server_hello.demo.Demo;
import com.dongseo.server_hello.demo.DemoRepository;

// for AppointmentService. mockito is used for fake repos
class ReservationServiceTest {

    private DemoRepository        roomRepository;
    private AppointmentRepository reservationRepository;
    private AppUserRepository     userRepository;
    private AppointmentService    service;

    private final Demo          room   = new Demo(1L, "Seminar A", 8);
    private final AppUser       mina   = new AppUser("mina", "hash", "USER");
    private final LocalDateTime ten    = LocalDateTime.of(2026, 12, 1, 10, 0);
    private final LocalDateTime eleven = LocalDateTime.of(2026, 12, 1, 11, 0);
    private final LocalDateTime twelve = LocalDateTime.of(2026, 12, 1, 12, 0);

    @BeforeEach                                   // before each test setup fresh fakes
    void setUp() {
        roomRepository        = mock(DemoRepository.class);
        reservationRepository = mock(AppointmentRepository.class);
        userRepository        = mock(AppUserRepository.class);
        service               = new AppointmentService(roomRepository, reservationRepository, userRepository);
    }

    // ---------- step 3 ----------

    @Test
    void createRefusesEndBeforeStart() {                        // 400
        assertThrows(IllegalArgumentException.class,
                () -> service.create(1L, "mina", eleven, ten));
        verify(reservationRepository, never()).save(any());      // save nothing
    }

    @Test
    void createRefusesMissingRoom() {                          // 404
        when(userRepository.findByUsername("mina")).thenReturn(Optional.of(mina));
        when(roomRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.create(99L, "mina", ten, eleven));
        verify(reservationRepository, never()).save(any());
    }

    // ---------- step 4 ----------

    @Test
    void createRefusesOverlap() {                              // 409
        when(userRepository.findByUsername("mina")).thenReturn(Optional.of(mina));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.isOverlapping(eq(1L), any(), any())).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(1L, "mina", ten, eleven));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void deleteRefusesAnotherUser() {                          // 403, rule C
        Appointment minas = new Appointment(room, mina, ten, eleven);
        when(reservationRepository.findByIdWithDemo(1L)).thenReturn(Optional.of(minas));

        assertThrows(ForbiddenException.class, () -> service.delete(1L, "omar", false));
        verify(reservationRepository, never()).delete(any());
    }

    // ---------- lab H1 ----------

    @Test
    void createSavesWithTheLoggedInOwner() {
        when(userRepository.findByUsername("mina")).thenReturn(Optional.of(mina));
        when(roomRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(room));
        when(reservationRepository.isOverlapping(eq(1L), any(), any())).thenReturn(false);
        when(reservationRepository.save(any(Appointment.class))).thenAnswer(call -> call.getArgument(0));

        Appointment saved = service.create(1L, "mina", ten, eleven);

        assertSame(mina, saved.getOwner());
        assertSame(room, saved.getDemo());
        assertEquals(ten, saved.getStartTime());
        assertEquals(eleven, saved.getEndTime());
        verify(reservationRepository).save(saved);
    }

    @Test
    void ownerMayDelete() {
        Appointment minas = new Appointment(room, mina, ten, eleven);
        when(reservationRepository.findByIdWithDemo(1L)).thenReturn(Optional.of(minas));

        assertDoesNotThrow(() -> service.delete(1L, "mina", false));
        verify(reservationRepository).delete(minas);
    }

    @Test
    void adminMayDeleteAnotherUsersReservation() {
        Appointment minas = new Appointment(room, mina, ten, eleven);
        when(reservationRepository.findByIdWithDemo(1L)).thenReturn(Optional.of(minas));

        assertDoesNotThrow(() -> service.delete(1L, "admin", true));
        verify(reservationRepository).delete(minas);
    }

    @Test
    void adminMayNotReschedule() {                              // owner only
        Appointment minas = new Appointment(room, mina, ten, eleven);
        when(reservationRepository.findByIdWithDemo(1L)).thenReturn(Optional.of(minas));

        assertThrows(ForbiddenException.class, () -> service.reschedule(1L, "admin", eleven, twelve));
        assertEquals(ten, minas.getStartTime());                // booking does not move
        assertEquals(eleven, minas.getEndTime());
    }
}
