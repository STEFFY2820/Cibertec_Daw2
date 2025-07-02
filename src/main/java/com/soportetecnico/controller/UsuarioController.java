package com.soportetecnico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soportetecnico.model.Usuario;
import com.soportetecnico.service.UsuarioService;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;


	@PutMapping
	public ResponseEntity<Map<String, Object>> actualizar(@RequestBody Usuario usuario) {
		return usuarioService.actualizar(usuario);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
		return usuarioService.eliminar(id);
	}
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> listar() {
		return usuarioService.listar();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Map<String, Object>> buscarPorId(@PathVariable Long id) {
		return usuarioService.buscarPorId(id);
	}

	@GetMapping("/search-username")
	public ResponseEntity<Map<String, Object>> buscarPorUsername(@RequestParam String username) {
		return usuarioService.buscarPorUsername(username);
	}
}
