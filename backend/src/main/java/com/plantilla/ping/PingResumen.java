package com.plantilla.ping;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Respuesta de {@code GET /api/v1/pings}.
 *
 * @param ultimos         los 20 pings más recientes
 * @param total           número total de pings guardados
 * @param horaBaseDeDatos hora actual según el servidor de PostgreSQL
 */
public record PingResumen(List<PingResponse> ultimos, long total, OffsetDateTime horaBaseDeDatos) {
}
