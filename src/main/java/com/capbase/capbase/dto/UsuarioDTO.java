package com.capbase.capbase.dto;

import java.time.LocalDate;

public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaRegistro;

    // Constructor vacío
    public UsuarioDTO() {
    }

    // Constructor completo
    public UsuarioDTO(Long id, String nombre, String email, LocalDate fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}