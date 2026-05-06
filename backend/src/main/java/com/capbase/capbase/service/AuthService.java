package com.capbase.capbase.service;

import com.capbase.capbase.dto.AuthResponseDTO;
import com.capbase.capbase.dto.LoginRequestDTO;
import com.capbase.capbase.dto.RegisterRequestDTO;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }

        String token = jwtService.generarToken(usuario.getEmail());

        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {

        if (usuarioRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        usuario.setFechaRegistro(LocalDate.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String token = jwtService.generarToken(usuarioGuardado.getEmail());

        return new AuthResponseDTO(token);
    }
}