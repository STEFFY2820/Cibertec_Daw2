package com.soportetecnico.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "usuarios", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long id;
	private String username;
	private String password;
	@Enumerated(EnumType.STRING)
	private Role role;

}
