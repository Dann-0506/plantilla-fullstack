package com.plantilla.ping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.Generated;

/**
 * Entidad JPA que representa una fila de la tabla {@code pings}.
 *
 * <p>La tabla la crea Flyway (src/main/resources/db/migration/V1__crear_pings.sql), no
 * Hibernate. Con {@code ddl-auto=validate}, si estos campos no coinciden con el
 * esquema real, el backend no arranca.
 *
 * <p>La entidad nunca sale de la API: el controlador responde con
 * {@link PingResponse}.
 */
@Entity
@Table(name = "pings")
public class Ping {

    /** Lo asigna PostgreSQL (columna IDENTITY). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mensaje opcional. Su límite de 140 caracteres también existe en la BD. */
    @Column(length = 140)
    private String mensaje;

    /**
     * Lo completa PostgreSQL con {@code DEFAULT now()}. {@code @Generated} le
     * indica a Hibernate que no envíe este valor en el INSERT y que lo lea de
     * vuelta después de guardar.
     */
    @Generated
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    /** Constructor vacío requerido por JPA. */
    protected Ping() {
    }

    public Ping(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getId() {
        return id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }
}
