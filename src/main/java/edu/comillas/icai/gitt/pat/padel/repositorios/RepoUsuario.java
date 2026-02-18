package edu.comillas.icai.gitt.pat.padel.repositorios;

import edu.comillas.icai.gitt.pat.padel.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface RepoUsuario extends CrudRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}