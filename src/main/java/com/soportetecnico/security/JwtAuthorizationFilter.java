package com.soportetecnico.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soportetecnico.model.Usuario;
import com.soportetecnico.repository.UsuarioRepository;
import com.soportetecnico.utils.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter{
	
	private final UsuarioRepository usuarioRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String bearerToken = request.getHeader("Authorization");
		
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			String token = bearerToken.replace("Bearer ", "");
			
			if (JwtTokenProvider.validarToken(token)) {
				
				// 1. Obtener el email desde el token
				String username = JwtTokenProvider.obtenerUsername(token);

				// 2. Buscar el usuario completo en la BD
				Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

				if (usuario != null) {
					// 3. Obtener roles desde el token
					var authorities = JwtTokenProvider.obtenerRoles(token); // método ya implementado

					// ✅ 4. Crear el token de autenticación con el Usuario como principal
					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(usuario, null, authorities);

					// 5. Establecer autenticación en el contexto de seguridad
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}
		
		filterChain.doFilter(request, response);
	}
	
	
}
