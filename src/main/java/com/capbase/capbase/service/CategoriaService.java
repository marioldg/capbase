package com.capbase.capbase.service;

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
        // Compruebo primero si existe para evitar borrar algo que no está
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria no encontrada con id: " + id);
        }

        categoriaRepository.deleteById(id);
    }

    public Categoria actualizarCategoria(Long id, Categoria categoriaActualizada) {
        // Busco la categoria por id
        Optional<Categoria> categoriaOptional = categoriaRepository.findById(id);

        if (categoriaOptional.isPresent()) {
            Categoria categoriaExistente = categoriaOptional.get();

            // Actualizo solo los campos que me interesan
            categoriaExistente.setNombre(categoriaActualizada.getNombre());
            categoriaExistente.setDescripcion(categoriaActualizada.getDescripcion());

            return categoriaRepository.save(categoriaExistente);
        }

        // Si no existe, lanzo una excepción en vez de devolver null
        throw new RuntimeException("Categoria no encontrada con id: " + id);
    }
}