package com.plantilla.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales que muestra Swagger UI (http://localhost:8080/swagger-ui.html).
 * Los endpoints se documentan solos a partir de los controladores.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI().info(new Info()
                .title("API de la plantilla fullstack")
                .version("v1")
                .description("Recurso de ejemplo (ping con bitácora) para recorrer las tres capas."));
    }
}
