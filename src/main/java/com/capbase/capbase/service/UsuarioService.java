package com.capbase.capbase.service;

import com.capbase.capbase.dto.UsuarioCrearDTO;
import com.capbase.capbase.dto.UsuarioDTO;
import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO guardarUsuario(UsuarioCrearDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setFechaRegistro(LocalDate.now());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return convertirDTO(usuarioGuardado);
    }

    public UsuarioDTO actualizarUsuario(Long id, UsuarioCrearDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return convertirDTO(usuarioActualizado);
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id: " + id);
        }

        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO convertirDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getFechaRegistro()
        );
    }
}