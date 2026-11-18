package com.plantilla.ping;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica del recurso ping.
 *
 * <p>Esta capa no conoce HTTP: recibe y devuelve objetos Java. Por eso puede
 * probarse sin levantar un servidor (ver {@code PingServiceTest}).
 */
@Service
@Transactional(readOnly = true)
public class PingService {

    private final PingRepository pingRepository;
    private final HoraBaseDeDatosRepository horaBaseDeDatos;

    // Inyección por constructor: Spring entrega las dependencias.
    public PingService(PingRepository pingRepository, HoraBaseDeDatosRepository horaBaseDeDatos) {
        this.pingRepository = pingRepository;
        this.horaBaseDeDatos = horaBaseDeDatos;
    }

    public PingResumen resumen() {
        var ultimos = pingRepository.findTop20ByOrderByCreadoEnDescIdDesc().stream()
                .map(PingResponse::desde)
                .toList();
        return new PingResumen(ultimos, pingRepository.count(), horaBaseDeDatos.ahora());
    }

    @Transactional
    public PingResponse registrar(PingRequest solicitud) {
        var ping = new Ping(normalizar(solicitud.mensaje()));
        // saveAndFlush ejecuta el INSERT de inmediato, así la respuesta ya
        // incluye el id y la fecha que asignó PostgreSQL.
        var guardado = pingRepository.saveAndFlush(ping);
        return PingResponse.desde(guardado);
    }

    /** Un mensaje vacío o con solo espacios se guarda como NULL. */
    static String normalizar(String mensaje) {
        if (mensaje == null || mensaje.isBlank()) {
            return null;
        }
        return mensaje.strip();
    }
}
