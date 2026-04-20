package com.capbase.capbase.service;

import com.capbase.capbase.dto.MovimientoCrearDTO;
import com.capbase.capbase.dto.MovimientoDTO;
import com.capbase.capbase.dto.PageResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public PageResponseDTO<MovimientoDTO> obtenerTodos(Long usuarioId, Long categoriaId, String search, String orden, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movimiento> paginaMovimientos;

        if (usuarioId != null && categoriaId != null) {
            paginaMovimientos = movimientoRepository.findByUsuarioIdAndCategoriaId(usuarioId, categoriaId, pageable);
        } else if (usuarioId != null) {
            paginaMovimientos = movimientoRepository.findByUsuarioId(usuarioId, pageable);
        } else if (categoriaId != null) {
            paginaMovimientos = movimientoRepository.findByCategoriaId(categoriaId, pageable);
        } else {
            paginaMovimientos = movimientoRepository.findAll(pageable);
        }

        // hago una copia para poder ordenar sin que pete
        List<Movimiento> movimientos = new ArrayList<>(paginaMovimientos.getContent());

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

        // ordenar por fecha
        if ("asc".equalsIgnoreCase(orden)) {
            movimientos.sort(Comparator.comparing(Movimiento::getFecha));
        } else {
            // por defecto desc
            movimientos.sort(Comparator.comparing(Movimiento::getFecha).reversed());
        }

        List<MovimientoDTO> content = movimientos.stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                content,
                paginaMovimientos.getNumber(),
                paginaMovimientos.getSize(),
                paginaMovimientos.getTotalElements(),
                paginaMovimientos.getTotalPages(),
                paginaMovimientos.isFirst(),
                paginaMovimientos.isLast()
        );
    }

    public PageResponseDTO<MovimientoDTO> obtenerMovimientosUsuarioLogueado(String email, Long categoriaId, String search, String orden, int page, int size) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return obtenerTodos(usuario.getId(), categoriaId, search, orden, page, size);
    }

    public ResumenMovimientoDTO obtenerResumenPorUsuario(Long usuarioId) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId, Pageable.unpaged()).getContent();

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

    public ResumenMovimientoDTO obtenerResumenPorUsuarioLogueado(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return obtenerResumenPorUsuario(usuario.getId());
    }

    public List<ResumenCategoriaDTO> obtenerResumenPorCategorias(Long usuarioId, String tipo, Integer mes, Integer anio) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId, Pageable.unpaged()).getContent();

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

    public List<ResumenCategoriaDTO> obtenerResumenPorCategoriasUsuarioLogueado(String email, String tipo, Integer mes, Integer anio) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return obtenerResumenPorCategorias(usuario.getId(), tipo, mes, anio);
    }

    public List<ResumenMensualDTO> obtenerResumenMensual(Long usuarioId, Integer anio) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId, Pageable.unpaged()).getContent();

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

    public List<ResumenMensualDTO> obtenerResumenMensualUsuarioLogueado(String email, Integer anio) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return obtenerResumenMensual(usuario.getId(), anio);
    }

    public List<ResumenCategoriaDTO> obtenerTopCategorias(Long usuarioId, Integer limite) {
        List<Movimiento> movimientos = movimientoRepository.findByUsuarioId(usuarioId, Pageable.unpaged()).getContent();

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

    public List<ResumenCategoriaDTO> obtenerTopCategoriasUsuarioLogueado(String email, Integer limite) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return obtenerTopCategorias(usuario.getId(), limite);
    }

    public MovimientoDTO guardarMovimiento(MovimientoCrearDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

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

    public void eliminarMovimiento(Long id, String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Movimiento movimiento = movimientoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

        movimientoRepository.delete(movimiento);
    }

    public MovimientoDTO actualizarMovimiento(Long id, MovimientoCrearDTO dto, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Movimiento movimiento = movimientoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));

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