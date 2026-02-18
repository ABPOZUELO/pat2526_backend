package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.entity.Pista;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    @Autowired
    private RepoPista repoPista;

    @Autowired
    private RepoReserva repoReserva;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePista(@PathVariable Long id) {
        Pista pista = repoPista.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean tieneReservasFuturas = repoReserva.existsByIdPistaAndEstadoAndHoraFinGreaterThanEqual(
                id, EstadosReserva.ACTIVA, LocalDateTime.now()
        );

        if (tieneReservasFuturas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la pista");
        }

        pista.activa = false; // Coincide con tu Pista.java
        repoPista.save(pista);
    }

    @GetMapping("/{id}/reservations")
    public List<Reserva> getReservations(@PathVariable Long id, @RequestParam LocalDateTime date) {
        Pista pista = repoPista.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LocalDateTime inicioDelDia = date.withHour(8).withMinute(0);
        LocalDateTime finDelDia = date.withHour(22).withMinute(0);

        return repoReserva.findByIdPistaAndEstadoAndHoraInicioGreaterThanEqualAndHoraInicioLessThanOrderByHoraInicioAsc(
                pista.idPista, EstadosReserva.ACTIVA, inicioDelDia, finDelDia // Coincide con tu Pista.java
        );
    }
}