-- =====================================================================
-- V1: tabla del recurso de ejemplo "ping con bitácora".
--
-- Ubicación: backend/src/main/resources/db/migration, la carpeta que Flyway
-- revisa por defecto. Se empaqueta dentro del backend, por lo que agregar
-- una migración requiere reconstruirlo (docker compose up -d --build backend).
--
-- Reglas de Flyway:
--   * El nombre sigue el patrón V<versión>__<descripción>.sql.
--   * Flyway aplica cada archivo una sola vez, en orden, y lo registra en
--     la tabla flyway_schema_history.
--   * Una migración ya aplicada NUNCA se edita (Flyway detecta el cambio
--     por su suma de verificación y el backend no arranca). Para cambiar
--     el esquema se crea una migración nueva: V2__..., V3__...
-- =====================================================================

CREATE TABLE pings (
    -- Clave primaria sustituta. GENERATED ALWAYS AS IDENTITY es la forma
    -- estándar de SQL para autoincrementar; BIGINT evita quedarse sin valores.
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    -- Opcional (admite NULL). VARCHAR(140) impone el límite también en la
    -- base, aunque la API ya lo valide: si algún día se omite la validación,
    -- la base sigue protegiendo la integridad del dato.
    mensaje     VARCHAR(140),

    -- TIMESTAMPTZ guarda un instante absoluto (con zona horaria).
    -- DEFAULT now() deja que la base asigne la fecha; NOT NULL garantiza
    -- que ninguna fila quede sin ella.
    creado_en   TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- Un mensaje presente no puede ser una cadena vacía o solo de espacios:
    -- "sin mensaje" se representa con NULL, no con ''.
    CONSTRAINT ck_pings_mensaje_no_vacio
        CHECK (mensaje IS NULL OR length(btrim(mensaje)) > 0)
);

-- La consulta del listado ordena por fecha; el índice la acelera cuando la
-- tabla crece. (Con pocas filas PostgreSQL puede preferir no usarlo.)
CREATE INDEX ix_pings_creado_en ON pings (creado_en DESC);

COMMENT ON TABLE pings IS 'Recurso de ejemplo de la plantilla. Puede eliminarse al iniciar el proyecto propio.';
