package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/availability")
public class AvailabilityController {

    @Autowired
    private RepoReserva repoReserva;

    // MÉTODO GET: Devuelve las horas ocupadas de una pista en un día concreto
    @GetMapping
    public List<Reserva> checkAvailability(
            @RequestParam Long idPista,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        // 1. Calculamos desde las 00:00 hasta las 23:59 de ese día
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);

        // 2. Usamos el método que ya tenías creado en tu repositorio
        // Devuelve solo las reservas ACTIVAS (ignora las canceladas) ordenadas por hora
        return repoReserva.findByIdPistaAndEstadoAndHoraInicioGreaterThanEqualAndHoraInicioLessThanOrderByHoraInicioAsc(
                idPista, EstadosReserva.ACTIVA, inicioDia, finDia
        );
    }
}