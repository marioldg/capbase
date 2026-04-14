package com.capbase.capbase.controller;

import com.capbase.capbase.model.Movimiento;
import com.capbase.capbase.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public List<Movimiento> obtenerMovimientos() {
        return movimientoService.obtenerTodos();
    }

    @PostMapping
    public Movimiento guardarMovimiento(@Valid @RequestBody Movimiento movimiento) {
        // Aqui creo un nuevo movimiento
        return movimientoService.guardarMovimiento(movimiento);
    }

    @PutMapping("/{id}")
    public Movimiento actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody Movimiento movimiento) {
        // Actualizo un movimiento existente
        return movimientoService.actualizarMovimiento(id, movimiento);
    }

    @DeleteMapping("/{id}")
    public void eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
    }
}