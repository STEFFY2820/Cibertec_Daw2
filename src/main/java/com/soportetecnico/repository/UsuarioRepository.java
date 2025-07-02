package com.soportetecnico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soportetecnico.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Optional<Usuario> findByUsername(String username);
	List<Usuario> findByUsernameContainingIgnoreCase(String username);

	Boolean existsByUsername(String username);
}
