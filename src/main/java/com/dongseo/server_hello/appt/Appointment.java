package com.dongseo.server_hello.appt;

import com.dongseo.server_hello.AppUser.AppUser;
import com.dongseo.server_hello.demo.Demo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long            id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "demo_id")
    private Demo demo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    private LocalDateTime   startTime;
    private LocalDateTime   endTime;

    protected Appointment() {}

    public Appointment(Demo demo, AppUser owner, LocalDateTime startTime, LocalDateTime endTime) {
        this.demo = demo;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isOwnedBy(String username) {
        return owner.getUsername().equals(username);
    }

    public Long getId() {
        return id;
    }

    public Demo getDemo() {
        return demo;
    }

    public AppUser getOwner() { return owner; }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
