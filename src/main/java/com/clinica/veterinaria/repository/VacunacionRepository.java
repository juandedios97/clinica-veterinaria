package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.Vacunacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Long> {
    List<Vacunacion> findByMascotaId(Long mascotaId);
    List<Vacunacion> findByProximaDosisBefore(LocalDate fecha);
    List<Vacunacion> findByProximaDosisBetween(LocalDate inicio, LocalDate fin);
}