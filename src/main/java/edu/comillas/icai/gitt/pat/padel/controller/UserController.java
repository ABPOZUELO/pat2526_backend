package edu.comillas.icai.gitt.pat.padel.controller;

import java.util.HashMap;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import edu.comillas.icai.gitt.pat.padel.BaseController;
import edu.comillas.icai.gitt.pat.padel.model.Usuario;
import scala.jdk.CollectionConverters;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;  
import java.util.HashMap;

@RestController //Indica que esta clase es un controlador REST (maneja solicitudes HTTP y devuelve respuestas JSON)

@RequestMapping("/users")   //Define la ruta base para todos los endpoints de este controlador
public class UserController extends BaseController {
    private HashMap<Integer, Usuario> usuarios =  new HashMap<>(); //Lista de users


    @PreAuthorize("hasRole('ADMIN')") 
    @GetMapping    //Sin parametros resonde a la ruta base
    public Collection<Usuario> getUsuarios() {
        return usuarios.values();               //Devuelve una colección de usuarios (los valores del HashMap)
    }                                           //Se envía un 200 "OK" por defecto a no ser que el usuario no este autorizado (no sea ADMIN)

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/{userId}")
    public Usuario getUsuario(@PathVariable int userId){
        if(!usuarios.containsKey(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");   //Error 404 y sale del metodo
        }

        return usuarios.get(userId);
    }
    
    @PreAuthorize("hasRole('ADMIN') or userId == authentication.principal.id")
    @PatchMapping("/{userId}")
    public Usuario updateUsuario(@PathVariable int userId, @RequestBody Usuario userNuevosDatos){
        // Obtener el usuario existente del HashMap usando su ID
        Usuario usuarioExistente = usuarios.get(userId);
    
        // Error 404 (user no encontrado)
        if (usuarioExistente == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        //Error 400(nombre vacio)
        if (userNuevosDatos.getNombre() != null && userNuevosDatos.getNombre().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
    
        // Y telefono vacio
        if (userNuevosDatos.getTelefono() != null && userNuevosDatos.getTelefono().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El teléfono no puede estar vacío");
        }
    
        // Error 409 (email duplicado)
        if (userNuevosDatos.getEmail() != null) {
        // Recorrer todos los usuarios del HashMap
            for (Usuario u : usuarios.values()) {
            // Verificar si otro usuario (diferente al actual) ya tiene ese email
               if (u.getEmail().equalsIgnoreCase(userNuevosDatos.getEmail()) && u.getIdUsuario() != userId) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya está en uso por otro usuario");
                }
            }
        }
    
    
    // ACTUALIZACIÓN PARCIAL: Solo actualizar campos que vengan en la petición
    // En PATCH, solo actualizamos los campos que NO son null en userNuevosDatos
    
        if (userNuevosDatos.getNombre() != null) {
            usuarioExistente.setNombre(userNuevosDatos.getNombre());
        }
    
        if (userNuevosDatos.getEmail() != null) {
            usuarioExistente.setEmail(userNuevosDatos.getEmail());
        }

        if (userNuevosDatos.getTelefono() != null) {
            usuarioExistente.setTelefono(userNuevosDatos.getTelefono());
        }
    
        if (userNuevosDatos.getPassword() != null) {
            usuarioExistente.setPassword(userNuevosDatos.getPassword());
        }
    
        return usuarioExistente;
}




        
    }
