package com.capbase.capbase.service;

import com.capbase.capbase.dto.PresupuestoCrearDTO;
import com.capbase.capbase.dto.PresupuestoDTO;
import com.capbase.capbase.dto.PresupuestoEstadoDTO;
import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Categoria;
import com.capbase.capbase.model.Movimiento;
import com.capbase.capbase.model.Presupuesto;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.CategoriaRepository;
import com.capbase.capbase.repository.MovimientoRepository;
import com.capbase.capbase.repository.PresupuestoRepository;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final MovimientoRepository movimientoRepository;

    public PresupuestoService(PresupuestoRepository presupuestoRepository,
                              UsuarioRepository usuarioRepository,
                              CategoriaRepository categoriaRepository,
                              MovimientoRepository movimientoRepository) {
        this.presupuestoRepository = presupuestoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    // obtener todos los presupuestos del usuario logueado
    public List<PresupuestoDTO> obtenerPresupuestos(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Presupuesto> presupuestos = presupuestoRepository.findAll();

        return presupuestos.stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    // crear presupuesto
    public PresupuestoDTO crearPresupuesto(PresupuestoCrearDTO dto, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setLimite(dto.getLimite());
        presupuesto.setMes(dto.getMes());
        presupuesto.setAnio(dto.getAnio());
        presupuesto.setUsuario(usuario);
        presupuesto.setCategoria(categoria);

        Presupuesto guardado = presupuestoRepository.save(presupuesto);

        return convertirDTO(guardado);
    }

    // eliminar presupuesto
    public void eliminarPresupuesto(Long id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado"));

        if (!presupuesto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este presupuesto");
        }

        presupuestoRepository.delete(presupuesto);
    }

    // mapper a DTO
    private PresupuestoDTO convertirDTO(Presupuesto p) {
        return new PresupuestoDTO(
                p.getId(),
                p.getLimite(),
                p.getMes(),
                p.getAnio(),
                p.getCategoria().getNombre()
        );
    }

    public List<PresupuestoEstadoDTO> obtenerEstadoPresupuestos(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        List<Presupuesto> presupuestos = presupuestoRepository.findAll()
                .stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .toList();

        List<Movimiento> movimientos = movimientoRepository
                .findByUsuarioId(usuario.getId(), Pageable.unpaged())
                .getContent();

        List<PresupuestoEstadoDTO> resultado = new ArrayList<>();

        for (Presupuesto p : presupuestos) {

            BigDecimal gastado = BigDecimal.ZERO;

            for (Movimiento m : movimientos) {

                boolean mismaCategoria = m.getCategoria().getId().equals(p.getCategoria().getId());
                boolean mismoMes = m.getFecha().getMonthValue() == p.getMes();
                boolean mismoAnio = m.getFecha().getYear() == p.getAnio();
                boolean esGasto = "GASTO".equalsIgnoreCase(m.getTipo());

                if (mismaCategoria && mismoMes && mismoAnio && esGasto) {
                    gastado = gastado.add(m.getCantidad());
                }
            }

            double porcentaje = 0;

            if (p.getLimite().compareTo(BigDecimal.ZERO) > 0) {
                porcentaje = gastado
                        .divide(p.getLimite(), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            resultado.add(new PresupuestoEstadoDTO(
                    p.getCategoria().getNombre(),
                    p.getLimite(),
                    gastado,
                    porcentaje
            ));
        }

        return resultado;
    }
}