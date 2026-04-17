package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.EstadoVacunacion;
import com.clinica.veterinaria.model.Vacunacion;
import com.clinica.veterinaria.service.MascotaService;
import com.clinica.veterinaria.service.VacunacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vacunacion")
public class VacunacionController {

    private final VacunacionService vacunacionService;
    private final MascotaService mascotaService;

    public VacunacionController(VacunacionService vacunacionService, MascotaService mascotaService) {
        this.vacunacionService = vacunacionService;
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("vacunaciones", vacunacionService.findAll());
        model.addAttribute("vencidas", vacunacionService.findVencidas());
        model.addAttribute("proximas", vacunacionService.findProximas());
        model.addAttribute("alDia", vacunacionService.findAlDia());
        model.addAttribute("vencidasCount", vacunacionService.countByEstado(EstadoVacunacion.VENCIDA));
        model.addAttribute("proximasCount", vacunacionService.countByEstado(EstadoVacunacion.PROXIMA));
        model.addAttribute("alDiaCount", vacunacionService.countByEstado(EstadoVacunacion.AL_DIA));
        return "vacunacion/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("vacunacion", new Vacunacion());
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("titulo", "Nueva vacunación");
        return "vacunacion/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("vacunacion", vacunacionService.findById(id));
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("titulo", "Editar vacunación");
        return "vacunacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Vacunacion vacunacion,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("mascotas", mascotaService.findAll());
            model.addAttribute("titulo", vacunacion.getId() == null ? "Nueva vacunación" : "Editar vacunación");
            return "vacunacion/formulario";
        }
        vacunacionService.save(vacunacion);
        flash.addFlashAttribute("exito", "Vacunación guardada correctamente");
        return "redirect:/vacunacion";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        vacunacionService.deleteById(id);
        flash.addFlashAttribute("exito", "Vacunación eliminada");
        return "redirect:/vacunacion";
    }
}