package com.dongseo.server_hello.appt;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        @NotBlank(message = "`appointedBy` cannot be blank")
        @Size(max = 100, message = "`appointedBy` field must be at most 100 characters")
        String appointedBy,

        @NotNull
        LocalDateTime startTime,

        @NotNull
        LocalDateTime endTime) {

    @AssertTrue(message = "endTime must not be before startTime")
    public boolean isEndTimeValid() {
        return startTime == null || endTime == null || !endTime.isBefore(startTime);
    }
}
