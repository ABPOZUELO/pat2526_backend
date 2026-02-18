package edu.comillas.icai.gitt.pat.padel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.comillas.icai.gitt.pat.padel.entity.Usuario;
import edu.comillas.icai.gitt.pat.padel.repositorios.RepoUsuario;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RepoUsuario repoUsuario;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repoUsuario.findByEmail(email);
        
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }
        
        if (!usuario.activo) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        return User.builder()
                .username(usuario.email)
                .password(usuario.password)
                .authorities(Collections.singleton(new SimpleGrantedAuthority(usuario.rol.name())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.activo)
                .build();
    }
}
