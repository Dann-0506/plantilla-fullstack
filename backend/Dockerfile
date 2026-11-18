# ---------- Etapa 1: compilación ----------
FROM maven:3.9-eclipse-temurin-25-alpine AS build
WORKDIR /build

# Primero solo el pom.xml: Docker reutiliza esta capa (y las dependencias
# descargadas) mientras el pom no cambie.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
# Las pruebas se ejecutan aparte con "mvn test" (las de integración usan Docker).
RUN mvn -q -B -DskipTests package

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# No ejecutar la aplicación como root.
RUN useradd --system --uid 1001 spring
USER spring

COPY --from=build /build/target/plantilla-backend.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
