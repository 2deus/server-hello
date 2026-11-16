package com.dongseo.server_hello.appt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDemoIdOrderByStartTime(Long roomId);
    List<Appointment> findByAppointedByOrderByStartTime(String appointedBy);

    boolean existsByDemoId(Long roomId);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.demo.id = :demoId AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean isOverlapping(@Param("demoId") Long demoId,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    @Query("SELECT a FROM Appointment a JOIN FETCH a.demo WHERE a.appointedBy = :name ORDER BY a.startTime")
    List<Appointment> findByAppointedByWithDemo(@Param("name") String name);
}
