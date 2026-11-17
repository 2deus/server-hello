package com.dongseo.server_hello.AppUser;

import jakarta.persistence.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String username;
    @Column(name = "password_hash")
    private String passwordHash;
    private String role;     // USER or ADMIN

    protected AppUser() {}

    public AppUser(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    public String getRole() {
        return role;
    }
}
