package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.model.Pista;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.comillas.icai.gitt.pat.padel.BaseController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController extends BaseController {

//    Hashmap para almacenar las pistas de pádel, con el id de la pista como clave y la pista como valor
    private static Map<Integer, Pista> pistas = new HashMap<>();

//    GET /pistaPadel/courts
    @GetMapping
//    OK 200 OK: Lista de pistas de pádel
    public Collection<Pista> getPistas(){
        return pistas.values();
    }

//    POST /pistaPadel/courts
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pista createPista(@RequestBody Pista pista){
//        ERROR 400 BAD REQUEST: Datos inválidos (nombre vacío, precio negativo)
        if (pista.getNombre() == null || pista.getNombre().isEmpty() || pista.getPrecioHora() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de la pista incorrectos");
        }
//        ERROR 409 CONFLICT: Pista con el mismo ID ya existe
        if (pistas.containsKey(pista.getIdPista())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pista con el mismo ID ya existe");
        }
//        ERROR 409 CONFLICT: Pista con el mismo nombre ya existe
        for (Pista p : pistas.values()) {
            if (p.getNombre().equalsIgnoreCase(pista.getNombre())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Pista con el mismo nombre ya existe");
            }
        }
//        OK 201 CREATED: Pista creada exitosamente
        pistas.put(pista.getIdPista(), pista);
        return pista;
    }

//    GET /pistaPadel/courts/{id}
    @GetMapping("/{id}")
    public Pista getPista(@PathVariable int id){
//        ERROR 404 NOT FOUND: Pista no encontrada
        if (!pistas.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
//        OK 200 OK: Detalles de la pista de pádel
        return pistas.get(id);
    }

//    DELETE /pistaPadel/courts/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePista(@PathVariable int id){
//        ERROR 404 NOT FOUND: Pista no encontrada
        if (!pistas.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
//        ERROR 409 CONFLICT: Pista con reservas futuras
        // Aquí se debería verificar si la pista tiene reservas futuras antes de eliminarla
//        OK 204 NO CONTENT: Pista eliminada exitosamente
        pistas.remove(id);
    }

//    PATCH /pistaPadel/courts/{id}
    @PatchMapping("/{id}")
    public Pista updatePista(@PathVariable int id, @RequestBody Pista datosNuevos){
        Pista pistaExistente = pistas.get(id);

//        ERROR 400 BAD REQUEST: Datos inválidos (nombre vacío, precio negativo)
        if (datosNuevos.getNombre() != null && datosNuevos.getNombre().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
        if (datosNuevos.getPrecioHora() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio no puede ser negativo");
        }
//        ERROR 404 NOT FOUND: Pista no encontrada
        if (pistaExistente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
//        OK 200 OK: Pista actualizada exitosamente
        if (pistaExistente != null) {
            if (datosNuevos.getNombre() != null) {
                pistaExistente.setNombre(datosNuevos.getNombre());
            }
            if (datosNuevos.getUbicacion() != null) {
                pistaExistente.setUbicacion(datosNuevos.getUbicacion());
            }
            if (datosNuevos.getPrecioHora() != 0) {
                pistaExistente.setPrecioHora(datosNuevos.getPrecioHora());
            }
            pistaExistente.setActiva(datosNuevos.isActiva());
        }
        return pistaExistente;
    }
    public static boolean existePista(int idPista){
        return pistas.containsKey(idPista);
    }

    public static Pista obtenerPista(int idPista){
        return pistas.get(idPista);
    }
}
