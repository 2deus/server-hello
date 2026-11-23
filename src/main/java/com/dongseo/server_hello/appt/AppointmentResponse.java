package com.dongseo.server_hello.appt;

import java.time.LocalDateTime;

public record AppointmentResponse(Long id, Long demoId, String demoName, String owner, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDemo().getId(),
                a.getDemo().getName(),
                a.getOwner().getUsername(),
                a.getStartTime(),
                a.getEndTime()
        );
    }
}
