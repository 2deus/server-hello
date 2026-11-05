package com.dongseo.server_hello;

import java.time.LocalDateTime;

public record AppointmentCreateRequest(String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {}
