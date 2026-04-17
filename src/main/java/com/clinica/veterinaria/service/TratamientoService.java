package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.EstadoTratamiento;
import com.clinica.veterinaria.model.Tratamiento;
import com.clinica.veterinaria.repository.TratamientoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TratamientoService {

    private final TratamientoRepository repo;
    private final JdbcTemplate jdbcTemplate;
    private volatile boolean advancedColumnsReady = false;

    public TratamientoService(TratamientoRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Tratamiento> findByHistorial(Long historialId) {
        return repo.findByHistorialMedicoId(historialId);
    }

    public List<Tratamiento> findAll() {
        return repo.findAll();
    }

    public List<Tratamiento> findActivos() {
        ensureAdvancedColumns();
        return repo.findByEstado(EstadoTratamiento.ACTIVO);
    }

    public Tratamiento findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado: " + id));
    }

    public Tratamiento save(Tratamiento t) {
        ensureAdvancedColumns();
        if (t.getEstado() == null) {
            t.setEstado(EstadoTratamiento.ACTIVO);
        }
        if (t.getTipo() == null) {
            t.setTipo(com.clinica.veterinaria.model.TipoTratamiento.MEDICACION);
        }
        return repo.save(t);
    }

    public void deleteById(Long id) { repo.deleteById(id); }

    private synchronized void ensureAdvancedColumns() {
        if (advancedColumnsReady) {
            return;
        }
        jdbcTemplate.execute("ALTER TABLE tratamiento ADD COLUMN IF NOT EXISTS frecuencia varchar(100)");
        jdbcTemplate.execute("ALTER TABLE tratamiento ADD COLUMN IF NOT EXISTS tipo varchar(30)");
        jdbcTemplate.execute("ALTER TABLE tratamiento ADD COLUMN IF NOT EXISTS estado varchar(30)");
        jdbcTemplate.execute("UPDATE tratamiento SET tipo = 'MEDICACION' WHERE tipo IS NULL OR tipo = ''");
        jdbcTemplate.execute("UPDATE tratamiento SET estado = 'ACTIVO' WHERE estado IS NULL OR estado = ''");
        advancedColumnsReady = true;
    }
}
