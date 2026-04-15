package com.capbase.capbase.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {

        // Recojo todos los errores de validacion
        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        // Creo la respuesta que voy a devolver
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Error de validacion");
        respuesta.put("errores", errores);

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(ResourceNotFoundException ex) {

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }
}