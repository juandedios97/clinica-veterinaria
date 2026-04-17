package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.Cita;
import com.clinica.veterinaria.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByVeterinarioId(Long veterinarioId);
    List<Cita> findByEstado(EstadoCita estado);
}
