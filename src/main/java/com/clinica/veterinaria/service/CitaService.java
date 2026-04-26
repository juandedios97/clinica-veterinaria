package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.Cita;
import com.clinica.veterinaria.repository.CitaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository repo;

    public CitaService(CitaRepository repo) {
        this.repo = repo;
    }

    public List<Cita> findAll() { return repo.findAll(); }

    public Cita findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Cita no encontrada: " + id));
    }

    public Cita save(Cita c) { return repo.save(c); }

    public void deleteById(Long id) { repo.deleteById(id); }

    public List<Cita> findByMascota(Long mascotaId) {
        return repo.findByMascotaId(mascotaId);
    }

    public List<Cita> findByPropietario(Long propietarioId) {
        return repo.findByMascotaPropietarioIdOrderByFechaHoraDesc(propietarioId);
    }

    public List<Cita> findByVeterinario(Long veterinarioId) {
        return repo.findByVeterinarioId(veterinarioId);
    }
}
