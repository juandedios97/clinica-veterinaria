package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.Cita;
import com.clinica.veterinaria.model.EstadoCita;
import com.clinica.veterinaria.service.CitaService;
import com.clinica.veterinaria.service.MascotaService;
import com.clinica.veterinaria.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;

    public CitaController(CitaService citaService, MascotaService mascotaService,
                          VeterinarioService veterinarioService) {
        this.citaService = citaService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("citas", citaService.findAll().stream()
            .sorted(Comparator.comparing(c -> c.getFechaHora() == null ? LocalDateTime.MAX : c.getFechaHora()))
            .toList());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        return "citas/lista";
    }

    @GetMapping("/api/eventos")
    @ResponseBody
    public List<Map<String, Object>> eventos(@RequestParam(required = false) Long veterinarioId) {
        List<Cita> citas = veterinarioId != null
            ? citaService.findByVeterinario(veterinarioId)
            : citaService.findAll();

        return citas.stream()
            .filter(c -> c.getFechaHora() != null)
            .map(c -> {
                Map<String, Object> event = new LinkedHashMap<>();
                event.put("id", c.getId());
                event.put("title", c.getMascota().getNombre() + " - " + c.getMotivo());
                event.put("start", c.getFechaHora().toString());
                event.put("end", c.getFechaHora().plusMinutes(30).toString());
                event.put("color", colorPorEstado(c.getEstado()));

                Map<String, Object> extra = new LinkedHashMap<>();
                extra.put("veterinarioId", c.getVeterinario().getId());
                extra.put("veterinarioNombre", c.getVeterinario().getNombreCompleto());
                extra.put("mascota", c.getMascota().getNombre());
                extra.put("estado", c.getEstado().name());
                event.put("extendedProps", extra);
                return event;
            })
            .toList();
    }

    @PostMapping("/api/{id}/mover")
    @ResponseBody
    public Map<String, Object> mover(@PathVariable Long id,
                                     @RequestParam String nuevaFechaHora,
                                     @RequestParam(required = false) Long veterinarioId) {
        Map<String, Object> response = new LinkedHashMap<>();
        Cita cita = citaService.findById(id);

        try {
            cita.setFechaHora(LocalDateTime.parse(nuevaFechaHora));
        } catch (DateTimeParseException ex) {
            response.put("ok", false);
            response.put("message", "Fecha no valida");
            return response;
        }

        if (veterinarioId != null) {
            cita.setVeterinario(veterinarioService.findById(veterinarioId));
        }

        citaService.save(cita);
        response.put("ok", true);
        return response;
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("cita", new Cita());
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("estados", EstadoCita.values());
        model.addAttribute("titulo", "Nueva cita");
        return "citas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("cita", citaService.findById(id));
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("estados", EstadoCita.values());
        model.addAttribute("titulo", "Editar cita");
        return "citas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Cita cita,
                          BindingResult result, Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("mascotas", mascotaService.findAll());
            model.addAttribute("veterinarios", veterinarioService.findAll());
            model.addAttribute("estados", EstadoCita.values());
            model.addAttribute("titulo", cita.getId() == null ? "Nueva cita" : "Editar cita");
            return "citas/formulario";
        }
        citaService.save(cita);
        flash.addFlashAttribute("exito", "Cita guardada correctamente");
        return "redirect:/citas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        citaService.deleteById(id);
        flash.addFlashAttribute("exito", "Cita eliminada");
        return "redirect:/citas";
    }

    private String colorPorEstado(EstadoCita estado) {
        if (estado == null) {
            return "#f39c12";
        }
        return switch (estado) {
            case COMPLETADA -> "#2e7d32";
            case CANCELADA -> "#c62828";
            case PENDIENTE -> "#f39c12";
        };
    }
}
