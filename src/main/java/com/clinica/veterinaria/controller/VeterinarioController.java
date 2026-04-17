package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.Veterinario;
import com.clinica.veterinaria.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/veterinarios")
public class VeterinarioController {

    private final VeterinarioService service;

    public VeterinarioController(VeterinarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("veterinarios", service.findAll());
        return "veterinarios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("veterinario", new Veterinario());
        model.addAttribute("titulo", "Nuevo veterinario");
        return "veterinarios/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("veterinario", service.findById(id));
        model.addAttribute("titulo", "Editar veterinario");
        return "veterinarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Veterinario veterinario,
                          @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
                          BindingResult result, Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo veterinario" : "Editar veterinario");
            return "veterinarios/formulario";
        }

        Veterinario existente = veterinario.getId() != null ? service.findById(veterinario.getId()) : null;
        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String contentType = fotoArchivo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo veterinario" : "Editar veterinario");
                model.addAttribute("error", "El archivo de foto debe ser una imagen válida");
                return "veterinarios/formulario";
            }
            if (fotoArchivo.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo veterinario" : "Editar veterinario");
                model.addAttribute("error", "La foto no puede superar 5MB");
                return "veterinarios/formulario";
            }
            try {
                veterinario.setFoto(fotoArchivo.getBytes());
                veterinario.setFotoContentType(contentType);
            } catch (IOException e) {
                model.addAttribute("titulo", veterinario.getId() == null ? "Nuevo veterinario" : "Editar veterinario");
                model.addAttribute("error", "No se pudo procesar la foto");
                return "veterinarios/formulario";
            }
        } else if (existente != null) {
            veterinario.setFoto(existente.getFoto());
            veterinario.setFotoContentType(existente.getFotoContentType());
        }

        service.save(veterinario);
        flash.addFlashAttribute("exito", "Veterinario guardado correctamente");
        return "redirect:/veterinarios";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("veterinario", service.findById(id));
        return "veterinarios/ver";
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        Veterinario veterinario = service.findById(id);
        byte[] foto = veterinario.getFoto();
        if (foto == null || foto.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = veterinario.getFotoContentType();
        String safeContentType = (contentType != null && contentType.startsWith("image/"))
            ? contentType
            : "image/jpeg";
        return ResponseEntity.ok().header("Content-Type", safeContentType).body(foto);
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        service.deleteById(id);
        flash.addFlashAttribute("exito", "Veterinario eliminado");
        return "redirect:/veterinarios";
    }
}
