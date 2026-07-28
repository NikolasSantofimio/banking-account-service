package com.bankingtest.accountservice.controller;

import com.bankingtest.accountservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @Value("${app.auth.username}")
    private String validUsername;

    @Value("${app.auth.password}")
    private String validPassword;

    public record LoginRequest(String username, String password) {}
    public record TokenResponse(String token) {}

    @PostMapping("/token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (validUsername.equals(request.username()) && validPassword.equals(request.password())) {
            String token = jwtUtil.generateToken(request.username());
            return ResponseEntity.ok(new TokenResponse(token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}