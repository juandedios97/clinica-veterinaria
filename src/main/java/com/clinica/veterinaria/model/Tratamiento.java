package com.clinica.veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "tratamiento")
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tratamiento es obligatorio")
    @Size(max = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 100)
    private String dosis;

    @Size(max = 100)
    private String frecuencia;

    @Size(max = 100)
    private String duracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "El tipo de tratamiento es obligatorio")
    private TipoTratamiento tipo = TipoTratamiento.MEDICACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @NotNull(message = "El estado del tratamiento es obligatorio")
    private EstadoTratamiento estado = EstadoTratamiento.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_id", nullable = false)
    @NotNull
    private HistorialMedico historialMedico;

    public Tratamiento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDosis() { return dosis; }
    public void setDosis(String dosis) { this.dosis = dosis; }

    public String getFrecuencia() { return frecuencia; }
    public void setFrecuencia(String frecuencia) { this.frecuencia = frecuencia; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public TipoTratamiento getTipo() { return tipo; }
    public void setTipo(TipoTratamiento tipo) { this.tipo = tipo; }

    public EstadoTratamiento getEstado() { return estado; }
    public void setEstado(EstadoTratamiento estado) { this.estado = estado; }

    public HistorialMedico getHistorialMedico() { return historialMedico; }
    public void setHistorialMedico(HistorialMedico historialMedico) { this.historialMedico = historialMedico; }
}
