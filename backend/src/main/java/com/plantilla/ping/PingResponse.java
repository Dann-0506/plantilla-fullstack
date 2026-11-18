package com.plantilla.ping;

import java.time.OffsetDateTime;

/**
 * DTO de salida: lo que la API devuelve por cada ping.
 *
 * <p>Separar la salida de la entidad permite decidir qué campos se exponen.
 * Por ejemplo, una entidad de usuario tendría el hash de la contraseña, pero
 * su DTO de salida nunca lo incluiría.
 */
public record PingResponse(Long id, String mensaje, OffsetDateTime creadoEn) {

    /** Mapeo manual de entidad a DTO. */
    public static PingResponse desde(Ping ping) {
        return new PingResponse(ping.getId(), ping.getMensaje(), ping.getCreadoEn());
    }
}
