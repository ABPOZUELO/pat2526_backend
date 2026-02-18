package edu.comillas.icai.gitt.pat.padel.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long idReserva;

    @Column(nullable = false)
    @NotNull(message = "El ID del usuario no puede estar vacío")
    public Long idUsuario;

    @Column(nullable = false)
    @NotNull(message = "El ID de la pista no puede estar vacío")
    public Long idPista;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "La fecha no puede estar vacía")
    @Generated(value = "org.hibernate.annotations.GenerationTime.INSERT")
    public LocalDateTime fechaReserva = LocalDateTime.now(); // Se asigna la fecha actual al crear la reserva

    @Column(nullable = false)
    @NotNull(message = "La hora de inicio no puede estar vacía")
    public LocalDateTime horaInicio;

    @Column(nullable = false)
    @NotNull(message = "La duración no puede estar vacía")
    @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
    public Integer duracionMinutos;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "La hora de finalización no puede estar vacía")
    public LocalDateTime horaFin;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull(message = "El estado activo no puede estar vacío")
    public EstadosReserva estadoActivo = EstadosReserva.ACTIVA;

    // Esto se usa para calcular la hora de finalización automáticamente antes de guardar o actualizar la reserva en la base de datos
    @PrePersist
    @PreUpdate
    private void calcularHoraFin() {
        if (horaInicio != null && duracionMinutos != null) {
            this.horaFin = horaInicio.plusMinutes(duracionMinutos).minusSeconds(1);
        }
    }
}
