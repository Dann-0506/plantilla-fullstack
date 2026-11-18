package com.plantilla.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS global.
 *
 * <p>El frontend y el backend corren en orígenes distintos (por ejemplo,
 * localhost:5173 y localhost:8080), así que el navegador solo permite las
 * peticiones si el backend autoriza explícitamente el origen del frontend.
 *
 * <p>Los orígenes vienen de la variable de entorno CORS_ORIGENES_PERMITIDOS
 * (lista separada por comas). Nunca use "*" con una API que maneje sesiones.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origenesPermitidos;

    public CorsConfig(@Value("${app.cors.origenes-permitidos}") String[] origenesPermitidos) {
        this.origenesPermitidos = origenesPermitidos;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(origenesPermitidos)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
