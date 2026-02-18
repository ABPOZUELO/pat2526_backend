package edu.comillas.icai.gitt.pat.padel.repositorios;

import org.springframework.data.repository.CrudRepository;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import java.time.LocalDateTime;
import java.util.List;

public interface RepoReserva extends CrudRepository<Reserva, Long> {

    // El método que usa TU controlador
    boolean existsByIdPistaAndEstadoAndHoraInicioLessThanEqualAndHoraFinGreaterThanEqual(
            Long idPista, EstadosReserva estado, LocalDateTime horaFin, LocalDateTime horaInicio
    );

    // El método que falta y que hace que falle el CourtController
    boolean existsByIdPistaAndEstadoActivoAndHoraFinGreaterThanEqual(
            Long idPista, EstadosReserva estado, LocalDateTime horaFin
    );

    // Métodos para el AdminController que ya pusimos antes
    List<Reserva> findByIdPistaAndIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
            Long idPista, Long idUsuario, LocalDateTime inicio, LocalDateTime fin
    );
    List<Reserva> findByIdPistaAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
            Long idPista, LocalDateTime inicio, LocalDateTime fin
    );
    List<Reserva> findByIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(
            Long idUsuario, LocalDateTime inicio, LocalDateTime fin
    );
    List<Reserva> findByHoraInicioGreaterThanEqualAndHoraInicioLessThan(
            LocalDateTime inicio, LocalDateTime fin
    );
    List<Reserva> findByIdPistaAndIdUsuario(Long idPista, Long idUsuario);
    List<Reserva> findByIdPista(Long idPista);
    List<Reserva> findByIdUsuario(Long idUsuario);
}