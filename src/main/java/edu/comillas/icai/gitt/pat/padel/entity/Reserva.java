package edu.comillas.icai.gitt.pat.padel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long idUsuario;
    public Long idPista;
    public LocalDateTime horaInicio;
    public Integer duracionMinutos;
    public LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    public EstadosReserva estado; //
}