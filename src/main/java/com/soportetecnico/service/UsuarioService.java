package com.soportetecnico.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.soportetecnico.model.Usuario;



public interface UsuarioService {
	//crear un usuario
    ResponseEntity<?> registrar(Usuario usuario);
	
	ResponseEntity<Map<String, Object>> actualizar(Usuario usuario);
	ResponseEntity<Map<String, Object>> eliminar(Long id);
	ResponseEntity<Map<String, Object>> listar();
	ResponseEntity<Map<String, Object>> buscarPorId(Long id);
	ResponseEntity<Map<String, Object>> buscarPorUsername(String username);
	
}
