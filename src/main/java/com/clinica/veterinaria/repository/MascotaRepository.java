package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByPropietarioId(Long propietarioId);
    Optional<Mascota> findByIdAndPropietarioId(Long id, Long propietarioId);
    List<Mascota> findByNombreContainingIgnoreCase(String nombre);
    List<Mascota> findByEspecieIgnoreCase(String especie);
}
