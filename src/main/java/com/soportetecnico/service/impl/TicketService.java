package com.soportetecnico.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soportetecnico.dto.ticket.EstadoUpdateRequest;
import com.soportetecnico.dto.ticket.MensajeCreacionRequest;
import com.soportetecnico.dto.ticket.MensajeDTO;
import com.soportetecnico.dto.ticket.TicketCreacionRequest;
import com.soportetecnico.dto.ticket.TicketDetalleDTO;
import com.soportetecnico.dto.ticket.TicketResumenDTO;
import com.soportetecnico.exception.ResourceNotFoundException;
import com.soportetecnico.model.EstadoTicket;
import com.soportetecnico.model.MensajeTicket;
import com.soportetecnico.model.Role;
import com.soportetecnico.model.Ticket;
import com.soportetecnico.model.Usuario;
import com.soportetecnico.repository.MensajeTicketRepository;
import com.soportetecnico.repository.TicketRepository;
import com.soportetecnico.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MensajeTicketRepository mensajeTicketRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public TicketDetalleDTO crearTicket(TicketCreacionRequest request, String clienteUsername) {
        Usuario cliente = usuarioRepository.findByUsername(clienteUsername)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario cliente con username: " + clienteUsername));

        Ticket nuevoTicket = Ticket.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .estado(EstadoTicket.ABIERTO)
                .cliente(cliente)
                .build();

        Ticket ticketGuardado = ticketRepository.save(nuevoTicket);
        return mapToTicketDetalleDTO(ticketGuardado);
    }

    @Transactional(readOnly = true)
    public List<TicketResumenDTO> obtenerTicketsPorUsuario(String username) {
        Usuario cliente = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con username: " + username));

        return ticketRepository.findByClienteId(cliente.getId()).stream() // Pasamos solo el ID
            .map(this::mapToTicketResumenDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TicketResumenDTO> obtenerTodosLosTicketsParaSoporte() {
        return ticketRepository.findAllByOrderByFechaCreacionDesc().stream()
                .map(this::mapToTicketResumenDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TicketDetalleDTO obtenerTicketPorId(Long ticketId, String usernameSolicitante) {
        Usuario solicitante = usuarioRepository.findByUsername(usernameSolicitante)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + usernameSolicitante));
        
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + ticketId));

        // Solo el cliente del ticket o un admin/soporte pueden verlo.
        if (!ticket.getCliente().equals(solicitante) && solicitante.getRole() == Role.USUARIO) {
            throw new AccessDeniedException("No tienes permiso para ver este ticket.");
        }

        return mapToTicketDetalleDTO(ticket);
    }

    @Transactional
    public MensajeDTO agregarMensaje(Long ticketId, MensajeCreacionRequest request, String autorUsername) {
        Usuario autor = usuarioRepository.findByUsername(autorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + autorUsername));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + ticketId));
        
        // Solo el cliente, el agente asignado o un admin/soporte pueden comentar.
        boolean isCliente = ticket.getCliente().equals(autor);
        boolean isAgente = ticket.getAgente() != null && ticket.getAgente().equals(autor);
        boolean isSoporteAdmin = autor.getRole() == Role.SOPORTE || autor.getRole() == Role.ADMIN;

        if (!isCliente && !isAgente && !isSoporteAdmin) {
            throw new AccessDeniedException("No tienes permiso para comentar en este ticket.");
        }

        MensajeTicket nuevoMensaje = MensajeTicket.builder()
                .contenido(request.getContenido())
                .ticket(ticket)
                .autor(autor)
                .build();
        
        // Si un agente de soporte responde por primera vez, se le asigna el ticket.
        if (isSoporteAdmin && ticket.getAgente() == null) {
            ticket.setAgente(autor);
        }
        ticket.setEstado(EstadoTicket.EN_PROGRESO); 
        ticketRepository.save(ticket); 

        MensajeTicket mensajeGuardado = mensajeTicketRepository.save(nuevoMensaje);
        return mapToMensajeDTO(mensajeGuardado);
    }
    
    @Transactional
    public TicketDetalleDTO actualizarEstado(Long ticketId, EstadoUpdateRequest request, String agenteUsername) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + ticketId));
        
        ticket.setEstado(request.getNuevoEstado());
        Ticket ticketActualizado = ticketRepository.save(ticket);
        
        return mapToTicketDetalleDTO(ticketActualizado);
    }

    // --- MÉTODOS PRIVADOS DE MAPEO (HELPERS) ---

    private TicketDetalleDTO mapToTicketDetalleDTO(Ticket ticket) {
        return TicketDetalleDTO.builder()
                .id(ticket.getId())
                .titulo(ticket.getTitulo())
                .descripcion(ticket.getDescripcion())
                .estado(ticket.getEstado())
                .clienteUsername(ticket.getCliente().getUsername())
                .agenteUsername(ticket.getAgente() != null ? ticket.getAgente().getUsername() : null)
                .fechaCreacion(ticket.getFechaCreacion())
                .fechaActualizacion(ticket.getFechaActualizacion())
                .mensajes(ticket.getMensajes() != null ? ticket.getMensajes().stream().map(this::mapToMensajeDTO).collect(Collectors.toList()) : List.of())
                .build();
    }
    
    private TicketResumenDTO mapToTicketResumenDTO(Ticket ticket) {
        return TicketResumenDTO.builder()
                .id(ticket.getId())
                .titulo(ticket.getTitulo())
                .estado(ticket.getEstado())
                .clienteUsername(ticket.getCliente().getUsername())
                .fechaCreacion(ticket.getFechaCreacion())
                .build();
    }

    private MensajeDTO mapToMensajeDTO(MensajeTicket mensaje) {
        return MensajeDTO.builder()
                .id(mensaje.getId())
                .contenido(mensaje.getContenido())
                .autorUsername(mensaje.getAutor().getUsername())
                .fechaEnvio(mensaje.getFechaEnvio())
                .build();
    }
}