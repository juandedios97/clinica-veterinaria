package com.clinica.veterinaria.repository;

import com.clinica.veterinaria.model.EstadoFactura;
import com.clinica.veterinaria.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumero(String numero);
    List<Factura> findByEstado(EstadoFactura estado);
    List<Factura> findByFechaVencimientoBeforeAndEstado(LocalDate fecha, EstadoFactura estado);
    List<Factura> findByFechaEmisionBetween(LocalDate desde, LocalDate hasta);
    List<Factura> findByFechaEmisionGreaterThanEqual(LocalDate desde);
    List<Factura> findByFechaEmisionLessThanEqual(LocalDate hasta);
}
