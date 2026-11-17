package com.dongseo.server_hello.AppUser;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {}
