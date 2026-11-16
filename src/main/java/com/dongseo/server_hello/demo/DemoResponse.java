package com.dongseo.server_hello.demo;

public record DemoResponse(Long id, String name, int capacity) {
    public static DemoResponse from(Demo d) {
        return new DemoResponse(
                d.getId(),
                d.getName(),
                d.getCapacity()
        );
    }
}