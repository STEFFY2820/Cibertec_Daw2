package com.soportetecnico.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soportetecnico.dto.ticket.EstadoUpdateRequest;
import com.soportetecnico.dto.ticket.MensajeCreacionRequest;
import com.soportetecnico.dto.ticket.MensajeDTO;
import com.soportetecnico.dto.ticket.TicketCreacionRequest;
import com.soportetecnico.dto.ticket.TicketDetalleDTO;
import com.soportetecnico.dto.ticket.TicketResumenDTO;
import com.soportetecnico.model.Usuario;
import com.soportetecnico.service.PdfExportService;
import com.soportetecnico.service.impl.TicketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // Endpoint para que un USUARIO cree un ticket.
    @PostMapping
    public ResponseEntity<TicketDetalleDTO> crearTicket(
            @RequestBody TicketCreacionRequest request,
            @AuthenticationPrincipal Usuario usuario) { // Obtenemos el usuario autenticado
        
        TicketDetalleDTO ticketCreado = ticketService.crearTicket(request, usuario.getUsername());
        return new ResponseEntity<>(ticketCreado, HttpStatus.CREATED);
    }
    
    // Endpoint para que un SOPORTE/ADMIN vea todos los tickets.
    @GetMapping
    public ResponseEntity<List<TicketResumenDTO>> obtenerTodosLosTickets() {
        List<TicketResumenDTO> tickets = ticketService.obtenerTodosLosTicketsParaSoporte();
        return ResponseEntity.ok(tickets);
    }
    
    // Endpoint para que un USUARIO vea sus propios tickets.
    @GetMapping("/mytickets")
    public ResponseEntity<List<TicketResumenDTO>> obtenerMisTickets(
            @AuthenticationPrincipal Usuario usuario) {
        
        List<TicketResumenDTO> tickets = ticketService.obtenerTicketsPorUsuario(usuario.getUsername());
        return ResponseEntity.ok(tickets);
    }

    // Endpoint para ver el detalle de un ticket específico.
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetalleDTO> obtenerTicketPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        TicketDetalleDTO ticket = ticketService.obtenerTicketPorId(id, usuario.getUsername());
        return ResponseEntity.ok(ticket);
    }

    // Endpoint para añadir un mensaje a un ticket existente.
    @PostMapping("/{id}/messages")
    public ResponseEntity<MensajeDTO> agregarMensaje(
            @PathVariable Long id,
            @RequestBody MensajeCreacionRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        MensajeDTO nuevoMensaje = ticketService.agregarMensaje(id, request, usuario.getUsername());
        return new ResponseEntity<>(nuevoMensaje, HttpStatus.CREATED);
    }

    // Endpoint para que un SOPORTE/ADMIN actualice el estado de un ticket.
    @PutMapping("/{id}/status")
    public ResponseEntity<TicketDetalleDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestBody EstadoUpdateRequest request,
            @AuthenticationPrincipal Usuario usuario) {
        
        TicketDetalleDTO ticketActualizado = ticketService.actualizarEstado(id, request, usuario.getUsername());
        return ResponseEntity.ok(ticketActualizado);
    }
    
    @Autowired
    private PdfExportService pdfExportService;

    @GetMapping("/exportar-pdf")//2
    public ResponseEntity<InputStreamResource> exportarTickets() {
        List<TicketResumenDTO> tickets = ticketService.obtenerTodosLosTicketsParaSoporte();

        ByteArrayInputStream pdfStream = pdfExportService.generarPdfDeTickets(tickets);

        HttpHeaders headers = new HttpHeaders();
       
        
        headers.add("Content-Disposition",  "attachment; filename=tickets.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}