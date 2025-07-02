package com.soportetecnico.utils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.soportetecnico.service.impl.UserDetailImplement;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class JwtTokenProvider {

	private final static String JWT_TOKEN_SECRETO = "aLg3eqbV2S4pZd9AFiMh4mAcRAt1Y0Jb";
	
	private final static Long JWT_TOKEN_DURACION = 3_600L;
	
	
	
	public static String crearToken(UserDetailImplement userDetail) {
		 
		long expiracionTiempo = JWT_TOKEN_DURACION * 1_000L;
		Date expiracionFecha = new Date(System.currentTimeMillis() + expiracionTiempo);
		
		Map<String , Object> map = new HashMap<>();
		map.put("role", userDetail.getRole());	
		//map.put("username", userDetail.getUsername());
		

		
		return Jwts.builder()
				.setSubject(userDetail.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(expiracionFecha)
				.addClaims(map)
				.signWith(getKey())
				.compact();
	}
	
	
	public static boolean validarToken(String token) {
        try {
            getClaims(token); // Si falla, lanza excepción
            return true;
        } catch (Exception e) {
            return false;
        }
    }
	
	private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
	
	private static Key getKey() {
		return Keys.hmacShaKeyFor(JWT_TOKEN_SECRETO.getBytes());
	}
	
	public static String obtenerUsername(String token) {
        return getClaims(token).getSubject(); // el 'sub' en el JWT
    }

    public static List<GrantedAuthority> obtenerRoles(String token) {
        String rol = (String) getClaims(token).get("role");
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
    }
	
	
	public static UsernamePasswordAuthenticationToken getauth(String token) {
		try {
			Claims claims = getClaims(token);
			
			String username = claims.getSubject();
			String role = (String) claims.get("role");
			String authority = "ROLE_" + role; 
			System.out.println("Authority: " + authority);

			
			return new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(()->authority));
			
		} catch (Exception e) {
			System.out.println("Error en el metodo {UsernamePasswordAuthenticationToken(): } " + e.getMessage());
			return null;
		} 
		
	}
	
	
	
}
