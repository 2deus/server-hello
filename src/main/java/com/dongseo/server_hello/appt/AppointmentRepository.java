package com.dongseo.server_hello.appt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDemoIdOrderByStartTime(Long roomId);
    List<Appointment> findByOwnerIdOrderByStartTime(Long ownerId);

    boolean existsByDemoId(Long roomId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.demo.id = :demoId AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean isOverlapping(@Param("demoId") Long demoId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.demo.id = :demoId AND a.startTime < :endTime AND a.endTime > :startTime AND a.id <> :id")
    boolean isOverlappingExcept(@Param("demoId") Long demoId,
                                @Param("id") Long id,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.demo JOIN FETCH a.owner WHERE a.owner.id = :ownerId ORDER BY a.startTime")
    List<Appointment> findByOwnerIdWithDemo(@Param("ownerId") Long ownerId);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.demo JOIN FETCH a.owner WHERE a.id = :id")
    Optional<Appointment> findByIdWithDemo(@Param("id") Long id);
}
