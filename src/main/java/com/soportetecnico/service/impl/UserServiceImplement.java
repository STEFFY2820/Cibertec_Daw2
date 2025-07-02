package com.soportetecnico.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soportetecnico.model.Usuario;
import com.soportetecnico.repository.UsuarioRepository;

@Service
public class UserServiceImplement implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow( () -> new UsernameNotFoundException("El usuario no se ha encontrado con el email: " + username) );
		
		return new UserDetailImplement(usuario);
	}

	
	
}
