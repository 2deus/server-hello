package com.dongseo.server_hello.demo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DemoCreateRequest(
        @NotBlank(message = "`name` cannot be blank")
        @Size(max = 100, message = "`name` field must be at most 100 characters")
        String name,

        @Min(value = 1, message = "`capacity` field must be between 1 and 20")
        @Max(value = 20, message = "`capacity` field must be between 1 and 20")
        int capacity) {}