package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import edu.comillas.icai.gitt.pat.padel.entity.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel")
public class AdminController {
    
    @Autowired
    private RepoReserva repoReserva;

    // GET /pistaPadel/admin/reservations - Ver todas las reservas con filtros opcionales
    @GetMapping("/admin/reservations")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los admins pueden ver todas las reservas
    public Iterable<Reserva> getReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) Long userId) {
        
        // Si no hay filtros, devolver todas las reservas
        if (date == null && courtId == null && userId == null) {
            return repoReserva.findAll();
        }
        
        // Aplicar filtros según los parámetros recibidos
        List<Reserva> reservas = new ArrayList<>();
        
        if (date != null && courtId != null && userId != null) {
            // Filtrar por fecha, pista y usuario
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdPistaAndIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                courtId, userId, inicioDelDia, finDelDia);
        } else if (date != null && courtId != null) {
            // Filtrar por fecha y pista
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdPistaAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                courtId, inicioDelDia, finDelDia);
        } else if (date != null && userId != null) {
            // Filtrar por fecha y usuario
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                userId, inicioDelDia, finDelDia);
        } else if (courtId != null && userId != null) {
            // Filtrar por pista y usuario
            reservas = repoReserva.findByIdPistaAndIdUsuario(courtId, userId);
        } else if (date != null) {
            // Filtrar solo por fecha
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                inicioDelDia, finDelDia);
        } else if (courtId != null) {
            // Filtrar solo por pista
            reservas = repoReserva.findByIdPista(courtId);
        } else if (userId != null) {
            // Filtrar solo por usuario
            reservas = repoReserva.findByIdUsuario(userId);
        }
        
        return reservas;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
