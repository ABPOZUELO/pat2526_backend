package edu.comillas.icai.gitt.pat.padel.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity 
public class Usuario {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  
  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "El email debe tener un formato válido")
  @Column(nullable = false, unique = true)
  public String email;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(nullable = false)
  @NotNull(message = "El rol no puede estar vacío")
  public Rol rol = Rol.USER; // Por defecto USER

  @NotBlank(message = "El nombre no puede estar vacío")
  @Column(nullable = false)
  public String nombre;
  
  @NotBlank(message = "Los apellidos no pueden estar vacíos")
  @Column(nullable = false)
  public String apellidos;
  
  @NotBlank(message = "La contraseña no puede estar vacía")
  @Column(nullable = false)
  public String password;
  
  @NotBlank(message = "El teléfono no puede estar vacío")
  @Column(nullable = false)
  public String telefono;
  
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Generated(value = "org.hibernate.annotations.GenerationTime.INSERT")
  @NotNull(message = "La fecha de registro no puede estar vacía")
  @Column(nullable = false)
  public LocalDateTime fechaRegistro = LocalDateTime.now(); // Se asigna la fecha actual al crear el usuario
  
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @NotNull(message = "El estado activo no puede estar vacío")
  @Column(nullable = false)
  public Boolean activo = true; // Por defecto activo
}

// INSERT INTO USUARIOS (email, rol, nombre, apellidos, password, telefono) VALUES ('prueba@gmail.com', 'USER', 'Prueba', 'Usuario', '$2a$12$vXrVT3YmVTzcdqTUdQfd6.c2XCUCtceXetWsvwoJMMgLnhDqpjzp.', '123456789');