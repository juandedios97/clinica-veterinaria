package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.DocumentoClinico;
import com.clinica.veterinaria.repository.DocumentoClinicoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentoClinicoService {

    private final DocumentoClinicoRepository repo;
    private final JdbcTemplate jdbcTemplate;
    private volatile boolean schemaReady = false;

    public DocumentoClinicoService(DocumentoClinicoRepository repo, JdbcTemplate jdbcTemplate) {
        this.repo = repo;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DocumentoClinico> findByHistorial(Long historialId) {
        ensureSchema();
        return repo.findByHistorialMedicoIdOrderByFechaSubidaDesc(historialId);
    }

    public DocumentoClinico findById(Long id) {
        ensureSchema();
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Documento clínico no encontrado: " + id));
    }

    public DocumentoClinico save(DocumentoClinico documento) {
        ensureSchema();
        if (documento.getFechaSubida() == null) {
            documento.setFechaSubida(LocalDateTime.now());
        }
        return repo.save(documento);
    }

    public void deleteById(Long id) {
        ensureSchema();
        repo.deleteById(id);
    }

    private synchronized void ensureSchema() {
        if (schemaReady) {
            return;
        }
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS documento_clinico (" +
            "id BIGINT NOT NULL AUTO_INCREMENT, " +
            "titulo VARCHAR(150) NOT NULL, " +
            "tipo_documento VARCHAR(30) NOT NULL, " +
            "contenido LONGBLOB NOT NULL, " +
            "nombre_archivo VARCHAR(255), " +
            "content_type VARCHAR(120), " +
            "observaciones VARCHAR(255), " +
            "fecha_subida DATETIME NOT NULL, " +
            "historial_id BIGINT NOT NULL, " +
            "PRIMARY KEY (id), " +
            "CONSTRAINT fk_documento_historial FOREIGN KEY (historial_id) REFERENCES historial_medico(id) ON DELETE CASCADE" +
            ")");
        schemaReady = true;
    }
}