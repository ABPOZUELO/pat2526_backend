package edu.comillas.icai.gitt.pat.padel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDateTime;

public record Pista (
    @NotNull(message = "El identificador de la pista no puede estar vacío")
    Integer idPista,
    
    @NotBlank(message = "El nombre de la pista no puede estar vacío")
    String nombre,
    
    @NotBlank(message = "La ubicación no puede estar vacía")
    String ubicacion,
    
    @NotNull(message = "El precio por hora no puede estar vacío")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    Double precioHora,
    
    @NotNull(message = "El estado activo no puede estar vacío")
    Boolean activa,
    
    @NotNull(message = "La fecha de alta no puede estar vacía")
    LocalDateTime fechaAlta
) {}
