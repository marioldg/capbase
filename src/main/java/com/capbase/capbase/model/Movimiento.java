package com.capbase.capbase.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El concepto es obligatorio
    @NotBlank(message = "El concepto es obligatorio")
    private String concepto;

    private String descripcion;

    // La cantidad debe ser mayor que 0
    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor que 0")
    private BigDecimal cantidad;

    // La fecha no puede ser nula
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    // Tipo obligatorio (GASTO o INGRESO)
    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}