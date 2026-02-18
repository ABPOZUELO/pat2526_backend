package edu.comillas.icai.gitt.pat.padel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import edu.comillas.icai.gitt.pat.padel.entity.Usuario;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoUsuario;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {
    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crea(@RequestBody @Valid Usuario usuarioNuevo){
        if(repoUsuario.findByEmail(usuarioNuevo.email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }

        // BLINDAJE: Forzamos que el usuario nuevo sea ROL.USER y esté activo
        usuarioNuevo.rol = edu.comillas.icai.gitt.pat.padel.entity.Rol.USER;
        usuarioNuevo.activo = true;

        usuarioNuevo.password = passwordEncoder.encode(usuarioNuevo.password);
        return repoUsuario.save(usuarioNuevo);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Usuario usuarioLogin, HttpServletRequest request) {
        try {
            Usuario usuario = repoUsuario.findByEmail(usuarioLogin.email);

            if (usuario == null || !usuario.activo) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o el usuario está deshabilitado");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            usuarioLogin.email,
                            usuarioLogin.password
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("sessionId", session.getId());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    @GetMapping("/me")
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String email = authentication.getName();
        Usuario usuario = repoUsuario.findByEmail(email);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        return usuario;
    }
}