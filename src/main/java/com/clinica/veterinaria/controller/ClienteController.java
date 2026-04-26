package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.Cita;
import com.clinica.veterinaria.model.EstadoCita;
import com.clinica.veterinaria.model.Mascota;
import com.clinica.veterinaria.model.Propietario;
import com.clinica.veterinaria.model.Usuario;
import com.clinica.veterinaria.service.CitaService;
import com.clinica.veterinaria.service.HistorialMedicoService;
import com.clinica.veterinaria.service.MascotaService;
import com.clinica.veterinaria.service.UsuarioService;
import com.clinica.veterinaria.service.VacunacionService;
import com.clinica.veterinaria.service.VeterinarioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Comparator;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final UsuarioService usuarioService;
    private final MascotaService mascotaService;
    private final CitaService citaService;
    private final HistorialMedicoService historialMedicoService;
    private final VacunacionService vacunacionService;
    private final VeterinarioService veterinarioService;

    public ClienteController(UsuarioService usuarioService, MascotaService mascotaService,
                             CitaService citaService, HistorialMedicoService historialMedicoService,
                             VacunacionService vacunacionService, VeterinarioService veterinarioService) {
        this.usuarioService = usuarioService;
        this.mascotaService = mascotaService;
        this.citaService = citaService;
        this.historialMedicoService = historialMedicoService;
        this.vacunacionService = vacunacionService;
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        Propietario propietario = propietarioActual(authentication);
        var mascotas = mascotaService.findByPropietario(propietario.getId());
        var citas = citaService.findByPropietario(propietario.getId());
        var vacunas = vacunacionService.findByPropietario(propietario.getId());

        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("citas", citas);
        model.addAttribute("proximasCitas", citas.stream()
            .filter(c -> c.getFechaHora() != null && !c.getFechaHora().isBefore(LocalDateTime.now()))
            .sorted(Comparator.comparing(Cita::getFechaHora))
            .limit(4)
            .toList());
        model.addAttribute("vacunasProximas", vacunas.stream()
            .filter(v -> v.getProximaDosis() != null)
            .sorted(Comparator.comparing(v -> v.getProximaDosis()))
            .limit(4)
            .toList());
        return "cliente/dashboard";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCita(Authentication authentication, Model model) {
        Propietario propietario = propietarioActual(authentication);
        cargarDatosFormularioCita(model, propietario);
        return "cliente/pedir-cita";
    }

    @PostMapping("/citas")
    public String solicitarCita(Authentication authentication,
                                @RequestParam Long mascotaId,
                                @RequestParam Long veterinarioId,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime fechaHora,
                                @RequestParam String motivo,
                                RedirectAttributes flash) {
        Propietario propietario = propietarioActual(authentication);
        Mascota mascota = mascotaService.findByIdAndPropietario(mascotaId, propietario.getId());

        Cita cita = new Cita();
        cita.setMascota(mascota);
        cita.setVeterinario(veterinarioService.findById(veterinarioId));
        cita.setFechaHora(fechaHora);
        cita.setMotivo(motivo);
        cita.setEstado(EstadoCita.PENDIENTE);
        citaService.save(cita);

        flash.addFlashAttribute("exito", "Solicitud de cita registrada. La clinica revisara la agenda y la confirmara contigo.");
        return "redirect:/cliente";
    }

    @GetMapping("/historial")
    public String historial(Authentication authentication, Model model) {
        Propietario propietario = propietarioActual(authentication);
        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotaService.findByPropietario(propietario.getId()));
        model.addAttribute("historiales", historialMedicoService.findByPropietario(propietario.getId()));
        model.addAttribute("vacunas", vacunacionService.findByPropietario(propietario.getId()));
        return "cliente/historial";
    }

    private void cargarDatosFormularioCita(Model model, Propietario propietario) {
        model.addAttribute("propietario", propietario);
        model.addAttribute("mascotas", mascotaService.findByPropietario(propietario.getId()));
        model.addAttribute("veterinarios", veterinarioService.findAll());
    }

    private Propietario propietarioActual(Authentication authentication) {
        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        if (usuario.getPropietario() == null) {
            throw new IllegalStateException("El usuario cliente no tiene propietario asociado.");
        }
        return usuario.getPropietario();
    }
}
