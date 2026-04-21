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
    public ResumenMovimientoDTO obtenerResumen(Authentication authentication) {
        String email = authentication.getName();
        return movimientoService.obtenerResumenPorUsuarioLogueado(email);
    }

    @GetMapping("/resumen-categorias")
    public List<ResumenCategoriaDTO> obtenerResumenPorCategorias(
            Authentication authentication,
            @RequestParam String tipo,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {

        String email = authentication.getName();
        return movimientoService.obtenerResumenPorCategoriasUsuarioLogueado(email, tipo, mes, anio);
    }

    @GetMapping("/resumen-mensual")
    public List<ResumenMensualDTO> obtenerResumenMensual(
            Authentication authentication,
            @RequestParam Integer anio) {

        String email = authentication.getName();
        return movimientoService.obtenerResumenMensualUsuarioLogueado(email, anio);
    }

    @GetMapping("/top-categorias")
    public List<ResumenCategoriaDTO> obtenerTopCategorias(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "3") Integer limite) {

        String email = authentication.getName();
        return movimientoService.obtenerTopCategoriasUsuarioLogueado(email, limite);
    }

    @PostMapping
    public MovimientoDTO guardarMovimiento(
            Authentication authentication,
            @Valid @RequestBody MovimientoCrearDTO movimiento) {

        String email = authentication.getName();
        return movimientoService.guardarMovimiento(movimiento, email);
    }

    @PutMapping("/{id}")
    public MovimientoDTO actualizarMovimiento(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody MovimientoCrearDTO movimiento) {

        String email = authentication.getName();
        return movimientoService.actualizarMovimiento(id, movimiento, email);
    }
    @DeleteMapping("/{id}")
    public void eliminarMovimiento(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        movimientoService.eliminarMovimiento(id, email);
    }
}