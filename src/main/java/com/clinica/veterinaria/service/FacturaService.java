package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.EstadoFactura;
import com.clinica.veterinaria.model.Factura;
import com.clinica.veterinaria.repository.FacturaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository repo;

    public FacturaService(FacturaRepository repo) {
        this.repo = repo;
    }

    public List<Factura> findAll() {
        return findAllFiltrado(null, null);
    }

    public List<Factura> findAllFiltrado(LocalDate desde, LocalDate hasta) {
        actualizarVencidas();
        List<Factura> base;
        if (desde != null && hasta != null) {
            base = repo.findByFechaEmisionBetween(desde, hasta);
        } else if (desde != null) {
            base = repo.findByFechaEmisionGreaterThanEqual(desde);
        } else if (hasta != null) {
            base = repo.findByFechaEmisionLessThanEqual(hasta);
        } else {
            base = repo.findAll();
        }

        return base.stream()
            .sorted(Comparator.comparing(Factura::getFechaEmision, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
            .toList();
    }

    public Factura findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada: " + id));
    }

    public Factura save(Factura factura) {
        if (factura.getNumero() == null || factura.getNumero().isBlank()) {
            factura.setNumero(generarNumeroFactura());
        }
        if (factura.getFechaEmision() == null) {
            factura.setFechaEmision(LocalDate.now());
        }
        if (factura.getEstado() == null) {
            factura.setEstado(EstadoFactura.PENDIENTE);
        }
        if (factura.getPorcentajeIva() == null) {
            factura.setPorcentajeIva(new BigDecimal("21.00"));
        }
        return repo.save(factura);
    }

    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    public void marcarComoPagada(Long id) {
        Factura factura = findById(id);
        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDate.now());
        repo.save(factura);
    }

    public long countByEstado(EstadoFactura estado) {
        return repo.findByEstado(estado).size();
    }

    public BigDecimal totalFacturado() {
        return repo.findAll().stream()
            .map(Factura::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalPagado() {
        return repo.findByEstado(EstadoFactura.PAGADA).stream()
            .map(Factura::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String generarNumeroFactura() {
        long next = repo.count() + 1;
        return String.format("FAC-%05d", next);
    }

    private void actualizarVencidas() {
        LocalDate hoy = LocalDate.now();
        List<Factura> pendientesVencidas = repo.findByFechaVencimientoBeforeAndEstado(hoy, EstadoFactura.PENDIENTE);
        if (pendientesVencidas.isEmpty()) {
            return;
        }
        pendientesVencidas.forEach(f -> f.setEstado(EstadoFactura.VENCIDA));
        repo.saveAll(pendientesVencidas);
    }
}
