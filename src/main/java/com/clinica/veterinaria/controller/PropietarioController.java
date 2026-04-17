package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.Propietario;
import com.clinica.veterinaria.service.PropietarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/propietarios")
public class PropietarioController {

    private final PropietarioService service;

    public PropietarioController(PropietarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.isBlank()) {
            model.addAttribute("propietarios", service.buscar(buscar));
            model.addAttribute("buscar", buscar);
        } else {
            model.addAttribute("propietarios", service.findAll());
        }
        return "propietarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("propietario", new Propietario());
        model.addAttribute("titulo", "Nuevo propietario");
        return "propietarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("propietario", service.findById(id));
        model.addAttribute("titulo", "Editar propietario");
        return "propietarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Propietario propietario,
                          BindingResult result, Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", propietario.getId() == null ? "Nuevo propietario" : "Editar propietario");
            return "propietarios/formulario";
        }
        service.save(propietario);
        flash.addFlashAttribute("exito", "Propietario guardado correctamente");
        return "redirect:/propietarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        service.deleteById(id);
        flash.addFlashAttribute("exito", "Propietario eliminado");
        return "redirect:/propietarios";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("propietario", service.findById(id));
        return "propietarios/ver";
    }
}
