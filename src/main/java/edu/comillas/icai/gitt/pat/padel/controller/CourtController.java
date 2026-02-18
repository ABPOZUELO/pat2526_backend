package edu.comillas.icai.gitt.pat.padel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.entity.Pista;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoPista;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // GET /pistaPadel/courts/availability - Ver disponibilidad de todas las pistas o de una específica
    @GetMapping("/availability")
    public Map<String, Object> getAvailability(
            @RequestParam(required = false) Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        
        if (courtId == null) {
            // Retornar disponibilidad de todas las pistas
            List<Map<String, Object>> disponibilidadPistas = new ArrayList<>();
            Iterable<Pista> pistas = repoPista.findByActiva(true);
            
            for (Pista pista : pistas) {
                Map<String, Object> pistaInfo = obtenerDisponibilidadPista(pista, date);
                disponibilidadPistas.add(pistaInfo);
            }
            
            response.put("courts", disponibilidadPistas);
        } else {
            // Retornar disponibilidad de una pista específica
            Pista pista = repoPista.findById(courtId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista " + courtId + " no existe"));
            
            Map<String, Object> pistaInfo = obtenerDisponibilidadPista(pista, date);
            response.putAll(pistaInfo);
        }
        
        return response;
    }
    
    // Método auxiliar para calcular la disponibilidad de una pista en un día específico
    private Map<String, Object> obtenerDisponibilidadPista(Pista pista, LocalDate date) {
        Map<String, Object> pistaInfo = new HashMap<>();
        pistaInfo.put("courtId", pista.idPista);
        pistaInfo.put("name", pista.nombre);
        
        // Si la pista no está activa, no tiene horarios disponibles
        if (!pista.activa) {
            pistaInfo.put("available", false);
            pistaInfo.put("availableSlots", new ArrayList<>());
            return pistaInfo;
        }
        
        // Esto se puede cambiar, pero por ahora he decidido que el horario es de 8 a 22
        LocalDateTime inicioDelDia = date.atTime(8, 0); // Horario de apertura: 8:00
        LocalDateTime finDelDia = date.atTime(22, 0); // Horario de cierre: 22:00
        
        // Obtener todas las reservas activas del día para esta pista
        List<Reserva> reservasDelDia = repoReserva.findByIdPistaAndEstadoActivoAndHoraInicioGreaterThanEqualAndHoraInicioLessThanOrderByHoraInicioAsc(
            pista.idPista, 
            EstadosReserva.ACTIVA, 
            inicioDelDia, 
            finDelDia
        );
        
        // Calcular los slots disponibles
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        LocalDateTime horaActual = inicioDelDia;
        
        for (Reserva reserva : reservasDelDia) {
            // Si hay tiempo libre antes de esta reserva
            if (horaActual.isBefore(reserva.horaInicio)) {
                Map<String, Object> slot = new HashMap<>();
                slot.put("startTime", horaActual);
                slot.put("endTime", reserva.horaInicio);
                availableSlots.add(slot);
            }
            // Avanzar la hora actual al final de la reserva
            horaActual = reserva.horaFin.isAfter(horaActual) ? reserva.horaFin : horaActual;
        }
        
        // Si queda tiempo libre después de la última reserva
        if (horaActual.isBefore(finDelDia)) {
            Map<String, Object> slot = new HashMap<>();
            slot.put("startTime", horaActual);
            slot.put("endTime", finDelDia);
            availableSlots.add(slot);
        }
        
        pistaInfo.put("available", !availableSlots.isEmpty());
        pistaInfo.put("availableSlots", availableSlots);
        
        return pistaInfo;
    }

    // GET /pistaPadel/courts/{id}/availability - Ver disponibilidad de una pista específica
    @GetMapping("/{id}/availability")
    public Map<String, Object> getCourtAvailability(
            @PathVariable @NonNull Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Pista pista = repoPista.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista no ha sido encontrada"));
        
        Map<String, Object> response = obtenerDisponibilidadPista(pista, date);
        response.put("date", date);
        
        return response;
    }
}
