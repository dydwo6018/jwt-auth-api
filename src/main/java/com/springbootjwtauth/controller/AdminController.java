package com.springbootjwtauth.controller;

import com.springbootjwtauth.model.Role;
import com.springbootjwtauth.model.User;
import com.springbootjwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    // 관리자만 모든 사용자 목록 조회 가능
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{userId}/roles")
    public ResponseEntity<String> assignAdminRole(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + userId);
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        System.out.println("Role updated to ADMIN for userId: " + userId);

        return ResponseEntity.ok("관리자 권한이 부여되었습니다.");
    }
}
