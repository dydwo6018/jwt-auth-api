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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;


    public SignupResponse  signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.create(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.USER // 기본 권한 USER
        );

        userRepository.save(user);

        return new SignupResponse(
                user.getUsername(),
                List.of(new SignupResponse.RoleDto(user.getRole().name()))
        );
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String token = jwtProvider.createAccessToken(user);

        return new LoginResponse(token);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User grantAdminRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.setRole(Role.ADMIN);
        return userRepository.save(user);
    }

}
