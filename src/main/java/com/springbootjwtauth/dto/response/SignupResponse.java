package com.springbootjwtauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SignupResponse {

    private String username;
    private List<RoleDto> roles;

    @Getter
    @AllArgsConstructor
    public static class RoleDto {
        private String role;
    }
}
