# FlyTrack - Pipeline CI/CD

## 🎯 Descripción Rápida

Este proyecto implementa un pipeline completo de **Integración Continua y Despliegue Continuo (CI/CD)** para la aplicación FlyTrack usando:

- **GitHub Actions** para automatización
- **Docker** para contenedores
- **JUnit** para tests unitarios
- **SonarQube** para análisis de código (opcional)
- **Kubernetes** para orquestación (preparado)

---

## 🚀 Inicio Rápido

### 1. Clonar y preparar el proyecto

```bash
git clone https://github.com/tu-usuario/FlyTrack.git
cd FlyTrack
```

### 2. Crear las ramas necesarias

```bash
# Si no existen
git checkout -b tests
git checkout -b main
```

### 3. Desarrollo Local

**Windows** (PowerShell):
```powershell
.\scripts\flytrack.ps1 dev:up    # Levanta MariaDB
.\scripts\flytrack.ps1 build     # Compila
.\scripts\flytrack.ps1 test      # Tests
.\scripts\flytrack.ps1 run       # Ejecuta app
```

**Linux/macOS** (Bash):
```bash
chmod +x ./scripts/flytrack.sh
./scripts/flytrack.sh dev:up     # Levanta MariaDB
./scripts/flytrack.sh build      # Compila
./scripts/flytrack.sh test       # Tests
./scripts/flytrack.sh run        # Ejecuta app
```

### 4. Hacer cambios y commit

```bash
git add .
git commit -m "Descripción del cambio"
git push origin tests     # Para staging
# o
git push origin main      # Para producción
```

---

## 📋 Ficheros Principales

| Archivo | Propósito |
|---------|-----------|
| `.github/workflows/ci-cd-pipeline.yml` | Pipeline de CI/CD |
| `Backend/Dockerfile` | Construcción de imagen Docker |
| `Backend/compose.yaml` | Desarrollo local (MariaDB) |
| `docker-compose.staging.yml` | Staging con Docker Compose |
| `Backend/src/main/resources/application-*.properties` | Configuración por ambiente |
| `scripts/flytrack.ps1` | Helper script (Windows) |
| `scripts/flytrack.sh` | Helper script (Linux/macOS) |
| `DEVOPS_PIPELINE.md` | Documentación completa |

---

## 🔄 Comportamiento por Rama

### 🧪 Rama: `tests` (STAGING)
- ✅ Compila automáticamente
- ✅ Ejecuta tests
- ✅ Construye imagen Docker
- ✅ **Despliega automáticamente a Staging**

### 🎯 Rama: `main` (PRODUCCIÓN)
- ✅ Compila automáticamente
- ✅ Ejecuta tests
- ✅ Construye imagen Docker
- ✅ **Requiere aprobación manual para producción**

---

## 🐳 Comandos Docker Útiles

```bash
# Construir imagen
docker build -t flytrack:local Backend/

# Ejecutar contenedor
docker run -p 8080:8080 flytrack:local

# Con Docker Compose (Staging)
docker-compose -f docker-compose.staging.yml up -d

# Ver logs
docker-compose logs -f

# Detener todo
docker-compose down
```

---

## 📊 Monitoreo del Pipeline

1. Ve a GitHub → Tu repositorio
2. Pestaña "Actions"
3. Selecciona el workflow "CI/CD Pipeline - FlyTrack"
4. Abre la última ejecución para ver logs

---

## 🆘 Solución Rápida de Problemas

### Docker no inicia MariaDB
```powershell
# Windows PowerShell
docker ps -a  # Ver contenedores
docker logs mariadb  # Ver errores
docker system prune -a  # Limpiar
```

### Tests fallan
```bash
cd Backend
./gradlew clean test --info  # Info detallada
```

### Push bloqueado
```bash
git config user.name "Tu Nombre"
git config user.email "tu@email.com"
git push -u origin tests
```

---

## 📚 Documentación Completa

Ver: [`DEVOPS_PIPELINE.md`](DEVOPS_PIPELINE.md)

---

## ✅ Checklist de Configuración

- [ ] Crear ramas `tests` y `main` en GitHub
- [ ] Hacer push de código
- [ ] Verificar que Actions está habilitado
- [ ] Ver ejecución del pipeline en Actions
- [ ] Revisar logs si hay errores

---

## 🔗 Enlaces Útiles

- [GitHub Actions](https://github.com/features/actions)
- [Docker Documentation](https://docs.docker.com/)
- [Spring Boot on Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [JUnit 5](https://junit.org/junit5/)

---

**¿Necesitas ayuda?** Lee [`DEVOPS_PIPELINE.md`](DEVOPS_PIPELINE.md) para información detallada.

