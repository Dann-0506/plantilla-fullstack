package com.plantilla.ping;

import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para {@code POST /api/v1/pings}.
 *
 * <p>Es un {@code record}: inmutable y sin getters ni setters escritos a mano.
 * Las anotaciones de validación se revisan en el controlador gracias a
 * {@code @Valid}; si fallan, el manejador global responde 400.
 *
 * @param mensaje texto opcional de hasta 140 caracteres
 */
public record PingRequest(
        @Size(max = 140, message = "El mensaje no puede superar 140 caracteres.")
        String mensaje) {
}
