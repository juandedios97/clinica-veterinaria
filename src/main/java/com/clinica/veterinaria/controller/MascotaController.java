package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.Mascota;
import com.clinica.veterinaria.service.MascotaService;
import com.clinica.veterinaria.service.PropietarioService;
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
@RequestMapping("/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;
    private final PropietarioService propietarioService;

    public MascotaController(MascotaService mascotaService, PropietarioService propietarioService) {
        this.mascotaService = mascotaService;
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mascotas", mascotaService.findAll());
        return "mascotas/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("propietarios", propietarioService.findAll());
        model.addAttribute("titulo", "Nueva mascota");
        return "mascotas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.findById(id));
        model.addAttribute("propietarios", propietarioService.findAll());
        model.addAttribute("titulo", "Editar mascota");
        return "mascotas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Mascota mascota,
                          @RequestParam(value = "fotoArchivo", required = false) MultipartFile fotoArchivo,
                          BindingResult result, Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("propietarios", propietarioService.findAll());
            model.addAttribute("titulo", mascota.getId() == null ? "Nueva mascota" : "Editar mascota");
            return "mascotas/formulario";
        }

        Mascota mascotaExistente = mascota.getId() != null ? mascotaService.findById(mascota.getId()) : null;

        if (fotoArchivo != null && !fotoArchivo.isEmpty()) {
            String contentType = fotoArchivo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                model.addAttribute("propietarios", propietarioService.findAll());
                model.addAttribute("titulo", mascota.getId() == null ? "Nueva mascota" : "Editar mascota");
                model.addAttribute("error", "El archivo debe ser una imagen válida");
                return "mascotas/formulario";
            }
            if (fotoArchivo.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("propietarios", propietarioService.findAll());
                model.addAttribute("titulo", mascota.getId() == null ? "Nueva mascota" : "Editar mascota");
                model.addAttribute("error", "La imagen no puede superar 5MB");
                return "mascotas/formulario";
            }
            try {
                mascota.setFoto(fotoArchivo.getBytes());
                mascota.setFotoContentType(contentType);
            } catch (IOException e) {
                model.addAttribute("propietarios", propietarioService.findAll());
                model.addAttribute("titulo", mascota.getId() == null ? "Nueva mascota" : "Editar mascota");
                model.addAttribute("error", "No se pudo procesar la imagen");
                return "mascotas/formulario";
            }
        } else if (mascotaExistente != null) {
            mascota.setFoto(mascotaExistente.getFoto());
            mascota.setFotoContentType(mascotaExistente.getFotoContentType());
        }

        mascotaService.save(mascota);
        flash.addFlashAttribute("exito", "Mascota guardada correctamente");
        return "redirect:/mascotas";
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> foto(@PathVariable Long id) {
        Mascota mascota = mascotaService.findById(id);
        byte[] foto = mascota.getFoto();
        if (foto == null || foto.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = mascota.getFotoContentType();
        String safeContentType = (contentType != null && contentType.startsWith("image/"))
            ? contentType
            : "image/jpeg";

        return ResponseEntity.ok().header("Content-Type", safeContentType).body(foto);
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        mascotaService.deleteById(id);
        flash.addFlashAttribute("exito", "Mascota eliminada");
        return "redirect:/mascotas";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.findById(id));
        return "mascotas/ver";
    }
}
