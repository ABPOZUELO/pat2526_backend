package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.entity.Rol;
import edu.comillas.icai.gitt.pat.padel.entity.Usuario;
import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoUsuario;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/pistaPadel/reservations")
public class ReservationController {

    @Autowired
    private RepoReserva repoReserva;
    @Autowired
    private RepoUsuario repoUsuario;
    @Autowired
    private RepoPista repoPista;

    // NUEVO MÉTODO: Para que los usuarios vean "Mis Reservas"
    @GetMapping
    public List<Reserva> obtenerMisReservas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = repoUsuario.findByEmail(auth.getName());

        Iterable<Reserva> todasLasReservas = repoReserva.findAll();

        // Si es ADMIN ve todas, si es usuario normal, solo ve las suyas
        if (usuarioAutenticado.rol == Rol.ADMIN) {
            return StreamSupport.stream(todasLasReservas.spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            return StreamSupport.stream(todasLasReservas.spliterator(), false)
                    .filter(r -> r.idUsuario.equals(usuarioAutenticado.id))
                    .collect(Collectors.toList());
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(@RequestBody Reserva reserva) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = repoUsuario.findByEmail(auth.getName());

        if (usuarioAutenticado.rol == Rol.ADMIN) {
            if (reserva.idUsuario == null) reserva.idUsuario = usuarioAutenticado.id;
        } else {
            reserva.idUsuario = usuarioAutenticado.id;
        }

        if (reserva.idPista == null || repoPista.findById(reserva.idPista).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }

        // MEJORA: Evitar que el sistema explote si el frontend no manda la hora
        if (reserva.horaInicio == null || reserva.duracionMinutos == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Es obligatorio enviar la hora de inicio y la duración");
        }

        // Cálculo de horaFin
        reserva.horaFin = reserva.horaInicio.plusMinutes(reserva.duracionMinutos);
        reserva.estado = EstadosReserva.ACTIVA; // Nos aseguramos de que nace ACTIVA

        // COMPROBACIÓN DE SOLAPAMIENTO
        boolean ocupado = repoReserva.existsByIdPistaAndEstadoAndHoraInicioLessThanEqualAndHoraFinGreaterThanEqual(
                reserva.idPista, EstadosReserva.ACTIVA, reserva.horaFin, reserva.horaInicio
        );

        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La pista ya está ocupada en ese horario");
        }

        return repoReserva.save(reserva);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = repoUsuario.findByEmail(auth.getName());

        Reserva reserva = repoReserva.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // MEJORA SEGURIDAD: Comprobamos que la reserva es suya o es un ADMIN
        if (usuarioAutenticado.rol != Rol.ADMIN && !reserva.idUsuario.equals(usuarioAutenticado.id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para cancelar esta reserva");
        }

        // Hacemos un "Soft Delete" cambiándole el estado en vez de borrarla de la base de datos
        reserva.estado = EstadosReserva.CANCELADA;
        repoReserva.save(reserva);
    }
}