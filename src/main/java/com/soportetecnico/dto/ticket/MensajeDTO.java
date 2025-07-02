package com.soportetecnico.dto.ticket;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MensajeDTO {
    private Long id;
    private String contenido;
    private String autorUsername;
    private LocalDateTime fechaEnvio;
}