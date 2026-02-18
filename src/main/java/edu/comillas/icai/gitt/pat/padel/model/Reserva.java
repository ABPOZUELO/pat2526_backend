package edu.comillas.icai.gitt.pat.padel.model;
import java.time.LocalDateTime;

public class Reserva {
    private int idReserva;
    private int userId;
    private int courtId;
    private LocalDateTime date;
    private LocalDateTime startTime;
    private int durationMinutes;


    public Reserva() {}  

    public Reserva(int idReserva, int userId, int courtId, LocalDateTime date) {
        this.idReserva = idReserva;
        this.userId = userId;
        this.courtId = courtId;
        this.date = date;
    }
    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    
}
