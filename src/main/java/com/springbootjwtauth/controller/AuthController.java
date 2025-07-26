package com.springbootjwtauth.controller;

import com.springbootjwtauth.dto.request.LoginRequest;
import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.dto.response.LoginResponse;
import com.springbootjwtauth.dto.response.SignupResponse;
import com.springbootjwtauth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(new SignupResponse("회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
