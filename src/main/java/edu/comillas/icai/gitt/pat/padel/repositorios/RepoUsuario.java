package edu.comillas.icai.gitt.pat.padel.repositorios;

import org.springframework.data.repository.CrudRepository;
import edu.comillas.icai.gitt.pat.padel.entity.Usuario;

public interface RepoUsuario extends CrudRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}