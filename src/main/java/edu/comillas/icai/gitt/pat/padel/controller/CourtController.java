package edu.comillas.icai.gitt.pat.padel.controller;

import edu.comillas.icai.gitt.pat.padel.model.Pista;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import edu.comillas.icai.gitt.pat.padel.BaseController;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController extends BaseController {

//    Hashmap para almacenar las pistas de pádel, con el id de la pista como clave y la pista como valor
    private Map<Integer, Pista> pistas = new HashMap<>();

//    GET /pistaPadel/courts
    @GetMapping
    public Collection<Pista> getPistas(){
        return pistas.values();
    }

//    POST /pistaPadel/courts
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pista createPista(@RequestBody Pista pista){
        pistas.put(pista.getIdPista(), pista);
        return pista;
    }

//    GET /pistaPadel/courts/{id}
    @GetMapping("/{id}")
    public Pista getPista(@PathVariable int id){
        return pistas.get(id);
    }

//    DELETE /pistaPadel/courts/{id}
    @DeleteMapping("/{id}")
    public void deletePista(@PathVariable int id){
        pistas.remove(id);
    }

//    PATCH /pistaPadel/courts/{id}
    @PatchMapping("/{id}")
    public Pista updatePista(@PathVariable int id, @RequestBody Pista datosNuevos){
        Pista pistaExistente = pistas.get(id);

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
}
