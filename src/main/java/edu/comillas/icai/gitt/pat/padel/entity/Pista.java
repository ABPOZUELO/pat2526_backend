package edu.comillas.icai.gitt.pat.padel.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Pista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idPista;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre de la pista no puede estar vacío")
    public String nombre;
    
    @Column(nullable = false)
    @NotBlank(message = "La ubicación no puede estar vacía")
    public String ubicacion;
    
    @Column(nullable = false)
    @NotNull(message = "El precio por hora no puede estar vacío")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    public Double precioHora;   
    
    @Column(nullable = false)
    @NotNull(message = "El estado activo no puede estar vacío")
    public Boolean activa;
    
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Generated(value = "org.hibernate.annotations.GenerationTime.INSERT")
    @NotNull(message = "La fecha de alta no puede estar vacía")
    @Column(nullable = false)
    public LocalDateTime fechaAlta = LocalDateTime.now(); // Se asigna la fecha actual al crear el usuario
}
