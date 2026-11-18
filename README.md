# Plantilla fullstack: Spring Boot + React + PostgreSQL

Entorno de desarrollo ya integrado para construir aplicaciones web con base de
datos. La plantilla resuelve la configuración (contenedores, conexión a la base,
migraciones, CORS, manejo de errores, pruebas) para que el tiempo se dedique al
diseño de los datos y a las reglas de negocio del proyecto.

Incluye un único ejemplo, el **ping con bitácora**, que recorre las tres capas.
Sirve como modelo para crear las funcionalidades propias y se elimina cuando ya
no hace falta.

| Capa | Tecnologías |
|---|---|
| Frontend | React 19, TypeScript, Tailwind CSS, TanStack Query, Zustand |
| Backend | Java 25, Spring Boot 4, Spring Data JPA, Jakarta Bean Validation |
| Base de datos | PostgreSQL 16, Flyway |
| Desarrollo y pruebas | Docker Compose, mise, Maven, npm, Vite, Swagger UI, JUnit, Mockito, Testcontainers, Vitest |

---

## 1. Requisitos

**Para ejecutar la plantilla** basta con Docker:

| Herramienta | Verificar con |
|---|---|
| Docker Desktop (o Docker Engine con Compose v2) | `docker compose version` |
| Git | `git --version` |

**Para desarrollar** se necesitan además Java 25, Maven 3.9 y Node.js 26 en su
equipo. La forma recomendada de instalarlos es [mise](https://mise.jdx.dev):
con el archivo `mise.toml` del repositorio, `mise install` instala esas
versiones exactas solo para este proyecto.

## 2. Inicio rápido

```bash
git clone <url-del-repositorio> plantilla
cd plantilla
cp .env.example .env          # En PowerShell: Copy-Item .env.example .env
docker compose up -d --build
```

La primera vez tarda varios minutos porque descarga imágenes y dependencias.

### Qué debería ver

```bash
docker compose ps
```

Los tres servicios deben aparecer en ejecución y `db` como `healthy`.

| Dirección | Qué es |
|---|---|
| http://localhost:5173 | La aplicación. Debe mostrar **Conectado** y la hora de la base de datos |
| http://localhost:8080/swagger-ui.html | Documentación interactiva de la API |
| http://localhost:8080/api/v1/pings | La API en JSON |

Prueba desde la terminal:

```bash
curl -X POST http://localhost:8080/api/v1/pings \
     -H "Content-Type: application/json" \
     -d '{"mensaje":"Hola"}'
```

Respuesta esperada (`201 Created`):

```json
{ "id": 1, "mensaje": "Hola", "creadoEn": "2026-11-08T18:04:12.345678Z" }
```

## 3. Formas de trabajo

La plantilla usa dos herramientas con propósitos distintos:

- **Docker** es el entorno de referencia: ejecuta la aplicación completa igual
  en cualquier equipo, sin instalar Java ni Node.js.
- **mise** es el entorno de trabajo: instala en su equipo las mismas versiones
  que usan los contenedores, para editar, ejecutar, probar y depurar con rapidez.

| Situación | Cómo trabajar |
|---|---|
| Primer contacto con la plantilla | Todo en Docker |
| Programar el backend | Backend con mise y base de datos en Docker |
| Programar el frontend | Con mise o en Docker; ambos recargan al guardar |
| Que el editor entienda el código | mise (JDK y `node_modules` en su equipo) |
| Ejecutar las pruebas | mise; la prueba de integración usa Docker |
| Comprobar que todo funciona antes de entregar | Todo en Docker |
| Revisar el proyecto de otra persona | Todo en Docker |

### A. Todo en Docker

Es el [inicio rápido](#2-inicio-rápido). El frontend recarga solo al guardar,
porque su código está montado en el contenedor. El backend, en cambio, se
compila al construir su imagen: cada cambio en Java o en una migración requiere
`docker compose up -d --build backend`. Por eso, para programar el backend
conviene la forma B.

### B. Desarrollo diario con mise

```bash
mise install              # una sola vez: Java, Maven y Node.js
mise run instalar         # dependencias del frontend
mise run bd               # PostgreSQL en Docker

mise run backend          # terminal 1: backend en http://localhost:8080
mise run frontend         # terminal 2: frontend en http://localhost:5173
```

- mise carga las variables del `.env` en sus tareas, así que el backend se
  conecta a la base sin configurar nada más.
- El backend incluye **Spring Boot DevTools**: al recompilar (al guardar en el
  IDE, o con `mvn compile` en otra terminal), la aplicación se reinicia sola
  en uno o dos segundos.
- Si el entorno completo de Docker está corriendo, detenga primero su backend
  para liberar el puerto: `docker compose stop backend`.
- El frontend también puede seguir en Docker (`docker compose up -d db frontend`)
  mientras el backend corre con mise.
- Para ejecutar o depurar el backend desde el IDE, la configuración de
  ejecución necesita las variables del `.env` (por ejemplo, `POSTGRES_USER` y
  `POSTGRES_PASSWORD`).

`mise tasks` muestra todas las tareas disponibles.

## 4. Estructura del repositorio

```
plantilla/
├── backend/                     Spring Boot (Java 25)
│   └── src/main/
│       ├── java/com/plantilla/
│       │   ├── ping/            Funcionalidad de ejemplo (todas sus capas)
│       │   │   ├── PingController.java     Endpoints HTTP
│       │   │   ├── PingService.java        Lógica
│       │   │   ├── PingRepository.java     Acceso a datos (Spring Data JPA)
│       │   │   ├── Ping.java               Entidad JPA
│       │   │   ├── PingRequest.java        DTO de entrada (record)
│       │   │   └── PingResponse.java       DTO de salida (record)
│       │   └── shared/          Código transversal
│       │       ├── config/      CORS y Swagger UI
│       │       └── exception/   Manejador global de errores (Problem Details)
│       └── resources/
│           ├── application.properties
│           └── db/migration/    ★ ESQUEMA DE LA BASE DE DATOS (Flyway)
├── frontend/                    React + TypeScript + Vite
│   └── src/
│       ├── features/ping/       Componentes y hooks del ejemplo
│       └── lib/                 Cliente HTTP, TanStack Query y Zustand
├── database/
│   ├── README.md                Conexión, respaldos y restauración
│   └── respaldos/               Destino de los respaldos (no se suben a Git)
├── docs/                        Decisiones de diseño y modelo de datos
├── docker-compose.yml           Entorno completo (base de datos, backend y frontend)
├── mise.toml                    Versiones, carga del .env y tareas de desarrollo
└── .env.example                 Plantilla de variables de entorno
```

> El esquema de la base de datos vive en
> `backend/src/main/resources/db/migration`. Ahí se define el modelo de datos
> de su proyecto, con un archivo SQL por cada cambio. Está dentro del backend
> porque es el backend quien lo aplica (Flyway) y lo valida (Hibernate).

## 5. Recorrido de una petición

Al enviar el formulario de la página:

1. `PingForm` llama al hook `useRegistrarPing` (TanStack Query), que usa el
   cliente HTTP de `lib/httpClient.ts`.
2. El navegador hace primero una petición `OPTIONS` (verificación de CORS) y
   después el `POST /api/v1/pings` al backend.
3. `PingController` convierte el JSON en `PingRequest` y lo valida.
4. `PingService` crea la entidad y la guarda.
5. `PingRepository` (Hibernate) envía el `INSERT` a PostgreSQL.
6. PostgreSQL asigna `id` y `creado_en` y aplica las restricciones.
7. El backend responde `201 Created` con un `PingResponse`.
8. El hook invalida la consulta `['pings']` y el listado se actualiza.

Si el mensaje supera 140 caracteres, el paso 3 responde `400` con un cuerpo
Problem Details y la página muestra el detalle del error.

## 6. Cómo agregar una funcionalidad propia

Use el ejemplo `ping` como modelo. Para un recurso `producto`:

1. **Base de datos.** Cree
   `backend/src/main/resources/db/migration/V2__crear_productos.sql` con su
   `CREATE TABLE`. Nunca edite una migración que ya se aplicó.
2. **Backend.** Cree el paquete `com.plantilla.producto` con `Producto`
   (entidad), `ProductoRepository`, `ProductoService`, `ProductoController` y
   sus DTOs `ProductoRequest` y `ProductoResponse`. Ruta: `/api/v1/productos`.
3. **Frontend.** Cree `src/features/producto/` con un hook `useProductos.ts`
   y sus componentes, y agréguelos en `App.tsx`.
4. **Aplique los cambios.** Con mise, basta con recompilar: DevTools reinicia
   el backend y Flyway ejecuta la migración nueva. Con todo en Docker, ejecute
   `docker compose up -d --build backend`.

Regla entre funcionalidades: una puede llamar al **servicio** de otra, nunca a
su **repositorio**.

Cuando el ejemplo ya no sirva, borre `backend/.../ping/`,
`frontend/src/features/ping/`, sus usos en `App.tsx`, y agregue una migración
`DROP TABLE pings;`.

## 7. Pruebas

```bash
mise run pruebas              # todas
mise run pruebas-backend      # mvn test
mise run pruebas-frontend     # npm test
```

La prueba de integración del backend usa Docker (Testcontainers) y se omite si
Docker no está disponible.

| Prueba | Tipo | Qué verifica |
|---|---|---|
| `PingServiceTest` | Unitaria (JUnit + Mockito) | La lógica del servicio, sin base de datos |
| `PingIntegracionTest` | Integración (Testcontainers) | Migraciones, validación del esquema y restricciones en un PostgreSQL real |
| `PingList.test.tsx`, `PingForm.test.tsx` | Componente (Vitest + Testing Library) | La vista con respuestas simuladas del backend |

## 8. Comandos útiles

| Comando | Para qué |
|---|---|
| `mise tasks` | Lista las tareas de desarrollo |
| `mise run verificar` | Construye y levanta todo en Docker, como lo ejecutaría otra persona |
| `docker compose ps` | Estado de los servicios |
| `docker compose logs -f backend` | Ver el log del backend en Docker |
| `docker compose up -d --build backend` | Reconstruir el backend en Docker tras un cambio |
| `docker compose stop backend` | Liberar el puerto 8080 para ejecutar el backend con mise |
| `docker compose exec db psql -U plantilla -d plantilla` | Consola SQL dentro del contenedor |
| `docker compose down` | Detener, **conservando** los datos |
| `docker compose down -v` | Detener y **borrar** la base de datos |

Respaldos y restauración: ver [`database/README.md`](database/README.md).

## 9. Errores comunes

| Síntoma | Causa y solución |
|---|---|
| `required variable POSTGRES_USER is missing a value` | Falta el archivo `.env`. Copie `.env.example` a `.env`. |
| `port is already allocated` / `address already in use` | Otro programa usa el puerto (5432, 8080 o 5173). Cambie `DB_HOST_PORT`, `BACKEND_HOST_PORT` o `FRONTEND_HOST_PORT` en `.env`. Si el backend de Docker ocupa el 8080, deténgalo con `docker compose stop backend`. |
| El editor marca como error los imports de React o de Spring | Faltan las herramientas en su equipo. Ejecute `mise install` y `mise run instalar`. |
| `mvn` o `npm` no se reconocen como comando | Ejecute `mise install`, o use las tareas con `mise run`. |
| El backend con mise dice `Connection refused` | La base no está levantada. Ejecute `mise run bd`. |
| La página dice "Sin conexión con el backend" y la consola del navegador menciona CORS | El origen del frontend no está en `CORS_ORIGENES_PERMITIDOS`. Corríjalo en `.env` y reinicie el backend. |
| El backend se reinicia y el log dice `password authentication failed` | Cambió la contraseña en `.env` después de crear la base. La base conserva la anterior: restaure el valor o borre los datos con `docker compose down -v`. |
| `Validate failed: Migrations have failed validation` / `checksum mismatch` | Se editó una migración ya aplicada. Revierta el cambio y créelo como una migración nueva. |
| Agregué una migración y la tabla no aparece | El backend no se recompiló. Con mise, recompile; con Docker, ejecute `docker compose up -d --build backend`. |
| `Schema-validation: missing column` o `wrong column type` | La entidad JPA no coincide con la tabla. Revise la migración y la entidad. |
| Agregué una dependencia al frontend y el contenedor no la encuentra | `node_modules` vive en un volumen que conserva la versión anterior. Ejecute `docker compose up -d --build -V frontend`. |
| El frontend en Docker no recarga al guardar (Windows/macOS) | Ponga `VITE_WATCH_POLLING=true` en `.env` y ejecute `docker compose up -d frontend`. |
| Desaparecieron todos los datos | Se usó `docker compose down -v`, que elimina el volumen. Restaure un respaldo. |

## 10. Qué no incluye la plantilla

A propósito, la base no trae autenticación. Se construye en el taller práctico
del manual:

1. CRUD completo de usuarios.
2. Registro e inicio de sesión (Spring Security, BCrypt, JWT y rutas protegidas).
3. Roles y autorización.

También quedan fuera los refresh tokens, la recuperación de contraseña, la
verificación de correo, la integración continua y el despliegue a producción.
Las razones de cada decisión están en [`docs/decisiones.md`](docs/decisiones.md).
