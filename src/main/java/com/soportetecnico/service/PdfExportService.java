package com.soportetecnico.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.soportetecnico.dto.ticket.TicketResumenDTO;
@Service
public class PdfExportService {

	public ByteArrayInputStream generarPdfDeTickets(List<TicketResumenDTO> tickets) {
	    Document document = new Document();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    try {
	        PdfWriter.getInstance(document, out);
	        document.open();

	        Paragraph titulo = new Paragraph("REPORTE DE TICKETS",
	                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK));
	        titulo.setAlignment(Element.ALIGN_CENTER);
	        document.add(titulo);
	        document.add(Chunk.NEWLINE);

	        PdfPTable table = new PdfPTable(5);
	        table.setWidthPercentage(100);
	        table.setWidths(new int[]{1, 4, 3, 3,2});

	        Stream.of("ID", "Título", "Usuario", "Estado","Fecha Creación")
	                .forEach(header -> {
	                    PdfPCell headerCell = new PdfPCell();
	                    headerCell.setBackgroundColor(Color.LIGHT_GRAY);
	                    headerCell.setPhrase(new Phrase(header));
	                    table.addCell(headerCell);
	                });

	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	        
	        tickets.sort(Comparator.comparing(TicketResumenDTO::getId));
	        
	        for (TicketResumenDTO t : tickets) {
	            table.addCell(String.valueOf(t.getId()));
	            table.addCell(t.getTitulo());
	            table.addCell(t.getClienteUsername());
	            table.addCell(t.getEstado().name());
	            table.addCell(t.getFechaCreacion().format(formatter));
	        }

	        document.add(table);
	        document.close();

	    } catch (DocumentException e) {
	        e.printStackTrace();
	    }

	    return new ByteArrayInputStream(out.toByteArray());
	}
}

