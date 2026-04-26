package com.clinica.veterinaria.controller;

import com.clinica.veterinaria.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class HomeController {

    private final MascotaService mascotaService;
    private final CitaService citaService;
    private final PropietarioService propietarioService;
    private final VeterinarioService veterinarioService;
    private final HistorialMedicoService historialMedicoService;
    private final TratamientoService tratamientoService;
    private final VacunacionService vacunacionService;

    public HomeController(MascotaService mascotaService, CitaService citaService,
                          PropietarioService propietarioService, VeterinarioService veterinarioService,
                          HistorialMedicoService historialMedicoService, TratamientoService tratamientoService,
                          VacunacionService vacunacionService) {
        this.mascotaService = mascotaService;
        this.citaService = citaService;
        this.propietarioService = propietarioService;
        this.veterinarioService = veterinarioService;
        this.historialMedicoService = historialMedicoService;
        this.tratamientoService = tratamientoService;
        this.vacunacionService = vacunacionService;
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication authentication) {
        boolean esCliente = authentication.getAuthorities().stream()
            .anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));
        return esCliente ? "redirect:/cliente" : "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String index(Model model) {
        var mascotas = mascotaService.findAll();
        var citas = citaService.findAll();
        var historiales = historialMedicoService.findAll();
        var tratamientosActivos = tratamientoService.findActivos().stream()
            .sorted(Comparator.comparing(t -> t.getNombre() == null ? "" : t.getNombre()))
            .toList();
        var vacunasVencidas = vacunacionService.findVencidas();
        var vacunasProximas = vacunacionService.findProximas();
        var vacunasAlDia = vacunacionService.findAlDia();

        LocalDate hoy = LocalDate.now();

        model.addAttribute("totalMascotas", mascotas.size());
        model.addAttribute("totalCitas", citas.size());
        model.addAttribute("totalPropietarios", propietarioService.findAll().size());
        model.addAttribute("totalVeterinarios", veterinarioService.findAll().size());

        model.addAttribute("ultimasCitas", citas.stream()
            .sorted(Comparator.comparing(c -> c.getFechaHora() == null ? LocalDateTime.MIN : c.getFechaHora(), Comparator.reverseOrder()))
            .limit(5)
            .toList());

        var citasHoy = citas.stream()
            .filter(c -> c.getFechaHora() != null && c.getFechaHora().toLocalDate().isEqual(hoy))
            .sorted(Comparator.comparing(c -> c.getFechaHora() == null ? LocalDateTime.MAX : c.getFechaHora()))
            .toList();

        model.addAttribute("alertaCitasHoyCount", citasHoy.size());
        model.addAttribute("alertaCitasHoy", citasHoy.stream().limit(5).toList());
        model.addAttribute("alertaTratamientosActivosCount", tratamientosActivos.size());
        model.addAttribute("alertaTratamientosActivos", tratamientosActivos.stream().limit(5).toList());
        model.addAttribute("alertaVacunasVencidasCount", vacunasVencidas.size());
        model.addAttribute("alertaVacunasVencidas", vacunasVencidas.stream().limit(5).toList());
        model.addAttribute("alertaVacunasProximasCount", vacunasProximas.size());
        model.addAttribute("alertaVacunasProximas", vacunasProximas.stream().limit(5).toList());
        model.addAttribute("alertaVacunasAlDiaCount", vacunasAlDia.size());

        // Consultas por semana (ultimas 8 semanas)
        Map<LocalDate, Long> consultasPorSemana = new LinkedHashMap<>();
        LocalDate inicioSemanaActual = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (int i = 7; i >= 0; i--) {
            LocalDate semana = inicioSemanaActual.minusWeeks(i);
            consultasPorSemana.put(semana, 0L);
        }
        historiales.stream()
            .filter(h -> h.getFecha() != null)
            .forEach(h -> {
                LocalDate semana = h.getFecha().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                if (consultasPorSemana.containsKey(semana)) {
                    consultasPorSemana.put(semana, consultasPorSemana.get(semana) + 1);
                }
            });

        List<String> consultasSemanaLabels = new ArrayList<>();
        List<Long> consultasSemanaData = new ArrayList<>();
        consultasPorSemana.forEach((semana, total) -> {
            consultasSemanaLabels.add(semana.toString());
            consultasSemanaData.add(total);
        });

        // Nuevas mascotas por mes (ultimos 6 meses)
        Map<String, Long> mascotasPorMes = new LinkedHashMap<>();
        LocalDate mesActual = hoy.withDayOfMonth(1);
        for (int i = 5; i >= 0; i--) {
            LocalDate mes = mesActual.minusMonths(i);
            String key = mes.getYear() + "-" + String.format("%02d", mes.getMonthValue());
            mascotasPorMes.put(key, 0L);
        }
        mascotas.stream()
            .filter(m -> m.getFechaRegistro() != null)
            .forEach(m -> {
                String key = m.getFechaRegistro().getYear() + "-" + String.format("%02d", m.getFechaRegistro().getMonthValue());
                if (mascotasPorMes.containsKey(key)) {
                    mascotasPorMes.put(key, mascotasPorMes.get(key) + 1);
                }
            });

        List<String> mascotasMesLabels = new ArrayList<>();
        List<Long> mascotasMesData = new ArrayList<>();
        mascotasPorMes.forEach((mes, total) -> {
            mascotasMesLabels.add(mes);
            mascotasMesData.add(total);
        });

        // Tipos de consultas mas comunes (por palabras clave)
        Map<String, Long> tipoConsultaConteo = new LinkedHashMap<>();
        tipoConsultaConteo.put("Vacunacion", 0L);
        tipoConsultaConteo.put("Desparasitacion", 0L);
        tipoConsultaConteo.put("Control", 0L);
        tipoConsultaConteo.put("Urgencia", 0L);
        tipoConsultaConteo.put("Dermatologia", 0L);
        tipoConsultaConteo.put("General", 0L);

        historiales.forEach(h -> {
            String texto = ((h.getDiagnostico() == null ? "" : h.getDiagnostico()) + " "
                + (h.getDescripcion() == null ? "" : h.getDescripcion())).toLowerCase(Locale.ROOT);
            String tipo;
            if (texto.contains("vacun")) {
                tipo = "Vacunacion";
            } else if (texto.contains("desparasit")) {
                tipo = "Desparasitacion";
            } else if (texto.contains("control") || texto.contains("revision")) {
                tipo = "Control";
            } else if (texto.contains("urgenc") || texto.contains("trauma") || texto.contains("fractura")) {
                tipo = "Urgencia";
            } else if (texto.contains("piel") || texto.contains("dermat")) {
                tipo = "Dermatologia";
            } else {
                tipo = "General";
            }
            tipoConsultaConteo.put(tipo, tipoConsultaConteo.get(tipo) + 1);
        });

        List<String> tipoConsultaLabels = new ArrayList<>();
        List<Long> tipoConsultaData = new ArrayList<>();
        tipoConsultaConteo.forEach((tipo, total) -> {
            if (total > 0) {
                tipoConsultaLabels.add(tipo);
                tipoConsultaData.add(total);
            }
        });

        model.addAttribute("consultasSemanaLabels", consultasSemanaLabels);
        model.addAttribute("consultasSemanaData", consultasSemanaData);
        model.addAttribute("mascotasMesLabels", mascotasMesLabels);
        model.addAttribute("mascotasMesData", mascotasMesData);
        model.addAttribute("tipoConsultaLabels", tipoConsultaLabels);
        model.addAttribute("tipoConsultaData", tipoConsultaData);

        return "index";
    }
}
