package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.RolUsuario;
import com.clinica.veterinaria.model.Usuario;
import com.clinica.veterinaria.service.PropietarioService;
import com.clinica.veterinaria.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PropietarioService propietarioService;

    public UsuarioController(UsuarioService usuarioService, PropietarioService propietarioService) {
        this.usuarioService = usuarioService;
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        cargarDatosFormulario(model);
        model.addAttribute("titulo", "Nuevo usuario");
        return "usuarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.findById(id));
        cargarDatosFormulario(model);
        model.addAttribute("titulo", "Editar usuario");
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        boolean edicionSinPassword = usuario.getId() != null && (usuario.getPassword() == null || usuario.getPassword().isBlank());
        boolean soloErroresPassword = result.getFieldErrors().stream().allMatch(e -> "password".equals(e.getField()));

        if (usuario.getRol() == RolUsuario.CLIENTE
                && (usuario.getPropietario() == null || usuario.getPropietario().getId() == null)) {
            result.rejectValue("propietario", "cliente.propietario", "Debe asociar un propietario para usuarios cliente");
        }

        if (result.hasErrors() && !(edicionSinPassword && soloErroresPassword)) {
            cargarDatosFormulario(model);
            model.addAttribute("titulo", usuario.getId() == null ? "Nuevo usuario" : "Editar usuario");
            return "usuarios/formulario";
        }

        usuarioService.save(usuario);
        flash.addFlashAttribute("exito", "Usuario guardado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.deleteById(id);
            flash.addFlashAttribute("exito", "Usuario eliminado");
        } catch (IllegalStateException ex) {
            flash.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("propietarios", propietarioService.findAll());
    }
}
