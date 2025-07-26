package com.springbootjwtauth.service;

import com.springbootjwtauth.dto.request.LoginRequest;
import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.dto.response.LoginResponse;
import com.springbootjwtauth.jwt.JwtProvider;
import com.springbootjwtauth.model.Role;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;


    public void signup(SignupRequest request) {
        // 유저 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        // 유저 생성
        User user = User.create(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER // 기본 권한은 USER로 고정
        );

        // 저장
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        // AuthenticationManager로 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 인증 성공 시 SecurityContext에 등록 (필수는 아님)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserDetails에서 username 가져오기
        String username = authentication.getName();

        // DB에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // JWT 토큰 생성
        String accessToken = jwtProvider.createAccessToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

}
