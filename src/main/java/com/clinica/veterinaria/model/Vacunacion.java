package com.clinica.veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "vacunacion")
public class Vacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El tipo de vacuna es obligatorio")
    @Size(max = 120)
    @Column(name = "tipo_vacuna", nullable = false)
    private String tipoVacuna;

    @NotNull(message = "La fecha aplicada es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_aplicada", nullable = false)
    private LocalDate fechaAplicada;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;

    @Size(max = 255)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    @NotNull(message = "Debe seleccionar una mascota")
    private Mascota mascota;

    public Vacunacion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoVacuna() { return tipoVacuna; }
    public void setTipoVacuna(String tipoVacuna) { this.tipoVacuna = tipoVacuna; }

    public LocalDate getFechaAplicada() { return fechaAplicada; }
    public void setFechaAplicada(LocalDate fechaAplicada) { this.fechaAplicada = fechaAplicada; }

    public LocalDate getProximaDosis() { return proximaDosis; }
    public void setProximaDosis(LocalDate proximaDosis) { this.proximaDosis = proximaDosis; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    @Transient
    public EstadoVacunacion getEstadoCalculado() {
        LocalDate hoy = LocalDate.now();
        if (proximaDosis == null) {
            return EstadoVacunacion.AL_DIA;
        }
        if (proximaDosis.isBefore(hoy)) {
            return EstadoVacunacion.VENCIDA;
        }
        if (!proximaDosis.isAfter(hoy.plusDays(30))) {
            return EstadoVacunacion.PROXIMA;
        }
        return EstadoVacunacion.AL_DIA;
    }

    @Transient
    public String getBadgeClass() {
        return switch (getEstadoCalculado()) {
            case VENCIDA -> "bg-danger";
            case PROXIMA -> "bg-warning text-dark";
            case AL_DIA -> "bg-success";
        };
    }
}