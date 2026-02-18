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

@RestController
@RequestMapping("/pistaPadel/reservations")
public class ReservationController {

    @Autowired
    private RepoReserva repoReserva;
    @Autowired
    private RepoUsuario repoUsuario;
    @Autowired
    private RepoPista repoPista;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva crearReserva(@RequestBody Reserva reserva) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAutenticado = repoUsuario.findByEmail(auth.getName());

        // Usamos los campos públicos de tu equipo: idUsuario, rol, id
        if (usuarioAutenticado.rol == Rol.ADMIN) {
            if (reserva.idUsuario == null) reserva.idUsuario = usuarioAutenticado.id;
        } else {
            reserva.idUsuario = usuarioAutenticado.id;
        }

        if (reserva.idPista == null || repoPista.findById(reserva.idPista).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }

        // Cálculo de horaFin usando LocalDateTime
        if (reserva.horaInicio != null && reserva.duracionMinutos != null) {
            reserva.horaFin = reserva.horaInicio.plusMinutes(reserva.duracionMinutos);
        }

        // COMPROBACIÓN DE SOLAPAMIENTO (Sincronizada con el repositorio corregido)
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
        if (!repoReserva.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada");
        }
        repoReserva.deleteById(id);
    }
}