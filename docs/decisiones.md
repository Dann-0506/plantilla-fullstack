# Decisiones de diseño

Resumen de las decisiones de la plantilla. La justificación completa está en la
sección 3.4 del manual.

| # | Decisión | Alternativas descartadas | Motivo principal |
|---|---|---|---|
| 1 | Monorrepositorio con `backend/`, `frontend/` y `database/` | Repositorios separados; herramientas de workspaces | Se clona una vez y Compose ve todo desde la raíz |
| 2 | Paquetes por funcionalidad, con capas dentro de cada una | Paquetes por capa técnica; hexagonal / DDD | Todo lo de un recurso está junto; crear uno nuevo es copiar la estructura de `ping/` |
| 3 | Autenticación fuera de la base; se construye en el taller | Incluirla completa; incluirla con interruptor | El alumno entiende cada pieza en lugar de heredar una caja negra |
| 4 | API REST con prefijo `/api/v1` escrito en el controlador | GraphQL, gRPC; versionado nativo de Spring Framework 7 | REST es lo más extendido; el prefijo explícito es lo más fácil de leer |
| 5 | DTOs como `record`, entrada y salida separadas, mapeo manual | Exponer entidades; MapStruct | No filtra datos internos y se lee sin herramientas extra |
| 6 | Errores con Problem Details (RFC 9457) y un manejador global | Manejo por controlador; formato propio | Formato estándar y sin trazas internas expuestas |
| 7 | Flyway con Hibernate en modo `validate` | `ddl-auto=update`; Liquibase | El alumno escribe su DDL en SQL y el esquema queda versionado |
| 8 | Migraciones en `backend/src/main/resources/db/migration` | Carpeta `/database` en la raíz montada como volumen | Ubicación por defecto de Flyway: sin configuración, funciona igual en Maven, IDE, Docker y pruebas, y la imagen queda autocontenida |
| 9 | PostgreSQL 16 con la imagen oficial | SQLite, MongoDB; imagen personalizada | Modelo relacional completo; personalizar solo cuando haga falta |
| 10 | TanStack Query para datos del servidor; Zustand para estado de la interfaz | Un solo almacén global | Caché y actualización de datos remotos resueltas sin duplicarlos |
| 11 | Java 25 (LTS), Maven 3.9 y Node.js 26 (LTS) | Java 21; Java 27 y otras versiones sin soporte extendido; Maven 3.10, con cambios incompatibles respecto a la 3.9; Maven 4 (versión candidata, no estable) | Versiones con soporte extendido y una línea de Maven madura y ampliamente probada |
| 12 | Puerto de PostgreSQL publicado solo en `127.0.0.1` | No publicarlo; publicarlo en todas las interfaces | Permite usar pgAdmin o DBeaver sin exponer la base a la red |
| 13 | Docker como entorno de referencia y mise como entorno de trabajo | Solo Docker; instalación manual; SDKMAN + nvm; Dev Containers | Docker garantiza que la aplicación corra igual en cualquier equipo; mise hace práctico desarrollarla: mismas versiones que los Dockerfiles, carga del `.env` y tareas con un solo archivo |
| 14 | Spring Boot DevTools en el backend | Reiniciar manualmente; recompilar la imagen de Docker en cada cambio | Reinicio automático en segundos al recompilar; se desactiva solo al ejecutar el `.jar` empaquetado |

## Pendientes de confirmar

- Decisión 3 frente al objetivo de "aplicaciones web seguras" de la propuesta
  de servicio social (confirmar con el asesor).
