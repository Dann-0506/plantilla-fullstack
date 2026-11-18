# Base de datos

Esta carpeta reúne lo necesario para **operar** la base: conectarse, respaldar
y restaurar.

## Dónde está el esquema

> **El esquema de la base de datos está en
> [`backend/src/main/resources/db/migration`](../backend/src/main/resources/db/migration).**

Está dentro del backend porque es el backend quien lo aplica y lo valida:
Flyway ejecuta las migraciones al arrancar y Hibernate comprueba que las
entidades coincidan con las tablas. Además, así se empaquetan junto con el
código y funcionan igual desde Maven, el IDE, Docker y las pruebas.

## Migraciones

Flyway aplica estos archivos al arrancar el backend, en orden y una sola vez
cada uno. El registro de lo aplicado queda en la tabla `flyway_schema_history`.

- Nombre: `V<número>__<descripción>.sql` (dos guiones bajos), por ejemplo
  `V2__crear_productos.sql`.
- **Nunca edite una migración ya aplicada.** Flyway detecta el cambio y el
  backend no arranca. Los cambios se hacen con una migración nueva.
- Comente cada restricción (`NOT NULL`, `CHECK`, `UNIQUE`, `FOREIGN KEY`) con
  la regla de negocio que protege, como en `V1__crear_pings.sql`.
- Las migraciones forman parte del backend: después de agregar una, ejecute
  `docker compose up -d --build backend`.

Para empezar de cero durante el desarrollo (se pierden todos los datos):

```bash
docker compose down -v
docker compose up -d --build
```

## Respaldos (`respaldos/`)

Los respaldos se hacen manualmente. Los archivos generados no se suben a Git
porque pueden contener datos reales.

Los comandos funcionan igual en Linux, macOS y Windows (PowerShell), porque el
archivo se genera dentro del contenedor y luego se copia.

### Crear un respaldo

```bash
docker compose exec db sh -c 'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc -f /tmp/respaldo.dump'
docker compose cp db:/tmp/respaldo.dump ./database/respaldos/respaldo_2026-11-08.dump
```

### Restaurar un respaldo

```bash
docker compose cp ./database/respaldos/respaldo_2026-11-08.dump db:/tmp/respaldo.dump
docker compose stop backend
docker compose exec db sh -c 'pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" --clean --if-exists /tmp/respaldo.dump'
docker compose start backend
```

El respaldo incluye la tabla `flyway_schema_history`, así que al arrancar,
Flyway reconoce las migraciones ya aplicadas y solo ejecuta las posteriores al
respaldo.

> **Advertencia:** `--clean` borra los objetos actuales antes de restaurar. Todo
> lo que se haya guardado después del respaldo se pierde.

## Conectarse con pgAdmin o DBeaver

| Campo | Valor |
|---|---|
| Host | `localhost` |
| Puerto | `DB_HOST_PORT` del `.env` (5432 por defecto) |
| Base de datos | `POSTGRES_DB` |
| Usuario y contraseña | `POSTGRES_USER` y `POSTGRES_PASSWORD` |

El puerto solo se publica en `127.0.0.1`: otras computadoras de la red no
pueden conectarse.
