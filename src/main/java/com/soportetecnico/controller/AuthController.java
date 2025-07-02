package com.soportetecnico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soportetecnico.model.Usuario;
import com.soportetecnico.service.UsuarioService;

@RestController
@RequestMapping("/auth")
public class AuthController {
		@Autowired
		private UsuarioService usuarioService;
	
		@PostMapping("/registrar")
		public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
			return usuarioService.registrar(usuario);
		}

}
