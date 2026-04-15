package com.capbase.capbase.service;

import com.capbase.capbase.dto.MovimientoCrearDTO;
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

    public MovimientoDTO guardarMovimiento(MovimientoCrearDTO dto) {
        // Busco el usuario por id
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUsuarioId()));

        // Busco la categoria por id
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + dto.getCategoriaId()));

        // Creo el movimiento con los datos que vienen del DTO
        Movimiento movimiento = new Movimiento();
        movimiento.setConcepto(dto.getConcepto());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        return convertirDTO(movimientoGuardado);
    }

    public void eliminarMovimiento(Long id) {
        // Compruebo si existe antes de borrar
        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento no encontrado con id: " + id);
        }

        movimientoRepository.deleteById(id);
    }

    public MovimientoDTO actualizarMovimiento(Long id, MovimientoCrearDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUsuarioId()));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + dto.getCategoriaId()));

        movimiento.setConcepto(dto.getConcepto());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        Movimiento movimientoActualizado = movimientoRepository.save(movimiento);

        return convertirDTO(movimientoActualizado);
    }

    private MovimientoDTO convertirDTO(Movimiento movimiento) {
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