package com.soportetecnico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.soportetecnico.security.JwtAuthenticationFilter;
import com.soportetecnico.security.JwtAuthorizationFilter;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

	private final UserDetailsService userDetailsService;
	private final JwtAuthorizationFilter jwtAuthorizationFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter();
		jwtAuthenticationFilter.setAuthenticationManager(authManager);
		jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");

		return http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.OPTIONS).permitAll() 
				.requestMatchers("/auth/**").permitAll() // Rutas abiertas (login, registra)
						
						// --- REGLAS PARA TICKETS ---
		                .requestMatchers(HttpMethod.POST, "/api/tickets").hasRole("USUARIO")
		                .requestMatchers(HttpMethod.GET, "/api/tickets/mytickets").hasRole("USUARIO")
		                .requestMatchers(HttpMethod.GET, "/api/tickets").hasAnyRole("ADMIN", "SOPORTE")
		                .requestMatchers(HttpMethod.GET, "/api/tickets/exportar-pdf").hasAnyRole("ADMIN", "SOPORTE")//1
		                .requestMatchers(HttpMethod.PUT, "/api/tickets/{id}/status").hasAnyRole("ADMIN", "SOPORTE")
		                .requestMatchers("/api/tickets/**").authenticated()
		                // --- REGLAS PARA TEST
		                .requestMatchers(HttpMethod.GET, "/api/test/**").hasRole("ADMIN")
		               
		                // --- REGLAS PARA USUARIOS ---
		                .requestMatchers("*", "/api/usuarios/**").hasRole("ADMIN")
						
						.anyRequest().authenticated()
				)
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilter(jwtAuthenticationFilter)
				.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
				.build();

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
		authManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		return authManagerBuilder.build();
	}

}
