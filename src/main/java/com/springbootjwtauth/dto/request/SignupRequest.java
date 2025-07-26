package com.springbootjwtauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String role;
}
