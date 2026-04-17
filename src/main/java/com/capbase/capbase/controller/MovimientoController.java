package com.capbase.capbase.controller;

import com.capbase.capbase.dto.MovimientoCrearDTO;
import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.dto.PageResponseDTO;
import com.capbase.capbase.dto.ResumenCategoriaDTO;
import com.capbase.capbase.dto.ResumenMensualDTO;
import com.capbase.capbase.dto.ResumenMovimientoDTO;
import com.capbase.capbase.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
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
    public PageResponseDTO<MovimientoDTO> obtenerMovimientos(
            Authentication authentication,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "desc") String orden,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size) {

        String email = authentication.getName();

        return movimientoService.obtenerMovimientosUsuarioLogueado(
                email,
                categoriaId,
                search,
                orden,
                page,
                size
        );
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
    public MovimientoDTO guardarMovimiento(
            Authentication authentication,
            @Valid @RequestBody MovimientoCrearDTO movimiento) {

        String email = authentication.getName();
        return movimientoService.guardarMovimiento(movimiento, email);
    }

    @PutMapping("/{id}")
    public MovimientoDTO actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody MovimientoCrearDTO movimiento) {
        return movimientoService.actualizarMovimiento(id, movimiento);
    }

    @DeleteMapping("/{id}")
    public void eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
    }
}