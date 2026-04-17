package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.model.EstadoFactura;
import com.clinica.veterinaria.model.Factura;
import com.clinica.veterinaria.service.CitaService;
import com.clinica.veterinaria.service.FacturaService;
import com.clinica.veterinaria.service.FacturaPdfService;
import com.clinica.veterinaria.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/facturacion")
public class FacturacionController {

    private final FacturaService facturaService;
    private final MascotaService mascotaService;
    private final CitaService citaService;
    private final FacturaPdfService facturaPdfService;

    public FacturacionController(FacturaService facturaService, MascotaService mascotaService, CitaService citaService,
                                 FacturaPdfService facturaPdfService) {
        this.facturaService = facturaService;
        this.mascotaService = mascotaService;
        this.citaService = citaService;
        this.facturaPdfService = facturaPdfService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                         Model model) {
        model.addAttribute("facturas", facturaService.findAllFiltrado(desde, hasta));
        model.addAttribute("pendientesCount", facturaService.countByEstado(EstadoFactura.PENDIENTE));
        model.addAttribute("pagadasCount", facturaService.countByEstado(EstadoFactura.PAGADA));
        model.addAttribute("vencidasCount", facturaService.countByEstado(EstadoFactura.VENCIDA));
        model.addAttribute("totalFacturado", facturaService.totalFacturado());
        model.addAttribute("totalPagado", facturaService.totalPagado());
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        return "facturacion/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        Factura factura = new Factura();
        factura.setNumero(facturaService.generarNumeroFactura());
        factura.setFechaEmision(LocalDate.now());
        factura.setConIva(true);
        factura.setPorcentajeIva(new BigDecimal("21.00"));
        model.addAttribute("factura", factura);
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("citas", citaService.findAll());
        model.addAttribute("estados", EstadoFactura.values());
        model.addAttribute("titulo", "Nueva factura");
        return "facturacion/formulario";
    }

    @GetMapping("/generar-desde-cita/{citaId}")
    public String generarDesdeCita(@PathVariable Long citaId, Model model) {
        var cita = citaService.findById(citaId);
        Factura factura = new Factura();
        factura.setNumero(facturaService.generarNumeroFactura());
        factura.setFechaEmision(LocalDate.now());
        factura.setMascota(cita.getMascota());
        factura.setCita(cita);
        factura.setConcepto("Consulta: " + cita.getMotivo());
        factura.setConIva(true);
        factura.setPorcentajeIva(new BigDecimal("21.00"));

        model.addAttribute("factura", factura);
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("citas", citaService.findAll());
        model.addAttribute("estados", EstadoFactura.values());
        model.addAttribute("titulo", "Generar factura desde cita");
        return "facturacion/formulario";
    }

    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("factura", facturaService.findById(id));
        model.addAttribute("mascotas", mascotaService.findAll());
        model.addAttribute("citas", citaService.findAll());
        model.addAttribute("estados", EstadoFactura.values());
        model.addAttribute("titulo", "Editar factura");
        return "facturacion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Factura factura,
                          BindingResult result,
                          Model model,
                          RedirectAttributes flash) {
        if (factura.getMascota() != null && factura.getMascota().getId() != null) {
            factura.setMascota(mascotaService.findById(factura.getMascota().getId()));
        }

        if (factura.getCita() == null || factura.getCita().getId() == null) {
            factura.setCita(null);
        } else {
            factura.setCita(citaService.findById(factura.getCita().getId()));
        }

        if (result.hasErrors()) {
            model.addAttribute("mascotas", mascotaService.findAll());
            model.addAttribute("citas", citaService.findAll());
            model.addAttribute("estados", EstadoFactura.values());
            model.addAttribute("titulo", factura.getId() == null ? "Nueva factura" : "Editar factura");
            return "facturacion/formulario";
        }

        if (factura.getEstado() == EstadoFactura.PAGADA && factura.getFechaPago() == null) {
            factura.setFechaPago(java.time.LocalDate.now());
        }
        if (factura.getEstado() != EstadoFactura.PAGADA) {
            factura.setFechaPago(null);
        }

        facturaService.save(factura);
        flash.addFlashAttribute("exito", "Factura guardada correctamente");
        return "redirect:/facturacion";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Factura factura = facturaService.findById(id);
        byte[] pdf = facturaPdfService.generarPdf(factura);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura-" + factura.getNumero() + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @PostMapping("/{id}/pagar")
    public String marcarPagada(@PathVariable Long id, RedirectAttributes flash) {
        facturaService.marcarComoPagada(id);
        flash.addFlashAttribute("exito", "Factura marcada como pagada");
        return "redirect:/facturacion";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        facturaService.deleteById(id);
        flash.addFlashAttribute("exito", "Factura eliminada");
        return "redirect:/facturacion";
    }
}
