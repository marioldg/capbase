package com.capbase.capbase.service;

import com.capbase.capbase.dto.MovimientoCrearDTO;
import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.dto.ResumenCategoriaDTO;
import com.capbase.capbase.dto.ResumenMensualDTO;
import com.capbase.capbase.dto.ResumenMovimientoDTO;
import com.capbase.capbase.exception.ResourceNotFoundException;
import com.capbase.capbase.model.Categoria;
import com.capbase.capbase.model.Movimiento;
import com.capbase.capbase.model.Usuario;
import com.capbase.capbase.repository.CategoriaRepository;
import com.capbase.capbase.repository.MovimientoRepository;
import com.capbase.capbase.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             UsuarioRepository usuarioRepository,
                             CategoriaRepository categoriaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<MovimientoDTO> obtenerTodos(Long usuarioId, Long categoriaId, String search) {
        List<Movimiento> movimientos;

        if (usuarioId != null && categoriaId != null) {
            movimientos = movimientoRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId);
        } else if (usuarioId != null) {
            movimientos = movimientoRepository.findByUsuarioId(usuarioId);
        } else if (categoriaId != null) {
            movimientos = movimientoRepository.findByCategoriaId(categoriaId);
        } else {
            movimientos = movimientoRepository.findAll();
        }

        // si me pasan texto, filtro por concepto o descripcion
        if (search != null && !search.trim().isEmpty()) {
            String textoBusqueda = search.toLowerCase();

            movimientos = movimientos.stream()
                    .filter(movimiento ->
                            movimiento.getConcepto().toLowerCase().contains(textoBusqueda) ||
                                    (movimiento.getDescripcion() != null &&
                                            movimiento.getDescripcion().toLowerCase().contains(textoBusqueda))
                    )
                    .collect(Collectors.toList());
        }

        return movimientos.stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    public ResumenMovimientoDTO obtenerResumenPorUsuario(Long usuarioId) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId);

        BigDecimal totalIngresos = BigDecimal.ZERO;
        BigDecimal totalGastos = BigDecimal.ZERO;

        for (Movimiento movimiento : movimientos) {
            if ("INGRESO".equalsIgnoreCase(movimiento.getTipo())) {
                totalIngresos = totalIngresos.add(movimiento.getCantidad());
            } else if ("GASTO".equalsIgnoreCase(movimiento.getTipo())) {
                totalGastos = totalGastos.add(movimiento.getCantidad());
            }
        }

        BigDecimal balance = totalIngresos.subtract(totalGastos);

        return new ResumenMovimientoDTO(totalIngresos, totalGastos, balance);
    }

    public List<ResumenCategoriaDTO> obtenerResumenPorCategorias(Long usuarioId, String tipo, Integer mes, Integer anio) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId);

        Map<String, BigDecimal> totalesPorCategoria = new LinkedHashMap<>();

        for (Movimiento movimiento : movimientos) {
            boolean mismoTipo = movimiento.getTipo().equalsIgnoreCase(tipo);

            boolean mismaFecha = true;

            if (mes != null && anio != null) {
                mismaFecha = movimiento.getFecha().getMonthValue() == mes
                        && movimiento.getFecha().getYear() == anio;
            }

            if (mismoTipo && mismaFecha) {
                String nombreCategoria = movimiento.getCategoria().getNombre();
                BigDecimal totalActual = totalesPorCategoria.getOrDefault(nombreCategoria, BigDecimal.ZERO);
                totalesPorCategoria.put(nombreCategoria, totalActual.add(movimiento.getCantidad()));
            }
        }

        List<ResumenCategoriaDTO> resumen = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : totalesPorCategoria.entrySet()) {
            resumen.add(new ResumenCategoriaDTO(entry.getKey(), entry.getValue()));
        }

        resumen.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));

        return resumen;
    }

    public List<ResumenMensualDTO> obtenerResumenMensual(Long usuarioId, Integer anio) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId);

        Map<Integer, BigDecimal> ingresosPorMes = new LinkedHashMap<>();
        Map<Integer, BigDecimal> gastosPorMes = new LinkedHashMap<>();

        for (int mes = 1; mes <= 12; mes++) {
            ingresosPorMes.put(mes, BigDecimal.ZERO);
            gastosPorMes.put(mes, BigDecimal.ZERO);
        }

        for (Movimiento movimiento : movimientos) {
            if (movimiento.getFecha().getYear() == anio) {
                int mes = movimiento.getFecha().getMonthValue();

                if ("INGRESO".equalsIgnoreCase(movimiento.getTipo())) {
                    ingresosPorMes.put(mes, ingresosPorMes.get(mes).add(movimiento.getCantidad()));
                } else if ("GASTO".equalsIgnoreCase(movimiento.getTipo())) {
                    gastosPorMes.put(mes, gastosPorMes.get(mes).add(movimiento.getCantidad()));
                }
            }
        }

        List<ResumenMensualDTO> resumenMensual = new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {
            BigDecimal ingresos = ingresosPorMes.get(mes);
            BigDecimal gastos = gastosPorMes.get(mes);
            BigDecimal balance = ingresos.subtract(gastos);

            resumenMensual.add(new ResumenMensualDTO(mes, ingresos, gastos, balance));
        }

        return resumenMensual;
    }

    public List<ResumenCategoriaDTO> obtenerTopCategorias(Long usuarioId, Integer limite) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId);

        Map<String, BigDecimal> totalesPorCategoria = new HashMap<>();

        for (Movimiento movimiento : movimientos) {
            // solo gastos
            if ("GASTO".equalsIgnoreCase(movimiento.getTipo())) {
                String categoria = movimiento.getCategoria().getNombre();
                BigDecimal totalActual = totalesPorCategoria.getOrDefault(categoria, BigDecimal.ZERO);
                totalesPorCategoria.put(categoria, totalActual.add(movimiento.getCantidad()));
            }
        }

        return totalesPorCategoria.entrySet().stream()
                .map(entry -> new ResumenCategoriaDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getTotal().compareTo(a.getTotal()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    public MovimientoDTO guardarMovimiento(MovimientoCrearDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUsuarioId()));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + dto.getCategoriaId()));

        Movimiento movimiento = new Movimiento();
        movimiento.setConcepto(dto.getConcepto());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        return convertirDTO(movimientoGuardado);
    }

    public void eliminarMovimiento(Long id) {
        if (!movimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimiento no encontrado con id: " + id);
        }

        movimientoRepository.deleteById(id);
    }

    public MovimientoDTO actualizarMovimiento(Long id, MovimientoCrearDTO dto) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUsuarioId()));

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + dto.getCategoriaId()));

        movimiento.setConcepto(dto.getConcepto());
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFecha(dto.getFecha());
        movimiento.setTipo(dto.getTipo());
        movimiento.setUsuario(usuario);
        movimiento.setCategoria(categoria);

        Movimiento movimientoActualizado = movimientoRepository.save(movimiento);

        return convertirDTO(movimientoActualizado);
    }

    private MovimientoDTO convertirDTO(Movimiento movimiento) {
        return new MovimientoDTO(
                movimiento.getId(),
                movimiento.getConcepto(),
                movimiento.getDescripcion(),
                movimiento.getCantidad(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                movimiento.getCategoria().getNombre(),
                movimiento.getUsuario().getNombre()
        );
    }
}