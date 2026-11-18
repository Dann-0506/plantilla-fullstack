package com.plantilla.ping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Prueba de integración: levanta el backend completo contra un PostgreSQL real
 * en un contenedor. Flyway aplica las mismas migraciones de db/migration y
 * Hibernate valida las entidades contra ese esquema.
 *
 * <p>Requiere Docker. Si Docker no está disponible, la prueba se omite.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {
        // @ServiceConnection reemplaza URL, usuario y contraseña con los del
        // contenedor; estos valores solo evitan que falten las variables.
        "POSTGRES_USER=prueba",
        "POSTGRES_PASSWORD=prueba"
})
class PingIntegracionTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private PingService pingService;

    @Autowired
    private PingRepository pingRepository;

    @Test
    void registrarYConsultar_usaElEsquemaDeFlyway() {
        PingResponse creado = pingService.registrar(new PingRequest("desde la prueba"));

        assertThat(creado.id()).isNotNull();
        assertThat(creado.creadoEn()).as("lo asigna DEFAULT now()").isNotNull();

        PingResumen resumen = pingService.resumen();
        assertThat(resumen.total()).isGreaterThanOrEqualTo(1);
        assertThat(resumen.ultimos()).extracting(PingResponse::mensaje).contains("desde la prueba");
        assertThat(resumen.horaBaseDeDatos()).isNotNull();
    }

    @Test
    void laBaseRechazaMensajesMayoresA140() {
        // Segunda línea de defensa: aunque se omita la validación del DTO,
        // la restricción VARCHAR(140) de la tabla impide guardar el dato.
        assertThatThrownBy(() -> pingRepository.saveAndFlush(new Ping("x".repeat(141))))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
