package edu.comillas.icai.gitt.pat.padel.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

public record Usuario (
    @NotNull(message = "El identificador del usuario no puede estar vacío")
    Integer id,
    
    @NotBlank(message = "El nombre no puede estar vacío")
    String nombre,
    
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    String apellidos,
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    String email,
    
    @NotBlank(message = "La contraseña no puede estar vacía")
    String password,
    
    @NotBlank(message = "El teléfono no puede estar vacío")
    String telefono,
    
    @NotNull(message = "El rol no puede estar vacío")
    Rol rol,
    
    @NotNull(message = "La fecha de registro no puede estar vacía")
    LocalDateTime fechaRegistro,
    
    @NotNull(message = "El estado activo no puede estar vacío")
    Boolean activo
) {}