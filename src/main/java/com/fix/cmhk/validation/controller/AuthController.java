package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.dto.AuthResponse;
import com.fix.cmhk.validation.dto.LoginRequest;
import com.fix.cmhk.validation.dto.RegisterRequest;
import com.fix.cmhk.validation.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/verification-code/register")
    public ResponseEntity<String> sendRegisterVerificationCode(@RequestParam String email) {
        log.info("Received register verification code request for email: {}", email);
        String result = authService.sendVerificationCode(email, "REGISTER");
        log.info("Register verification code sent successfully to email: {}", email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verification-code/login")
    public ResponseEntity<String> sendLoginVerificationCode(@RequestParam String email) {
        log.info("Received login verification code request for email: {}", email);
        String result = authService.sendVerificationCode(email, "LOGIN");
        log.info("Login verification code sent successfully to email: {}", email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verification-code/reset-password")
    public ResponseEntity<String> sendResetPasswordVerificationCode(@RequestParam String email) {
        log.info("Received reset password verification code request for email: {}", email);
        String result = authService.sendVerificationCode(email, "RESET_PASSWORD");
        log.info("Reset password verification code sent successfully to email: {}", email);
        return ResponseEntity.ok(result);
    }
} 