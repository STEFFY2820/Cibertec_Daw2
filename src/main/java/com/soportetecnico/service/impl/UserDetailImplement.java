package com.soportetecnico.service.impl;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.soportetecnico.model.Role;
import com.soportetecnico.model.Usuario;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDetailImplement implements UserDetails {

	@Serial
	private static final long serialVersionUID = 1L;

	private Usuario usuario;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Role rolEnum = usuario.getRole();
		
		 if (rolEnum == null) {
		        return Collections.emptyList();
		    }
		String rol = rolEnum.name();
		final String roleName = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

		return Collections.singletonList(() -> roleName);
	}



	@Override
	public String getPassword() {
		return usuario.getPassword();
	}

	@Override
	public String getUsername() {
		return usuario.getUsername();
	}

	public String getRole() {
		if (usuario.getRole().name().startsWith("ROLE_")) {
			return usuario.getRole().name().replace("ROLE_", "");
		}else {
			return usuario.getRole().name();
		}
		
	}

}
