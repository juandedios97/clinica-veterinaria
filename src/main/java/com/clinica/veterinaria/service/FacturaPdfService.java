package com.clinica.veterinaria.service;

import com.clinica.veterinaria.model.Factura;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class FacturaPdfService {

    public byte[] generarPdf(Factura factura) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            document.add(new Paragraph("Clinica Veterinaria", titleFont));
            document.add(new Paragraph("Factura: " + factura.getNumero(), normalBold));
            document.add(new Paragraph("Fecha emision: " + factura.getFechaEmision().format(dateFormatter), normal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell("Mascota");
            table.addCell(factura.getMascota().getNombre());
            table.addCell("Propietario");
            table.addCell(factura.getMascota().getPropietario().getNombreCompleto());
            table.addCell("Concepto");
            table.addCell(factura.getConcepto());
            table.addCell("Subtotal");
            table.addCell(factura.getSubtotal().toPlainString() + " EUR");
            table.addCell("Impuesto");
            table.addCell(factura.getImpuesto().toPlainString() + " EUR");
            table.addCell("Total");
            table.addCell(factura.getTotal().toPlainString() + " EUR");
            table.addCell("Estado");
            table.addCell(factura.getEstado().name());
            table.addCell("IVA aplicado");
            table.addCell(factura.isConIva() ? "SI (21%)" : "NO");
            document.add(table);

            document.close();
            return output.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el PDF de la factura", e);
        }
    }
}
