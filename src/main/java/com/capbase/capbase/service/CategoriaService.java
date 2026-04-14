package com.capbase.capbase.service;

import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Categoria;
import com.capbase.capbase.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria guardarCategoria(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Long id) {
        // Compruebo primero si existe antes de intentar borrarla
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
        }

        categoriaRepository.deleteById(id);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        // Busco la categoria para actualizar solo si existe
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);

        if (categoriaOptional.isPresent()) {
            Categoria categoriaExistente = categoriaOptional.get();

            categoriaExistente.setNombre(categoriaActualizada.getNombre());
            categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());

            return categoriaRepository.save(categoriaExistente);
        }

        throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
    }
}