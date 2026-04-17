package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.Propietario;
import com.clinica.veterinaria.repository.PropietarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropietarioService {

    private final PropietarioRepository repo;

    public PropietarioService(PropietarioRepository repo) {
        this.repo = repo;
    }

    public List<Propietario> findAll() {
        return repo.findAll();
    }

    public Propietario findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Propietario no encontrado: " + id));
    }

    public Propietario save(Propietario p) {
        return repo.save(p);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public List<Propietario> buscar(String termino) {
        return repo.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(termino, termino);
    }
}
