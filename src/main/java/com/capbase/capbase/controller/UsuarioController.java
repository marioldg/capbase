package com.capbase.capbase.controller;

import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        // Le pongo la fecha actual automaticamente al crear el usuario
        usuario.setFechaRegistro(LocalDate.now());
        return usuarioRepository.save(usuario);
    }
}