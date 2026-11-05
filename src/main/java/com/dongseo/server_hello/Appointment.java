package com.dongseo.server_hello;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long            id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demo_id")
    private Demo            demo;

    private String          appointedBy;
    private LocalDateTime   startTime;
    private LocalDateTime   endTime;

    protected Appointment() {}

    public Appointment(Demo demo, String appointedBy, LocalDateTime startTime, LocalDateTime endTime) {
        this.demo = demo;
        this.appointedBy = appointedBy;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public Demo getDemo() {
        return demo;
    }

    public String getAppointedBy() {
        return appointedBy;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
