package com.capbase.capbase.service;

import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Categoria;
import com.capbase.capbase.model.Movimiento;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.CategoriaRepository;
import com.capbase.capbase.repository.MovimientoRepository;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             UsuarioRepository usuarioRepository,
                             CategoriaRepository categoriaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<MovimientoDTO> obtenerTodos() {
        // Convierto la lista de movimientos a DTO para no devolver toda la entidad completa
        return movimientoRepository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    public Movimiento guardarMovimiento(Movimiento movimiento) {
        Long usuarioId = movimiento.getUsuario().getId();
        Long categoriaId = movimiento.getCategoria().getId();

        // Compruebo que el usuario existe antes de guardar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        // Compruebo que la categoria existe antes de guardar
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + categoriaId));

        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        return movimientoRepository.save(movimiento);
    }

    public void eliminarMovimiento(Long id) {
        // Compruebo si existe antes de borrar
        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento no encontrado con id: " + id);
        }

        movimientoRepository.deleteById(id);
    }

    public Movimiento actualizarMovimiento(Long id, Movimiento movimientoActualizado) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        Long usuarioId = movimientoActualizado.getUsuario().getId();
        Long categoriaId = movimientoActualizado.getCategoria().getId();

        // Compruebo que el usuario existe tambien al actualizar
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        // Compruebo que la categoria existe tambien al actualizar
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + categoriaId));

        movimiento.setConcepto(movimientoActualizado.getConcepto());
        movimiento.setDescripcion(movimientoActualizado.getDescripcion());
        movimiento.setCantidad(movimientoActualizado.getCantidad());
        movimiento.setFecha(movimientoActualizado.getFecha());
        movimiento.setTipo(movimientoActualizado.getTipo());
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        return movimientoRepository.save(movimiento);
    }

    private MovimientoDTO convertirDTO (Movimiento movimiento) {
        return new MovimientoDTO(
                movimiento.getId(),
                movimiento.getConcepto(),
                movimiento.getDescripcion(),
                movimiento.getCantidad(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                movimiento.getCategoria().getNombre(),
                movimiento.getUsuario().getNombre()
        );
    }
}