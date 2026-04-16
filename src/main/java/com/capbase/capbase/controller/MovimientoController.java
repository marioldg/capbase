package com.capbase.capbase.controller;

import com.capbase.capbase.dto.MovimientoCrearDTO;
import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.dto.ResumenCategoriaDTO;
import com.capbase.capbase.dto.ResumenMensualDTO;
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
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String search) {
        return movimientoService.obtenerTodos(usuarioId, categoriaId, search);
    }

    @GetMapping("/resumen")
    public ResumenMovimientoDTO obtenerResumen(@RequestParam Long usuarioId) {
        return movimientoService.obtenerResumenPorUsuario(usuarioId);
    }

    @GetMapping("/resumen-categorias")
    public List<ResumenCategoriaDTO> obtenerResumenPorCategorias(
            @RequestParam Long usuarioId,
            @RequestParam String tipo,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        return movimientoService.obtenerResumenPorCategorias(usuarioId, tipo, mes, anio);
    }

    @GetMapping("/resumen-mensual")
    public List<ResumenMensualDTO> obtenerResumenMensual(
            @RequestParam Long usuarioId,
            @RequestParam Integer anio) {
        return movimientoService.obtenerResumenMensual(usuarioId, anio);
    }

    @GetMapping("/top-categorias")
    public List<ResumenCategoriaDTO> obtenerTopCategorias(
            @RequestParam Long usuarioId,
            @RequestParam(required = false, defaultValue = "3") Integer limite) {
        return movimientoService.obtenerTopCategorias(usuarioId, limite);
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