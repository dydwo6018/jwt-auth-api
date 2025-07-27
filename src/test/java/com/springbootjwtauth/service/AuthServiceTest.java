package com.springbootjwtauth.service;

import com.springbootjwtauth.dto.request.LoginRequest;
import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.dto.response.LoginResponse;
import com.springbootjwtauth.dto.response.SignupResponse;
import com.springbootjwtauth.exception.CustomException;
import com.springbootjwtauth.exception.ErrorCode;
import com.springbootjwtauth.jwt.JwtProvider;
import com.springbootjwtauth.model.Role;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest("testuser", "1234");

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRoles().get(0).getRole());
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 사용자명")
    void signup_fail_duplicate_username() {
        // given
        String username = "dupUser";
        authService.signup(new SignupRequest(username, "1234"));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.signup(new SignupRequest(username, "1234")));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공 - JWT 토큰 발급")
    void login_success() {
        // given
        String username = "loginuser";
        String rawPassword = "abcd1234";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.create(username, encodedPassword, Role.USER);
        userRepository.save(user);

        LoginRequest request = new LoginRequest(username, rawPassword);

        // when
        LoginResponse response = authService.login(request);

        // then
        assertNotNull(response.getToken()); //
        assertTrue(response.getToken().startsWith("ey")); //
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_user_not_found() {
        // given
        LoginRequest request = new LoginRequest("nouser", "pw");

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.login(request));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_password_mismatch() {
        // given
        authService.signup(new SignupRequest("pwuser", "correctpw"));

        LoginRequest request = new LoginRequest("pwuser", "wrongpw");

        // when & then
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.login(request));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }

    @Test
    @DisplayName("JWT 토큰 유효성 검증")
    void token_validation_success() {
        // given
        authService.signup(new SignupRequest("jwtuser", "1234"));
        LoginResponse response = authService.login(new LoginRequest("jwtuser", "1234"));

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateToken(response.getToken()));
    }


    @Test
    @DisplayName("관리자 권한 부여 성공 - 서비스 직접 호출")
    void grantAdmin_success() {
        // given
        String targetUsername = "normaluser";
        authService.signup(new SignupRequest(targetUsername, "1234"));

        // when
        User updated = authService.grantAdminRole(targetUsername);

        // then
        assertEquals("ADMIN", updated.getRole().name());
        assertEquals(targetUsername, updated.getUsername());
    }


}
