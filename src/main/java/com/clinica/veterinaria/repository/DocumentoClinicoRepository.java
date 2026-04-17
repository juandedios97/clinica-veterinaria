package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.DocumentoClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentoClinicoRepository extends JpaRepository<DocumentoClinico, Long> {
    List<DocumentoClinico> findByHistorialMedicoIdOrderByFechaSubidaDesc(Long historialId);
}