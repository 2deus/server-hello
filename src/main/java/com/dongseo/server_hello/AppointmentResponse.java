package com.dongseo.server_hello;

import java.time.LocalDateTime;

public record AppointmentResponse(Long id, Long demoId, String demoName, String appointedBy, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDemo().getId(),
                a.getDemo().getName(),
                a.getAppointedBy(),
                a.getStartTime(),
                a.getEndTime()
        );
    }
}
