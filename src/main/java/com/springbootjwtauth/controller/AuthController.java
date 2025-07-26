package com.springbootjwtauth.controller;

import com.springbootjwtauth.dto.request.LoginRequest;
import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.dto.response.LoginResponse;
import com.springbootjwtauth.dto.response.SignupResponse;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입 및 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "username, password로 회원가입을 수행합니다.")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest request) {
        User savedUser = authService.signup(request);

        SignupResponse response = new SignupResponse(
                savedUser.getUsername(),
                List.of(new SignupResponse.RoleDto(savedUser.getRole().name()))
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "username, password를 통해 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
