package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.HistorialMedico;
import com.clinica.veterinaria.repository.HistorialMedicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HistorialMedicoService {

    private final HistorialMedicoRepository repo;

    public HistorialMedicoService(HistorialMedicoRepository repo) {
        this.repo = repo;
    }

    public List<HistorialMedico> findAll() { return repo.findAll(); }

    public HistorialMedico findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Historial no encontrado: " + id));
    }

    public HistorialMedico save(HistorialMedico h) { return repo.save(h); }

    public void deleteById(Long id) { repo.deleteById(id); }

    public List<HistorialMedico> findByMascota(Long mascotaId) {
        return repo.findByMascotaIdOrderByFechaDesc(mascotaId);
    }
}
