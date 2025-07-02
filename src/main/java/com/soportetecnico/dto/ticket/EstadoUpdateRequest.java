package com.soportetecnico.dto.ticket;

import com.soportetecnico.model.EstadoTicket;

import lombok.Data;

@Data
public class EstadoUpdateRequest {
    private EstadoTicket nuevoEstado;
}