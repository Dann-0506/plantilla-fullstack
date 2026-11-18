package com.plantilla.ping;

import java.time.OffsetDateTime;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/**
 * Consulta la hora del servidor de PostgreSQL.
 *
 * <p>No toda consulta necesita una entidad: para un valor suelto basta con SQL
 * directo mediante {@link JdbcClient}. Que esta consulta responda confirma
 * que el backend realmente llega a la base de datos.
 */
@Repository
public class HoraBaseDeDatosRepository {

    private final JdbcClient jdbc;

    public HoraBaseDeDatosRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public OffsetDateTime ahora() {
        return jdbc.sql("SELECT now()").query(OffsetDateTime.class).single();
    }
}
