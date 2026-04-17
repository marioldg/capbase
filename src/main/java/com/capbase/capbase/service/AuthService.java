package com.capbase.capbase.service;

import com.capbase.capbase.dto.AuthResponseDTO;
import com.capbase.capbase.dto.LoginRequestDTO;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        // De momento comparamos la contraseña tal cual.
        // Más adelante la ciframos con BCrypt, que es como debe hacerse bien.
        if (!usuario.getPassword().equals(loginRequest.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }

        String token = jwtService.generarToken(usuario.getEmail());

        return new AuthResponseDTO(token);
    }
}