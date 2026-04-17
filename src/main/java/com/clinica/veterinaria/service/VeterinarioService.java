package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.Veterinario;
import com.clinica.veterinaria.repository.VeterinarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VeterinarioService {

    private final VeterinarioRepository repo;

    public VeterinarioService(VeterinarioRepository repo) {
        this.repo = repo;
    }

    public List<Veterinario> findAll() {
        return repo.findAll();
    }

    public Veterinario findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado: " + id));
    }

    public Veterinario save(Veterinario v) {
        return repo.save(v);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
