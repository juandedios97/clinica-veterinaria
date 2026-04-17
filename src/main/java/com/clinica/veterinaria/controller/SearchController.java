package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.dto.SearchResultDTO;
import com.clinica.veterinaria.dto.SearchResponseDTO;
import com.clinica.veterinaria.model.HistorialMedico;
import com.clinica.veterinaria.model.Mascota;
import com.clinica.veterinaria.model.Propietario;
import com.clinica.veterinaria.repository.HistorialMedicoRepository;
import com.clinica.veterinaria.repository.MascotaRepository;
import com.clinica.veterinaria.repository.PropietarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
@CrossOrigin
public class SearchController {

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final HistorialMedicoRepository historialRepository;

    public SearchController(
            MascotaRepository mascotaRepository,
            PropietarioRepository propietarioRepository,
            HistorialMedicoRepository historialRepository
    ) {
        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
        this.historialRepository = historialRepository;
    }

    @GetMapping
    public ResponseEntity<SearchResponseDTO> search(@RequestParam String q) {
        if (q == null || q.trim().isEmpty() || q.length() < 2) {
            return ResponseEntity.ok(new SearchResponseDTO(List.of(), List.of(), List.of()));
        }

        String query = q.trim();

        // Búsqueda de mascotas
        List<Mascota> mascotas = mascotaRepository.findByNombreContainingIgnoreCase(query);
        List<SearchResultDTO> mascotasResults = mascotas.stream()
                .limit(5)
                .map(m -> new SearchResultDTO(
                        m.getId(),
                        "mascota",
                        m.getNombre(),
                        m.getEspecie() + (m.getRaza() != null ? " - " + m.getRaza() : ""),
                        "/mascotas/" + m.getId()
                ))
                .collect(Collectors.toList());

        // Búsqueda de propietarios
        List<Propietario> propietarios = propietarioRepository.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCase(query, query);
        List<SearchResultDTO> propietariosResults = propietarios.stream()
                .limit(5)
                .map(p -> new SearchResultDTO(
                        p.getId(),
                        "propietario",
                        p.getNombre() + " " + p.getApellidos(),
                        p.getTelefono() != null ? p.getTelefono() : p.getEmail() != null ? p.getEmail() : "Sin contacto",
                        "/propietarios/" + p.getId()
                ))
                .collect(Collectors.toList());

        // Búsqueda de historiales
        List<HistorialMedico> historiales = historialRepository.findAll().stream()
                .filter(h -> h.getDescripcion().toLowerCase().contains(query.toLowerCase()) ||
                             (h.getDiagnostico() != null && h.getDiagnostico().toLowerCase().contains(query.toLowerCase())))
                .limit(5)
                .collect(Collectors.toList());
        
        List<SearchResultDTO> historialesResults = historiales.stream()
                .map(h -> new SearchResultDTO(
                        h.getId(),
                        "historial",
                        h.getMascota() != null ? "Consulta - " + h.getMascota().getNombre() : "Consulta",
                        h.getDescripcion().substring(0, Math.min(50, h.getDescripcion().length())) + "...",
                        "/historial/" + h.getId()
                ))
                .collect(Collectors.toList());

        SearchResponseDTO response = new SearchResponseDTO(mascotasResults, propietariosResults, historialesResults);
        return ResponseEntity.ok(response);
    }
}
