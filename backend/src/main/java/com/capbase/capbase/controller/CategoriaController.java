package com.capbase.capbase.controller;

import com.capbase.capbase.dto.CategoriaCrearDTO;
import com.capbase.capbase.dto.CategoriaDTO;
import com.capbase.capbase.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaDTO> obtenerCategorias() {
        return categoriaService.obtenerTodas();
    }

    @PostMapping
    public CategoriaDTO guardarCategoria(@Valid @RequestBody CategoriaCrearDTO categoria) {
        // Aqui valido que la categoria que llega cumple las reglas
        return categoriaService.guardarCategoria(categoria);
    }

    @PutMapping("/{id}")
    public CategoriaDTO actualizarCategoria(@PathVariable Long id, @Valid @RequestBody CategoriaCrearDTO categoria) {
        // Tambien valido en el update para evitar datos incorrectos
        return categoriaService.actualizarCategoria(id, categoria);
    }

    @DeleteMapping("/{id}")
    public void eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
    }
}