package edu.comillas.icai.gitt.pat.padel.controller;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import edu.comillas.icai.gitt.pat.padel.BaseController;
import edu.comillas.icai.gitt.pat.padel.model.Reserva;

@RestController

@RequestMapping("/reservations")
public class ReservationController extends BaseController {
    private HashMap<Integer, Reserva> reservas =  new HashMap<>();

    private int contadorIdReserva = 1;

    @PostMapping    
    @ResponseStatus(HttpStatus.CREATED)//Si todo va bien devolver un "201 Created" por defecto, NO un "200 OK"
    public Reserva crearReserva(@RequestBody Reserva reserva){
        if(reserva.getCourtId() <= 0 || reserva.getDate() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rellene todos los campos obligatorios (courtId y date)");   //Error 400 y sale del metodo
        }
        if(CourtController.existePista(reserva.getCourtId()) == false){     //Al ser el metodo "existePista" static se puede llamar directamente con el nombre de la clase "CourtController"
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista introducida NO existe"); //Error 404
        }

        // ERROR 409 CONFLICT: Horario ya ocupado
        for(Reserva r : reservas.values()){
            if(r.getDate().equals(reserva.getDate()) && r.getCourtId() == reserva.getCourtId()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta pista ya esta ocupada para el horario introducido");
            }
        }
        reserva.setIdReserva(contadorIdReserva);
        contadorIdReserva++;
        reservas.put(reserva.getIdReserva(), reserva);
        return reserva;
    }


}