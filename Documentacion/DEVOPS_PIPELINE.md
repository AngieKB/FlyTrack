# 📋 Pipeline CI/CD - FlyTrack

## 📌 Descripción General

Este documento describe el pipeline de **Integración Continua y Despliegue Continuo (CI/CD)** configurado para el proyecto **FlyTrack**.

El pipeline está diseñado para:
- ✅ **Compilar** el código automáticamente
- 🧪 **Ejecutar pruebas** unitarias con JUnit
- 📊 **Analizar calidad** de código (SonarQube - opcional)
- 🐳 **Empaquetar** la aplicación en Docker
- 🚀 **Desplegar a Staging** (rama: `tests`)
- 🎯 **Desplegar a Producción** (rama: `main`)

---

## 🔄 Flujo del Pipeline por Rama

### Rama: `tests` (STAGING)
```
┌─────────────────┐
│ Push a 'tests'  │
└────────┬────────┘
         │
         ▼
    ┌─────────┐
    │  BUILD  │
    └────┬────┘
         │
         ▼
    ┌─────────┐
    │  TESTS  │
    └────┬────┘
         │
         ▼
  ┌──────────────┐
  │ DOCKER BUILD │
  └────┬─────────┘
       │
       ▼
  ┌──────────────────┐
  │ DEPLOY STAGING   │
  └──────────────────┘
```

### Rama: `main` (PRODUCTION)
```
┌──────────────────┐
│ Push a 'main'    │
└────────┬─────────┘
         │
         ▼
    ┌─────────┐
    │  BUILD  │
    └────┬────┘
         │
         ▼
    ┌─────────┐
    │  TESTS  │
    └────┬────┘
         │
         ▼
  ┌──────────────┐
  │ DOCKER BUILD │
  └────┬─────────┘
       │
       ▼
  ┌──────────────────┐
  │ DEPLOY PROD*     │
  │ (* Manual OK)    │
  └──────────────────┘
```

---

## 📂 Estructura de Archivos

```
FlyTrack/
├── .github/
│   └── workflows/
│       └── ci-cd-pipeline.yml          # Pipeline principal
├── Backend/
│   ├── Dockerfile                       # Multi-stage Docker build
│   ├── compose.yaml                     # Desarrollo local
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-staging.properties
│   │   │       └── application-production.properties
│   │   └── test/
│   │       └── java/
│   ├── build.gradle
│   └── gradlew
├── docker-compose.staging.yml           # Staging con Docker Compose
└── DEVOPS_PIPELINE.md                   # Este archivo
```

---

## 🚀 Etapas del Pipeline

### 1️⃣ BUILD (Compilación)
- **Activador**: Push a cualquier rama (`tests` o `main`)
- **Acción**: Compila el proyecto con Gradle
- **Resultado**: Artefactos listos en `Backend/build/libs/`

**Comando manual**:
```bash
cd Backend
./gradlew clean build -x test
```

---

### 2️⃣ TEST (Pruebas Automatizadas)
- **Activador**: Después del BUILD exitoso
- **Acción**: Ejecuta todas las pruebas unitarias con JUnit
- **BD**: MariaDB en contenedor para las pruebas
- **Resultado**: Reporte de tests en `Backend/build/test-results/`

**Comando manual**:
```bash
cd Backend
./gradlew test
```

---

### 3️⃣ DOCKER BUILD (Construcción de Imagen)
- **Activador**: Después de TEST exitoso, solo en pushes (no en PRs)
- **Acción**: Construye imagen Docker multi-stage
- **Destino**: GitHub Container Registry (`ghcr.io`)
- **Tags**:
  - `tests` para rama tests
  - `main` para rama main
  - Hash del commit

**Dockerfile**:
- Etapa 1 (Builder): Compila con Gradle
- Etapa 2 (Runtime): Imagen Alpine con JRE 21

---

### 4️⃣ DEPLOY STAGING
- **Activador**: Solo en rama `tests`
- **Acción**: Despliega a ambiente de staging
- **Estado**: 🚧 Requiere configuración del entorno (ver sección "Próximos Pasos")

**Comando Docker Compose**:
```bash
docker-compose -f docker-compose.staging.yml up -d
```

---

### 5️⃣ DEPLOY PRODUCTION
- **Activador**: Solo en rama `main`
- **Acción**: Despliega a producción
- **Estado**: 🚧 Requiere configuración del entorno

---

## 🛠️ Configuración Local

### Requisitos Previos
- docker
- Docker Compose
- Java 21
- Gradle (o usar `./gradlew`)

### Ejecutar en Desarrollo

1. **Basarse en la rama `tests`**:
   ```bash
   git checkout tests
   ```

2. **Levantar MariaDB**:
   ```bash
   cd Backend
   docker-compose up -d
   ```

3. **Ejecutar la aplicación**:
   ```bash
   ./gradlew bootRun
   ```

4. **Ejecutar tests**:
   ```bash
   ./gradlew test
   ```

---

## 🐳 Docker

### Construir Imagen Localmente
```bash
cd Backend
docker build -t flytrack:local .
```

### Ejecutar Contenedor
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/flytrack_db \
  flytrack:local
```

### Con Docker Compose (Staging)
```bash
docker-compose -f docker-compose.staging.yml up
```

---

## 🔐 Secretos en GitHub Actions

Para que funcione el pipeline, necesitas configurar estos secretos en GitHub:

### Ir a: Settings → Secrets and variables → Actions

1. **GITHUB_TOKEN** (automático)
   - Se usa para pushear a Container Registry

2. **(Opcional) SONARQUBE_TOKEN**
   - Para análisis de calidad

3. **(Opcional) DEPLOY_TOKEN**
   - Para despliegue a staging/producción

---

## 📊 Análisis de Calidad de Código (SonarQube)

Actualmente está **comentado** en el pipeline. Para activarlo:

1. Configurar una instancia de SonarQube
2. Descomenta la sección `quality-analysis` en `ci-cd-pipeline.yml`
3. Configura el token de SonarQube en los secretos de GitHub
4. Actualiza la URL de SonarQube

---

## 🔄 Monitoreo del Pipeline

### Ver Estado en GitHub
1. Ve a tu repositorio
2. Click en la pestaña "Actions"
3. Selecciona el workflow "CI/CD Pipeline - FlyTrack"
4. Expande cada ejecución para ver logs detallados

### Logs Detallados
- Click en el job que quieras revisar
- Expande cada paso para ver más información

---

## 🐛 Solución de Problemas

### El pipeline falla en BUILD
```bash
# Verifica que Gradle está actualizado
cd Backend
./gradlew --version

# Limpia y reintenta
./gradlew clean build
```

### El pipeline falla en TEST
```bash
# Verifica que MariaDB está levantado en localhost:3306
docker ps | grep mariadb

# Revisa los logs de MariaDB
docker logs mariadb
```

### Docker push falla
```bash
# Verifica credenciales
docker login ghcr.io

# Usa tu token personal de GitHub
# Usuario: nombre_de_usuario
# Contraseña: tu_token_personal
```

---

## 📋 Checklist de Configuración

- [ ] Repositorio creado en GitHub
- [ ] Ramas `tests` y `main` creadas
- [ ] Workflow `.github/workflows/ci-cd-pipeline.yml` en repositorio
- [ ] Dockerfile en `Backend/`
- [ ] Docker Compose configurado
- [ ] Secretos de GitHub configurados
- [ ] SonarQube configurado (opcional)
- [ ] Ambiente de Staging listo
- [ ] Ambiente de Producción listo

---

## 🔮 Próximos Pasos

### Corto Plazo
1. Implementar despliegue a Staging (scripts/webhooks)
2. Configurar SonarQube
3. Agregar más tests

### Mediano Plazo
1. Kubernetes para orquestación de contenedores
2. Monitoring con Prometheus/Grafana
3. Logging centralizado con ELK

### Largo Plazo
1. GitOps con ArgoCD
2. Service Mesh con Istio
3. Disaster Recovery Plan

---

## 📖 Referencias

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [SonarQube Documentation](https://docs.sonarqube.org/)

---

**Última actualización**: 2026-04-28

