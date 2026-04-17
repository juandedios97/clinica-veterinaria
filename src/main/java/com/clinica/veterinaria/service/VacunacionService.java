package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.EstadoVacunacion;
import com.clinica.veterinaria.model.Vacunacion;
import com.clinica.veterinaria.repository.VacunacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class VacunacionService {

    private final VacunacionRepository repo;

    public VacunacionService(VacunacionRepository repo) {
        this.repo = repo;
    }

    public List<Vacunacion> findAll() {
        return repo.findAll().stream()
            .sorted(Comparator.comparing(Vacunacion::getFechaAplicada, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .toList();
    }

    public Vacunacion findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Vacunación no encontrada: " + id));
    }

    public Vacunacion save(Vacunacion vacunacion) {
        return repo.save(vacunacion);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public List<Vacunacion> findByMascota(Long mascotaId) {
        return repo.findByMascotaId(mascotaId);
    }

    public List<Vacunacion> findVencidas() {
        return repo.findByProximaDosisBefore(LocalDate.now());
    }

    public List<Vacunacion> findProximas() {
        LocalDate hoy = LocalDate.now();
        return repo.findByProximaDosisBetween(hoy, hoy.plusDays(30));
    }

    public List<Vacunacion> findAlDia() {
        LocalDate hoy = LocalDate.now();
        return repo.findAll().stream()
            .filter(v -> v.getProximaDosis() == null || v.getProximaDosis().isAfter(hoy.plusDays(30)))
            .toList();
    }

    public long countByEstado(EstadoVacunacion estado) {
        return repo.findAll().stream()
            .filter(v -> v.getEstadoCalculado() == estado)
            .count();
    }
}