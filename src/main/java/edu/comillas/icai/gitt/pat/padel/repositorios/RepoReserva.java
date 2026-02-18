package edu.comillas.icai.gitt.pat.padel.repositorios;

import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;

public interface RepoReserva extends CrudRepository<Reserva, Long>{
    boolean existsByIdPistaAndEstadoActivoAndHoraInicioLessThanEqualAndHoraFinGreaterThanEqual(Long pistaId, EstadosReserva estado, LocalDateTime horaFin, LocalDateTime horaInicio);
    
    boolean existsByIdPistaAndEstadoActivoAndHoraFinGreaterThanEqual(Long pistaId, EstadosReserva estado, LocalDateTime horaInicio);
    
    List<Reserva> findByIdPistaAndEstadoActivoAndHoraInicioGreaterThanEqualAndHoraInicioLessThanOrderByHoraInicioAsc(
        Long pistaId, EstadosReserva estado, LocalDateTime inicioDelDia, LocalDateTime finDelDia);
}
