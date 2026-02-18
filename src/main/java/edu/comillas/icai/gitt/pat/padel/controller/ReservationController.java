package edu.comillas.icai.gitt.pat.padel.controller;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.entity.Rol;
import edu.comillas.icai.gitt.pat.padel.entity.Usuario;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoUsuario;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;

@RestController

@RequestMapping("/pistaPadel/reservations")
public class ReservationController {

    @Autowired
    private RepoReserva repoReserva;

    @Autowired
    private RepoUsuario RepoUsuario;

    @Autowired
    private RepoPista RepoPista;

    // Crear reserva: POST /pistaPadel/reservations
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//Si todo va bien devolver un "201 Created" por defecto, NO un "200 OK"
    public Reserva crearReserva(@RequestBody Reserva reserva){
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuarioAutenticado = authentication.getName();
        Usuario usuarioAutenticado = RepoUsuario.findByEmail(emailUsuarioAutenticado);

        // Lógica para asignar el idUsuario según el rol
        if (usuarioAutenticado.rol == Rol.ADMIN) {
            // Si es admin y no ha proporcionado idUsuario, usar su propio ID
            if (reserva.idUsuario == null) {
                reserva.idUsuario = usuarioAutenticado.id;
            }
            // Si es admin y ha proporcionado idUsuario, validar que exista
            else if (RepoUsuario.findById(reserva.idUsuario).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario con id " + reserva.idUsuario + " no existe");
            }
        } else {
            // Si no es admin, siempre usar su propio ID (ignorar cualquier idUsuario proporcionado)
            reserva.idUsuario = usuarioAutenticado.id;
        }

        // Calcular horaFin manualmente antes de las validaciones
        if (reserva.horaInicio != null && reserva.duracionMinutos != null) {
            reserva.horaFin = reserva.horaInicio.plusMinutes(reserva.duracionMinutos).minusSeconds(1);;
        }

        // Validar que la hora de inicio sea anterior a la hora de fin
        if (reserva.horaInicio.isAfter(reserva.horaFin) || reserva.horaInicio.isEqual(reserva.horaFin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar que exista la pista
        if (reserva.idPista == null || RepoPista.findById(reserva.idPista).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista con id " + reserva.idPista + " no existe");
        }

        // Validar que la reserva no se solape con otras reservas activas para la misma pista
        LocalDateTime inicio = reserva.horaInicio;
        LocalDateTime fin = reserva.horaFin;
        Long pistaId = reserva.idPista;

        boolean solapamiento = repoReserva.existsByIdPistaAndEstadoActivoAndHoraInicioLessThanEqualAndHoraFinGreaterThanEqual(
            pistaId, 
            reserva.estadoActivo, 
            fin, 
            inicio
        );

        if (solapamiento) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La reserva se solapa con otra reserva activa para la misma pista");
        }

        return repoReserva.save(reserva);
    }


}