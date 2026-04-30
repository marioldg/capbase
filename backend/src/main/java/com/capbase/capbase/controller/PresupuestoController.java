package com.capbase.capbase.controller;

import com.capbase.capbase.dto.PresupuestoCrearDTO;
import com.capbase.capbase.dto.PresupuestoDTO;
import com.capbase.capbase.dto.PresupuestoEstadoDTO;
import com.capbase.capbase.service.PresupuestoService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    public PresupuestoController(PresupuestoService presupuestoService) {
        this.presupuestoService = presupuestoService;
    }

    @GetMapping
    public List<PresupuestoDTO> obtenerPresupuestos(Authentication authentication) {
        String email = authentication.getName();
        return presupuestoService.obtenerPresupuestos(email);
    }

    @PostMapping
    public PresupuestoDTO crearPresupuesto(
            Authentication authentication,
            @Valid @RequestBody PresupuestoCrearDTO presupuestoCrearDTO) {

        String email = authentication.getName();
        return presupuestoService.crearPresupuesto(presupuestoCrearDTO, email);
    }

    @DeleteMapping("/{id}")
    public void eliminarPresupuesto(
            Authentication authentication,
            @PathVariable Long id) {

        String email = authentication.getName();
        presupuestoService.eliminarPresupuesto(id, email);
    }

    @GetMapping("/estado")
    public List<PresupuestoEstadoDTO> obtenerEstadoPresupuestos(Authentication authentication) {
        String email = authentication.getName();
        return presupuestoService.obtenerEstadoPresupuestos(email);
    }
}