package com.plantilla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend.
 *
 * <p>Spring Boot busca componentes (controladores, servicios, repositorios y
 * configuración) en este paquete y en todos sus subpaquetes. Por eso cada
 * funcionalidad nueva se crea como un subpaquete de {@code com.plantilla}.
 */
@SpringBootApplication
public class PlantillaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlantillaApplication.class, args);
    }
}
