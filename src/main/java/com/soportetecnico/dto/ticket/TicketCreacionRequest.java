package com.soportetecnico.dto.ticket;

import lombok.Data;

@Data
public class TicketCreacionRequest {
    private String titulo;
    private String descripcion;
}