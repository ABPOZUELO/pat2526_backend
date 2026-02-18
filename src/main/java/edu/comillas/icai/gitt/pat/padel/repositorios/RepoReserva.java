package edu.comillas.icai.gitt.pat.padel.repositorios;

import org.springframework.data.repository.CrudRepository;
import edu.comillas.icai.gitt.pat.padel.entity.Reserva;
import edu.comillas.icai.gitt.pat.padel.entity.EstadosReserva;
import java.time.LocalDateTime;
import java.util.List;

public interface RepoReserva extends CrudRepository<Reserva, Long> {

    // 1. Para TU controlador (ReservationController)
    boolean existsByIdPistaAndEstadoAndHoraInicioLessThanEqualAndHoraFinGreaterThanEqual(
            Long idPista, EstadosReserva estado, LocalDateTime horaFin, LocalDateTime horaInicio
    );

    // 2. Para el CourtController (Comprobar si hay reservas para poder borrar la pista)
    boolean existsByIdPistaAndEstadoAndHoraFinGreaterThanEqual(
            Long idPista, EstadosReserva estado, LocalDateTime horaFin
    );

    // 3. Para el listado de reservas por pista
    List<Reserva> findByIdPistaAndEstadoAndHoraInicioGreaterThanEqualAndHoraInicioLessThanOrderByHoraInicioAsc(
            Long idPista, EstadosReserva estado, LocalDateTime inicio, LocalDateTime fin
    );

    // 4. Métodos para el AdminController
    List<Reserva> findByIdPistaAndIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(Long idPista, Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    List<Reserva> findByIdPistaAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(Long idPista, LocalDateTime inicio, LocalDateTime fin);
    List<Reserva> findByIdUsuarioAndHoraInicioGreaterThanEqualAndHoraInicioLessThan(Long idUsuario, LocalDateTime inicio, LocalDateTime fin);
    List<Reserva> findByHoraInicioGreaterThanEqualAndHoraInicioLessThan(LocalDateTime inicio, LocalDateTime fin);
    List<Reserva> findByIdPistaAndIdUsuario(Long idPista, Long idUsuario);
    List<Reserva> findByIdPista(Long idPista);
    List<Reserva> findByIdUsuario(Long idUsuario);
}