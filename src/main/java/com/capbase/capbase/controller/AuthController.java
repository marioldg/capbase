package com.capbase.capbase.controller;

import com.capbase.capbase.dto.AuthResponseDTO;
import com.capbase.capbase.dto.LoginRequestDTO;
import com.capbase.capbase.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return authService.login(dto);
    }
}