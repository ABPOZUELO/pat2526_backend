package edu.comillas.icai.gitt.pat.padel.model;

import java.time.LocalDateTime;

public class Pista {

//    Declaración de atributos

    private int idPista;
    private String nombre;
    private String ubicacion;
    private double precioHora;
    private boolean activa;
    private LocalDateTime fechaAlta;

//    Constructores

    public Pista() {}

    public Pista(int idPista, String nombre, String ubicacion, double precioHora) {
        this.idPista = idPista;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.precioHora = precioHora;
        this.activa = true;
        this.fechaAlta = LocalDateTime.now();
    }

//    Getter's y Setter's

    public int getIdPista() {
        return idPista;
    }

    public void setIdPista(int idPista) {
        this.idPista = idPista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(double precioHora) {
        this.precioHora = precioHora;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
