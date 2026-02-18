package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.entity.Pista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import edu.comillas.icai.gitt.pat.padel.entity.Reserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminController {

    @Autowired
    private RepoReserva repoReserva;

    @Autowired
    private RepoPista repoPista; // INYECTAMOS EL REPO DE PISTAS

    // --- GESTIÓN DE PISTAS (NUEVO) ---

    // Crear una pista nueva
    @PostMapping("/courts")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public Pista crearPista(@RequestBody Pista nuevaPista) {
        nuevaPista.activa = true; // Forzamos que nazca activa
        nuevaPista.fechaAlta = LocalDateTime.now();
        return repoPista.save(nuevaPista);
    }

    // Modificar una pista existente (precio, nombre, etc.)
    @PutMapping("/courts/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Pista editarPista(@PathVariable Long id, @RequestBody Pista datosNuevos) {
        Pista pistaExistente = repoPista.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));

        // Actualizamos solo lo que el admin quiera cambiar
        if (datosNuevos.nombre != null) pistaExistente.nombre = datosNuevos.nombre;
        if (datosNuevos.ubicacion != null) pistaExistente.ubicacion = datosNuevos.ubicacion;
        if (datosNuevos.precioHora != null) pistaExistente.precioHora = datosNuevos.precioHora;
        if (datosNuevos.activa != null) pistaExistente.activa = datosNuevos.activa;

        return repoPista.save(pistaExistente);
    }

    // --- GESTIÓN DE RESERVAS (FILTROS) ---

    @GetMapping("/reservations")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Iterable<Reserva> getReservations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long courtId,
            @RequestParam(required = false) Long userId) {

        if (date == null && courtId == null && userId == null) {
            return repoReserva.findAll();
        }

        List<Reserva> reservas = new ArrayList<>();

        // El bloque de filtros que ya tenías está perfecto, se mantiene igual
        if (date != null && courtId != null && userId != null) {
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdPistaAndIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                    courtId, userId, inicioDelDia, finDelDia);
        } else if (date != null && courtId != null) {
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdPistaAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                    courtId, inicioDelDia, finDelDia);
        } else if (date != null && userId != null) {
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                    userId, inicioDelDia, finDelDia);
        } else if (courtId != null && userId != null) {
            reservas = repoReserva.findByIdPistaAndIdUsuario(courtId, userId);
        } else if (date != null) {
            LocalDateTime inicioDelDia = date.atStartOfDay();
            LocalDateTime finDelDia = date.plusDays(1).atStartOfDay();
            reservas = repoReserva.findByHoraInicioGreaterThanEqualAndHoraInicioLessThan(
                    inicioDelDia, finDelDia);
        } else if (courtId != null) {
            reservas = repoReserva.findByIdPista(courtId);
        } else if (userId != null) {
            reservas = repoReserva.findByIdUsuario(userId);
        }

        return reservas;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}