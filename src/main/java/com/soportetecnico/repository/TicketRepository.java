package com.soportetecnico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soportetecnico.model.Ticket;

// Le decimos a Spring que este repositorio maneja entidades 'Ticket' 
// y que el tipo de su ID es 'Long'.
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    
    List<Ticket> findByClienteId(Long clienteId);

    List<Ticket> findAllByOrderByFechaCreacionDesc();

}