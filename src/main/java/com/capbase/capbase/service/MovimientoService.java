package com.capbase.capbase.service;

import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Movimiento;
import com.capbase.capbase.repository.MovimientoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    public List<Movimiento> obtenerTodos() {
        return movimientoRepository.findAll();
    }

    public Movimiento guardarMovimiento(Movimiento movimiento) {
        // Guardo directamente el movimiento (luego mejoraremos validaciones)
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

        // Actualizo los campos principales
        movimiento.setConcepto(movimientoActualizado.getConcepto());
        movimiento.setDescripcion(movimientoActualizado.getDescripcion());
        movimiento.setCantidad(movimientoActualizado.getCantidad());
        movimiento.setFecha(movimientoActualizado.getFecha());
        movimiento.setTipo(movimientoActualizado.getTipo());
        movimiento.setCategoria(movimientoActualizado.getCategoria());
        movimiento.setUsuario(movimientoActualizado.getUsuario());

        return movimientoRepository.save(movimiento);
    }
}