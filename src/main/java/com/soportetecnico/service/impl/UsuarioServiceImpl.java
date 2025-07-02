package com.soportetecnico.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soportetecnico.model.Usuario;
import com.soportetecnico.repository.UsuarioRepository;
import com.soportetecnico.service.UsuarioService;
import com.soportetecnico.utils.Response;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder; // Para encriptar contraseñas si es necesario

	@Override
	public ResponseEntity<?> registrar(Usuario usuario) {

		try {
		    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		    if(usuarioRepository.existsByUsername(usuario.getUsername())) {
		        return Response.crearResponse(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está en uso.", null);
		    }

			Usuario nuevoUsuario = usuarioRepository.save(usuario);
			Usuario usuarioMapeado = usuarioRepository.findById(nuevoUsuario.getId())
				    .orElseThrow(() -> new RuntimeException("No encontrado"));
			return Response.crearResponse(HttpStatus.CREATED, "Usuario registrado correctamente.", usuarioMapeado);
		} catch (Exception e) {
			return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al registrar el usuario: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> actualizar(Usuario usuario) {
		if (usuario == null || usuario.getId() == null || usuario.getId() <= 0) {
			return Response.crearResponse(HttpStatus.BAD_REQUEST, "ID del usuario es inválido o nulo.", null);
		}

		try {
			return usuarioRepository.findById(usuario.getId())
					.map(existing -> {
						
						if (usuario.getUsername() != null) existing.setUsername(usuario.getUsername());
						if (usuario.getPassword() != null) existing.setPassword(passwordEncoder.encode(usuario.getPassword()));
						if (usuario.getRole() != null) existing.setRole(usuario.getRole());

						Usuario actualizado = usuarioRepository.save(existing);
						return Response.crearResponse(HttpStatus.OK, "Usuario actualizado correctamente.", actualizado);
					})
					.orElse(Response.crearResponse(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID " + usuario.getId(), null));
		} catch (Exception e) {
			return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el usuario: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> eliminar(Long id) {
		if (id == null || id <= 0) {
			return Response.crearResponse(HttpStatus.BAD_REQUEST, "ID del usuario es inválido.", null);
		}

		try {
			if (usuarioRepository.existsById(id)) {
				usuarioRepository.deleteById(id);
				return Response.crearResponse(HttpStatus.OK, "Usuario eliminado correctamente.", null);
			} else {
				return Response.crearResponse(HttpStatus.NOT_FOUND, "No se encontró el usuario con ID " + id, null);
			}
		} catch (Exception e) {
			return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el usuario: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> listar() {
		try {
			var usuarios = usuarioRepository.findAll();
			String mensaje = usuarios.isEmpty() ? "No se encontraron usuarios." : "Usuarios listados correctamente.";
			return Response.crearResponse(HttpStatus.OK, mensaje, usuarios);
		} catch (Exception e) {
			return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al listar los usuarios: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> buscarPorId(Long id) {
		if (id == null || id <= 0) {
			return Response.crearResponse(HttpStatus.BAD_REQUEST, "ID del usuario es inválido.", null);
		}

		try {
			return usuarioRepository.findById(id)
					.map(usuario -> Response.crearResponse(HttpStatus.OK, "Usuario encontrado correctamente.", usuario))
					.orElse(Response.crearResponse(HttpStatus.NOT_FOUND, "No se encontró el usuario con ID " + id, null));
		} catch (Exception e) {
			return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar el usuario: " + e.getMessage(), null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> buscarPorUsername(String username) {
	    if (username == null || username.trim().isEmpty()) {
	        return Response.crearResponse(HttpStatus.OK, "Término de búsqueda vacío. No se encontraron usuarios.", List.of());
	    }

	    try {
	        List<Usuario> usuariosEncontrados = usuarioRepository.findByUsernameContainingIgnoreCase(username);

	        // Siempre devuelve 200, aunque no haya resultados
	        String mensaje = usuariosEncontrados.isEmpty()
	            ? "No se encontraron usuarios con ese username."
	            : "Usuarios encontrados correctamente.";

	        return Response.crearResponse(HttpStatus.OK, mensaje, usuariosEncontrados);
	    }
	    catch (Exception e) {
	        return Response.crearResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error al buscar usuarios por username: " + e.getMessage(), null);
	    }
	}


}
