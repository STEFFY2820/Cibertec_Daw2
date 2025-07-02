package com.soportetecnico.dto.ticket;

import java.time.LocalDateTime;

import com.soportetecnico.model.EstadoTicket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketResumenDTO {
    private Long id;
    private String titulo;
    private EstadoTicket estado;
    private String clienteUsername;
    private LocalDateTime fechaCreacion;
}