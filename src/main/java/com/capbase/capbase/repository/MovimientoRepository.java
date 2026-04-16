package com.capbase.capbase.repository;

import com.capbase.capbase.model.Movimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    Page<Movimiento> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Movimiento> findByCategoriaId(Long categoriaId, Pageable pageable);

    Page<Movimiento> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId, Pageable pageable);
}