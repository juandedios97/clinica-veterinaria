package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.EstadoTratamiento;
import com.clinica.veterinaria.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {
    List<Tratamiento> findByHistorialMedicoId(Long historialId);
    List<Tratamiento> findByEstado(EstadoTratamiento estado);
}
