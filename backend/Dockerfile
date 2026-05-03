# Stage 1: Build
FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

# 1. Copiamos el archivo de configuración global de la raíz
COPY settings.gradle ./

# 2. Copiamos el ejecutable de Gradle y su carpeta interna desde la raíz
COPY gradlew ./
COPY gradle ./gradle

# 3. Copiamos los archivos específicos del módulo backend
# Usamos el prefijo "backend/" porque el contexto de Docker es la raíz del proyecto
COPY backend/build.gradle ./backend/
COPY backend/src ./backend/src

# 4. Damos permisos y compilamos apuntando al proyecto backend
RUN chmod +x ./gradlew
# Ejecutamos el build específicamente para el proyecto backend
RUN ./gradlew :backend:build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 5. Copiamos el JAR generado.
# Nota: La ruta de salida suele ser backend/build/libs/ si se compila desde la raíz
COPY --from=builder /app/backend/build/libs/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]