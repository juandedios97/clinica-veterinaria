package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.DocumentoClinico;
import com.clinica.veterinaria.model.EstadoTratamiento;
import com.clinica.veterinaria.model.HistorialMedico;
import com.clinica.veterinaria.model.TipoDocumentoClinico;
import com.clinica.veterinaria.model.TipoTratamiento;
import com.clinica.veterinaria.model.Tratamiento;
import com.clinica.veterinaria.service.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialMedicoService historialService;
    private final TratamientoService tratamientoService;
    private final MascotaService mascotaService;
    private final VeterinarioService veterinarioService;
    private final DocumentoClinicoService documentoClinicoService;

    public HistorialController(HistorialMedicoService historialService,
                               TratamientoService tratamientoService,
                               MascotaService mascotaService,
                               VeterinarioService veterinarioService,
                               DocumentoClinicoService documentoClinicoService) {
        this.historialService = historialService;
        this.tratamientoService = tratamientoService;
        this.mascotaService = mascotaService;
        this.veterinarioService = veterinarioService;
        this.documentoClinicoService = documentoClinicoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("historiales", historialService.findAll());
        return "historial/lista";
    }

    @GetMapping("/mascota/{mascotaId}")
    public String porMascota(@PathVariable Long mascotaId, Model model) {
        model.addAttribute("mascota", mascotaService.findById(mascotaId));
        model.addAttribute("historiales", historialService.findByMascota(mascotaId));
        return "historial/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(@RequestParam(required = false) Long mascotaId, Model model) {
        HistorialMedico h = new HistorialMedico();
        if (mascotaId != null) h.setMascota(mascotaService.findById(mascotaId));
        model.addAttribute("historial", h);
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("titulo", "Nueva consulta");
        return "historial/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("historial", historialService.findById(id));
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("veterinarios", veterinarioService.findAll());
        model.addAttribute("titulo", "Editar consulta");
        return "historial/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("historial") HistorialMedico historial,
                          BindingResult result, Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            model.addAttribute("mascotas", mascotaService.findAll());
            model.addAttribute("veterinarios", veterinarioService.findAll());
            model.addAttribute("titulo", historial.getId() == null ? "Nueva consulta" : "Editar consulta");
            return "historial/formulario";
        }
        historialService.save(historial);
        flash.addFlashAttribute("exito", "Consulta guardada correctamente");
        return "redirect:/historial";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        historialService.deleteById(id);
        flash.addFlashAttribute("exito", "Consulta eliminada");
        return "redirect:/historial";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model) {
        HistorialMedico h = historialService.findById(id);
        model.addAttribute("historial", h);
        model.addAttribute("tratamientos", tratamientoService.findByHistorial(id));
        model.addAttribute("documentos", documentoClinicoService.findByHistorial(id));
        model.addAttribute("nuevoTratamiento", new Tratamiento());
        model.addAttribute("nuevoDocumento", new DocumentoClinico());
        model.addAttribute("tiposTratamiento", TipoTratamiento.values());
        model.addAttribute("estadosTratamiento", EstadoTratamiento.values());
        model.addAttribute("tiposDocumento", TipoDocumentoClinico.values());
        return "historial/ver";
    }

    @PostMapping("/tratamiento/guardar/{historialId}")
    public String guardarTratamiento(@PathVariable Long historialId,
                                     @Valid @ModelAttribute("nuevoTratamiento") Tratamiento tratamiento,
                                     BindingResult result,
                                     RedirectAttributes flash) {
        if (result.hasErrors()) {
            flash.addFlashAttribute("errorTratamiento", "Revisa los datos del tratamiento");
            return "redirect:/historial/ver/" + historialId;
        }
        tratamiento.setHistorialMedico(historialService.findById(historialId));
        tratamientoService.save(tratamiento);
        flash.addFlashAttribute("exito", "Tratamiento añadido");
        return "redirect:/historial/ver/" + historialId;
    }

    @GetMapping("/tratamiento/eliminar/{tratamientoId}/{historialId}")
    public String eliminarTratamiento(@PathVariable Long tratamientoId,
                                      @PathVariable Long historialId,
                                      RedirectAttributes flash) {
        tratamientoService.deleteById(tratamientoId);
        flash.addFlashAttribute("exito", "Tratamiento eliminado");
        return "redirect:/historial/ver/" + historialId;
    }

    @PostMapping("/documento/guardar/{historialId}")
    public String guardarDocumento(@PathVariable Long historialId,
                                   @Valid @ModelAttribute("nuevoDocumento") DocumentoClinico documento,
                                   BindingResult result,
                                   @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                   Model model,
                                   RedirectAttributes flash) {
        if (archivo == null || archivo.isEmpty()) {
            result.reject("archivo.vacio", "Debes seleccionar un PDF o una imagen");
        }

        String contentType = archivo != null ? archivo.getContentType() : null;
        boolean esPdf = "application/pdf".equalsIgnoreCase(contentType);
        boolean esImagen = contentType != null && contentType.startsWith("image/");
        if (!result.hasErrors() && !esPdf && !esImagen) {
            result.reject("archivo.noSoportado", "Solo se admiten PDF e imágenes");
        }

        if (result.hasErrors()) {
            HistorialMedico h = historialService.findById(historialId);
            model.addAttribute("historial", h);
            model.addAttribute("tratamientos", tratamientoService.findByHistorial(historialId));
            model.addAttribute("documentos", documentoClinicoService.findByHistorial(historialId));
            model.addAttribute("nuevoTratamiento", new Tratamiento());
            model.addAttribute("error", "Revisa los datos del documento clínico");
            model.addAttribute("tiposTratamiento", TipoTratamiento.values());
            model.addAttribute("estadosTratamiento", EstadoTratamiento.values());
            model.addAttribute("tiposDocumento", TipoDocumentoClinico.values());
            return "historial/ver";
        }

        try {
            documento.setContenido(archivo.getBytes());
            documento.setNombreArchivo(archivo.getOriginalFilename());
            documento.setContentType(contentType);
            documento.setHistorialMedico(historialService.findById(historialId));
            if (documento.getTitulo() == null || documento.getTitulo().isBlank()) {
                documento.setTitulo(archivo.getOriginalFilename() != null ? archivo.getOriginalFilename() : "Documento clínico");
            }
            if (documento.getFechaSubida() == null) {
                documento.setFechaSubida(LocalDateTime.now());
            }
            documentoClinicoService.save(documento);
            flash.addFlashAttribute("exito", "Documento clínico subido correctamente");
        } catch (IOException e) {
            flash.addFlashAttribute("error", "No se pudo procesar el archivo");
        }
        return "redirect:/historial/ver/" + historialId;
    }

    @GetMapping("/documento/{documentoId}")
    public ResponseEntity<byte[]> verDocumento(@PathVariable Long documentoId) {
        DocumentoClinico documento = documentoClinicoService.findById(documentoId);
        byte[] contenido = documento.getContenido();
        if (contenido == null || contenido.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = documento.getContentType() != null ? documento.getContentType() : "application/octet-stream";
        return ResponseEntity.ok()
            .header("Content-Disposition", "inline; filename=\"" + (documento.getNombreArchivo() != null ? documento.getNombreArchivo() : "documento") + "\"")
            .header("Content-Type", contentType)
            .body(contenido);
    }

    @GetMapping("/documento/descargar/{documentoId}")
    public ResponseEntity<byte[]> descargarDocumento(@PathVariable Long documentoId) {
        DocumentoClinico documento = documentoClinicoService.findById(documentoId);
        byte[] contenido = documento.getContenido();
        if (contenido == null || contenido.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String filename = documento.getNombreArchivo() != null ? documento.getNombreArchivo() : "documento";
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
            .header("Content-Type", documento.getContentType() != null ? documento.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .body(contenido);
    }

    @GetMapping("/documento/eliminar/{documentoId}/{historialId}")
    public String eliminarDocumento(@PathVariable Long documentoId,
                                    @PathVariable Long historialId,
                                    RedirectAttributes flash) {
        documentoClinicoService.deleteById(documentoId);
        flash.addFlashAttribute("exito", "Documento eliminado");
        return "redirect:/historial/ver/" + historialId;
    }

    @PostMapping("/tratamiento/estado/{tratamientoId}/{historialId}")
    public String actualizarEstadoTratamiento(@PathVariable Long tratamientoId,
                                              @PathVariable Long historialId,
                                              @RequestParam EstadoTratamiento estado,
                                              RedirectAttributes flash) {
        Tratamiento tratamiento = tratamientoService.findById(tratamientoId);
        tratamiento.setEstado(estado);
        tratamientoService.save(tratamiento);
        flash.addFlashAttribute("exito", "Estado de tratamiento actualizado");
        return "redirect:/historial/ver/" + historialId;
    }
}
