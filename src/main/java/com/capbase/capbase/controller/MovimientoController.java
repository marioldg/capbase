package com.capbase.capbase.controller;

import com.capbase.capbase.dto.MovimientoCrearDTO;
import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.dto.ResumenMovimientoDTO;
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
    public List<MovimientoDTO> obtenerMovimientos(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long categoriaId) {
        return movimientoService.obtenerTodos(usuarioId, categoriaId);
    }

    @GetMapping("/resumen")
    public ResumenMovimientoDTO obtenerResumen(@RequestParam Long usuarioId) {
        return movimientoService.obtenerResumenPorUsuario(usuarioId);
    }

    @PostMapping
    public MovimientoDTO guardarMovimiento(@Valid @RequestBody MovimientoCrearDTO movimiento) {
        // Aqui creo un nuevo movimiento
        return movimientoService.guardarMovimiento(movimiento);
    }

    @PutMapping("/{id}")
    public MovimientoDTO actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody MovimientoCrearDTO movimiento) {
        // Actualizo un movimiento existente
        return movimientoService.actualizarMovimiento(id, movimiento);
    }

    @DeleteMapping("/{id}")
    public void eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
    }
}