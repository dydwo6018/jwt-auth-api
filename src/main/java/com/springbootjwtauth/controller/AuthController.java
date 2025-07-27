package com.springbootjwtauth.controller;

import com.springbootjwtauth.dto.request.LoginRequest;
import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.dto.response.LoginResponse;
import com.springbootjwtauth.dto.response.SignupResponse;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입, 로그인, 사용자 관리 API")
public class AuthController {

    private final AuthService authService;

    // 회원가입
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

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "username, password를 통해 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // 전체 사용자 조회 API
    @GetMapping("/users")
    @Operation(summary = "전체 사용자 조회", description = "모든 사용자의 username과 role 목록을 조회합니다.")
    public ResponseEntity<List<SignupResponse>> getAllUsers() {
        List<User> users = authService.findAllUsers();
        List<SignupResponse> result = users.stream()
                .map(u -> new SignupResponse(
                        u.getUsername(),
                        List.of(new SignupResponse.RoleDto(u.getRole().name()))
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // 관리자 권한 부여 API
    @PatchMapping("/admin/users/{username}/roles")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "관리자 권한 부여", description = "username으로 해당 사용자에게 ADMIN 권한을 부여합니다.")
    public ResponseEntity<SignupResponse> grantAdmin(@PathVariable String username) {
        User updated = authService.grantAdminRole(username);
        SignupResponse response = new SignupResponse(
                updated.getUsername(),
                List.of(new SignupResponse.RoleDto(updated.getRole().name()))
        );
        return ResponseEntity.ok(response);
    }
}
