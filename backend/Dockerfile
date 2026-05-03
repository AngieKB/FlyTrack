# Stage 1: Build
FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY build.gradle ./
# Eliminamos settings.gradle y los archivos de gradle wrapper de aquí
# porque ahora Docker los tomará desde la raíz del proyecto
COPY src ./src

# Compilar y generar el JAR
RUN chmod +x ./gradlew && ./gradlew build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]