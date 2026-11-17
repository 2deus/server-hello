package com.dongseo.server_hello.AppUser;

public record AppUserResponse(Long id, String username, String role) {
    public static AppUserResponse from(AppUser a) {
        return new AppUserResponse(
                a.getId(),
                a.getUsername(),
                a.getRole()
        );
    }
}
