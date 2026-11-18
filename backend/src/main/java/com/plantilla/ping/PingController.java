package com.plantilla.ping;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints del recurso de ejemplo.
 *
 * <p>El controlador solo traduce HTTP: recibe la petición, valida el DTO y
 * delega en el servicio. No contiene lógica de negocio ni acceso a datos.
 */
@RestController
@RequestMapping("/api/v1/pings")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    /** Lectura: nunca modifica datos. */
    @GetMapping
    public PingResumen listar() {
        return pingService.resumen();
    }

    /** Escritura: {@code @Valid} dispara la validación de {@link PingRequest}. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PingResponse registrar(@Valid @RequestBody PingRequest solicitud) {
        return pingService.registrar(solicitud);
    }
}
