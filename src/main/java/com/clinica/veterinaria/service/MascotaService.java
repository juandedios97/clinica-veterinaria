package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.Mascota;
import com.clinica.veterinaria.repository.MascotaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository repo;
    private final JdbcTemplate jdbcTemplate;
    private volatile boolean fotoColumnReady = false;
    private volatile boolean fechaRegistroColumnReady = false;

    public MascotaService(MascotaRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Mascota> findAll() {
        return repo.findAll();
    }

    public Mascota findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada: " + id));
    }

    public Mascota save(Mascota m) {
        ensureFotoColumnCapacity();
        ensureFechaRegistroColumn();
        if (m.getId() == null && m.getFechaRegistro() == null) {
            m.setFechaRegistro(LocalDate.now());
        }
        return repo.save(m);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public List<Mascota> findByPropietario(Long propietarioId) {
        return repo.findByPropietarioId(propietarioId);
    }

    private synchronized void ensureFotoColumnCapacity() {
        if (fotoColumnReady) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE mascota MODIFY COLUMN foto LONGBLOB");
        fotoColumnReady = true;
    }

    private synchronized void ensureFechaRegistroColumn() {
        if (fechaRegistroColumnReady) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE mascota ADD COLUMN IF NOT EXISTS fecha_registro date");
        fechaRegistroColumnReady = true;
    }
}
