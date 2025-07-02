package com.soportetecnico.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
	
	@Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos (URL de Angular)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cabeceras permitidas
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // Permitir credenciales (para JWT en cookies o sesiones, y buena práctica en general)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todas las rutas de la aplicación
        source.registerCorsConfiguration("/**", configuration);
        return  source;
    }
	 
   /* @Bean
    WebMvcConfigurer corsConfigurer() {
		
		return new WebMvcConfigurer() {
			
			public void addCorsMappings(CorsRegistry registry) {

				registry.addMapping("/api/usuarios/auth/login")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods("*")
				.exposedHeaders("*");
				
				registry.addMapping("/api/**")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods("*");
				
			}
			
			
		};
		
	}
	*/
}
