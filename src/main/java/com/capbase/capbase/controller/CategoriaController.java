package com.capbase.capbase.controller;

import com.capbase.capbase.model.Categoria;
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
    public List<Categoria> obtenerCategorias() {
        return categoriaService.obtenerTodas();
    }

    @PostMapping
    public Categoria guardarCategoria(@Valid @RequestBody Categoria categoria) {
        // Aqui valido que la categoria que llega cumple las reglas (por ejemplo, nombre no vacio)
        return categoriaService.guardarCategoria(categoria);
    }

    @PutMapping("/{id}")
    public Categoria actualizarCategoria(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        // Tambien valido en el update para evitar datos incorrectos
        return categoriaService.actualizarCategoria(id, categoria);
    }

    @DeleteMapping("/{id}")
    public void eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
    }
}