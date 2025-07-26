package com.springbootjwtauth.service;

import com.springbootjwtauth.dto.request.SignupRequest;
import com.springbootjwtauth.model.Role;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
