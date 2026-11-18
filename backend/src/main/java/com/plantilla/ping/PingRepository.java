package com.plantilla.ping;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla {@code pings}.
 *
 * <p>Spring Data JPA genera la implementación en tiempo de ejecución:
 * {@code save}, {@code count} y {@code findById} vienen heredados de
 * {@link JpaRepository}, y las consultas como {@link #findTop20ByOrderByCreadoEnDescIdDesc()}
 * se deducen del nombre del método.
 *
 * <p>Regla de la plantilla: otra funcionalidad puede usar {@link PingService},
 * pero nunca este repositorio directamente.
 */
public interface PingRepository extends JpaRepository<Ping, Long> {

    /** Equivale a: SELECT * FROM pings ORDER BY creado_en DESC, id DESC LIMIT 20. */
    List<Ping> findTop20ByOrderByCreadoEnDescIdDesc();
}
