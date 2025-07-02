package com.soportetecnico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soportetecnico.model.MensajeTicket;


public interface MensajeTicketRepository extends JpaRepository<MensajeTicket, Long> {
    // Por ahora lo dejamos vacío. Los métodos CRUD básicos como save()
    // son heredados de JpaRepository y serán suficientes para empezar.
}