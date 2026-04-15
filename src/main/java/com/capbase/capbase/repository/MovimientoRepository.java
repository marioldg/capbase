package com.capbase.capbase.repository;

import com.capbase.capbase.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByUsuarioId(Long usuarioId);

    List<Movimiento> findByCategoriaId(Long categoriaId);

    // 🔥 ESTE ES EL QUE TE FALTA
    List<Movimiento> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);
}