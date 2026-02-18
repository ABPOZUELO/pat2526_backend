package edu.comillas.icai.gitt.pat.padel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.entity.Pista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pistaPadel/courts")
public class CourtController {

    @Autowired
    private RepoPista repoPista;

    @Autowired
    private RepoReserva repoReserva;

    // GET /pistaPadel/courts
    @GetMapping
    public Iterable<Pista> getPistas(@RequestParam(required = false) Boolean active){
        System.out.println("Valor del parámetro 'active': " + active);
        if (active != null) {
            return repoPista.findByActiva(active);
        }
        return repoPista.findAll();
    }

    // POST /pistaPadel/courts
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los admins pueden crear pistas
    public Pista createPista(@RequestBody @Valid Pista pista){
        // Validar que no exista una pista con el mismo nombre
        Pista pistaExistente = repoPista.findByNombreIgnoreCase(pista.nombre);
        if (pistaExistente != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una pista con el nombre '" + pista.nombre + "'");
        }
        
        return repoPista.save(pista);
    }

    // GET /pistaPadel/courts/{id}
    @GetMapping("/{id}")
    public Pista getPista(@PathVariable @NonNull Long id){
        Pista pista = repoPista.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));
        
        return pista;
    }

    // DELETE /pistaPadel/courts/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los admins pueden eliminar pistas
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePista(@PathVariable @NonNull Long id){
        Pista pista = repoPista.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));
        
        
        // Verificar que no tenga reservas futuras activas
        boolean tieneReservasFuturas = repoReserva.existsByIdPistaAndEstadoActivoAndHoraFinGreaterThanEqual(
            id, 
            EstadosReserva.ACTIVA, 
            LocalDateTime.now()
        );
        
        if (tieneReservasFuturas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la pista porque tiene reservas futuras");
        }
        
        pista.activa = false;
        repoPista.save(pista);
        
    }

    // PATCH /pistaPadel/courts/{id}
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los admins pueden actualizar pistas
    @SuppressWarnings("null")
    public Pista updatePista(@PathVariable @NonNull Long id, @RequestBody Pista datosNuevos){
        Pista pistaExistente = repoPista.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));

        // Validar datos si se proporcionan
        if (datosNuevos.nombre != null && !datosNuevos.nombre.isBlank()) {
            // Validar que no exista otra pista con el mismo nombre
            Pista pistaConMismoNombre = repoPista.findByNombreIgnoreCase(datosNuevos.nombre);
            if (pistaConMismoNombre != null && !pistaConMismoNombre.idPista.equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otra pista con el nombre '" + datosNuevos.nombre + "'");
            }
            pistaExistente.nombre = datosNuevos.nombre;
        }
        if (datosNuevos.ubicacion != null && !datosNuevos.ubicacion.isBlank()) {
            pistaExistente.ubicacion = datosNuevos.ubicacion;
        }
        
        if (datosNuevos.precioHora != null) {
            if (!(datosNuevos.precioHora > 0)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio por hora debe ser mayor que 0");
            }
            pistaExistente.precioHora = datosNuevos.precioHora;
        }
        
        if (datosNuevos.activa != null && datosNuevos.activa == true) { // Solo se puede activar una pista, para desactivar una pista se debe usar el DELETE
            pistaExistente.activa = datosNuevos.activa;
        }

        return repoPista.save(pistaExistente);
    }
}
