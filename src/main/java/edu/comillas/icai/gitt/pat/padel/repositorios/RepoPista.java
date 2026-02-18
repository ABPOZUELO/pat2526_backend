package edu.comillas.icai.gitt.pat.padel.repositorios;

import org.springframework.data.repository.CrudRepository;

import edu.comillas.icai.gitt.pat.padel.entity.Pista;

public interface RepoPista extends CrudRepository<Pista, Long>{
    Pista findByNombreIgnoreCase(String nombre);

    Iterable<Pista> findByActiva(Boolean activa);
}
