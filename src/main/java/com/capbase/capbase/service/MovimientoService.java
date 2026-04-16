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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public List<MovimientoDTO> obtenerTodos(Long usuarioId, Long categoriaId) {
        List<Movimiento> movimientos;

        // Si me pasan usuarioId y categoriaId, filtro por ambos
        if (usuarioId != null && categoriaId != null) {
            movimientos = movimientoRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId);

            // Si me pasan solo usuarioId, filtro por usuario
        } else if (usuarioId != null) {
            movimientos = movimientoRepository.findByUsuarioId(usuarioId);

            // Si me pasan solo categoriaId, filtro por categoria
        } else if (categoriaId != null) {
            movimientos = movimientoRepository.findByCategoriaId(categoriaId);

            // Si no me pasan nada, saco todos
        } else {
            movimientos = movimientoRepository.findAll();
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

            // si me pasan mes y año, filtro por fecha
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

        // ordenar de mayor a menor
        resumen.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));

        return resumen;
    }

    public List<ResumenMensualDTO> obtenerResumenMensual(Long usuarioId, Integer anio) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId);

        Map<Integer, BigDecimal> ingresosPorMes = new LinkedHashMap<>();
        Map<Integer, BigDecimal> gastosPorMes = new LinkedHashMap<>();

        // inicializo los 12 meses para que salgan todos aunque alguno esté a 0
        for (int mes = 1; mes <= 12; mes++) {
            ingresosPorMes.put(mes, BigDecimal.ZERO);
            gastosPorMes.put(mes, BigDecimal.ZERO);
        }

        for (Movimiento movimiento : movimientos) {
            if (movimiento.getFecha().getYear() == anio) {
                int mes = movimiento.getFecha().getMonthValue();

                if ("INGRESO".equalsIgnoreCase(movimiento.getTipo())) {
                    BigDecimal totalActual = ingresosPorMes.get(mes);
                    ingresosPorMes.put(mes, totalActual.add(movimiento.getCantidad()));
                } else if ("GASTO".equalsIgnoreCase(movimiento.getTipo())) {
                    BigDecimal totalActual = gastosPorMes.get(mes);
                    gastosPorMes.put(mes, totalActual.add(movimiento.getCantidad()));
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

    public MovimientoDTO guardarMovimiento(MovimientoCrearDTO dto) {
        // Busco el usuario por id
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + dto.getUsuarioId()));

        // Busco la categoria por id
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + dto.getCategoriaId()));

        // Creo el movimiento con los datos que vienen del DTO
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
        // Compruebo si existe antes de borrar
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