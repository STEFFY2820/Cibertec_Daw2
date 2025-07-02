package com.soportetecnico.dto.ticket;

import java.time.LocalDateTime;
import java.util.List;

import com.soportetecnico.model.EstadoTicket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDetalleDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoTicket estado;
    private String clienteUsername;
    private String agenteUsername;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private List<MensajeDTO> mensajes;
}