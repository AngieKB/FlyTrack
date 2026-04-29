.PHONY: help build test clean run dev-up dev-down docker-build docker-run staging-up staging-down logs

# Variables
PROJECT_NAME := flytrack
JAVA_VERSION := 21
DOCKER_REGISTRY := ghcr.io
DOCKER_IMAGE := $(DOCKER_REGISTRY)/$(PROJECT_NAME):local

help:
	@echo "FlyTrack Pipeline - Comandos disponibles"
	@echo ""
	@echo "Desarrollo Local:"
	@echo "  make build          - Compila el proyecto"
	@echo "  make test           - Ejecuta tests"
	@echo "  make run            - Ejecuta la aplicación"
	@echo "  make clean          - Limpia el proyecto"
	@echo ""
	@echo "Docker:"
	@echo "  make dev-up         - Levanta MariaDB"
	@echo "  make dev-down       - Detiene MariaDB"
	@echo "  make docker-build   - Construye imagen Docker"
	@echo "  make docker-run     - Ejecuta contenedor Docker"
	@echo ""
	@echo "Staging:"
	@echo "  make staging-up     - Levanta staging completo"
	@echo "  make staging-down   - Detiene staging"
	@echo ""
	@echo "Utilidades:"
	@echo "  make logs           - Muestra logs de todos"
	@echo "  make help           - Muestra esta ayuda"

build:
	@echo "Compilando proyecto..."
	@cd Backend && ./gradlew clean build -x test
	@echo "Compilación completada"

test:
	@echo "Ejecutando tests..."
	@cd Backend && ./gradlew test
	@echo "Tests completados"

clean:
	@echo "Limpiando..."
	@cd Backend && ./gradlew clean
	@echo "Limpieza completada"

run:
	@echo "Ejecutando aplicación..."
	@cd Backend && ./gradlew bootRun

dev-up:
	@echo "Levantando MariaDB..."
	@cd Backend && docker-compose up -d
	@echo "MariaDB levantado"

dev-down:
	@echo "Deteniendo MariaDB..."
	@cd Backend && docker-compose down
	@echo "MariaDB detenido"

dev-logs:
	@cd Backend && docker-compose logs -f

docker-build:
	@echo "Construyendo imagen Docker..."
	@cd Backend && docker build -t $(DOCKER_IMAGE) .
	@echo "Imagen construida: $(DOCKER_IMAGE)"

docker-run:
	@echo "Ejecutando contenedor..."
	@docker run -p 8080:8080 $(DOCKER_IMAGE)

staging-up:
	@echo "Levantando Staging..."
	@docker-compose -f docker-compose.staging.yml up -d
	@echo "Staging levantado"

staging-down:
	@echo "Deteniendo Staging..."
	@docker-compose -f docker-compose.staging.yml down
	@echo "Staging detenido"

staging-logs:
	@docker-compose -f docker-compose.staging.yml logs -f

logs:
	@echo "Logs de desarrollo:"
	@cd Backend && docker-compose logs -f mariadb &
	@echo "Ctrl+C para detener"

status:
	@echo "Estado de desarrollo:"
	@cd Backend && docker-compose ps
	@echo ""
	@echo "Estado de staging:"
	@docker-compose -f docker-compose.staging.yml ps 2>/dev/null || echo "Staging no activado"

all: clean build test docker-build
	@echo "Pipeline completado exitosamente"

.DEFAULT_GOAL := help

