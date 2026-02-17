package edu.comillas.icai.gitt.pat.padel.model;

public class Usuario {
    private int idUsuario;        
    private String nombre;        
    private String email;         
    private String telefono;      
    private String password;      
    private Rol rol;

    public Usuario() {}     //Constructor vacío

    //Constructor Comleto
    public Usuario(int idUsuario, String nombre, String email, String telefono, String password, Rol rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.rol = rol;
    }

    //Métodods Getter y Setter

    public int getIdUsuario(){
        return idUsuario;
    }

    public void setUsuario(int idUsuario){
        this.idUsuario = idUsuario;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getTelefono(){
        return telefono;
    }

    public void setTelefono(String telefono){
        this.telefono = telefono;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public Rol getRol(){
        return rol;
    }

    public void setRol(Rol rol){
        this.rol = rol;
    }
}
