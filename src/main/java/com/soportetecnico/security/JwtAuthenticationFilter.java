package com.soportetecnico.security;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.soportetecnico.model.Auth;
import com.soportetecnico.service.impl.UserDetailImplement;
import com.soportetecnico.utils.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			// Lee las credenciales (email y password) desde el cuerpo de la petición JSON
			Auth auth = new ObjectMapper().readValue(request.getReader(), Auth.class);
			
			// Crear un token de autenticación con las credenciales recibidas, sin roles aún
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					auth.getUsername(),
					auth.getPassword(),
					Collections.emptyList());// No se pasan roles aquí porque todavía no se autentica
			
			// Delegar la autenticación al AuthenticationManager de Spring Security
			return getAuthenticationManager().authenticate(authToken);
			
		} catch (IOException e) {
		    throw new RuntimeException("Error al leer las credenciales del login", e);
		}
		
		

	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult) 
					throws IOException, ServletException {
		
		// Obtener detalles del usuario autenticado (incluye roles)
		UserDetailImplement userDetails = (UserDetailImplement) authResult.getPrincipal();
		
		// Crea token JWT con nombres, email y rol
		String token = JwtTokenProvider.crearToken(userDetails);
		
		// Agregar el token JWT al header Authorization de la respuesta
		response.addHeader("Authorization", "Bearer " + token);
		
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("token", token);
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
		
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
		
	}

}
