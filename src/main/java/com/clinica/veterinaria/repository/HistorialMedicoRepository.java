package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {
    List<HistorialMedico> findByMascotaIdOrderByFechaDesc(Long mascotaId);
}
