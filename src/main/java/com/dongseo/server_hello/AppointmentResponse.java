package com.dongseo.server_hello;

import java.time.LocalDateTime;

public record AppointmentResponse(Long id, Long demoId, String demoName, String appointedBy, LocalDateTime startDateTime, LocalDateTime endDateTime) {
    public static AppointmentResponse from(Appointment r) {
        return new AppointmentResponse(
                r.getId(),
                r.getDemo().getId(),
                r.getDemo().getName(),
                r.getAppointedBy(),
                r.getStartTime(),
                r.getEndTime()
        );
    }
}
