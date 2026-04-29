#!/bin/bash

# Script para gestionar el ambiente FlyTrack
# Uso: ./scripts/flytrack.sh [comando]

set -e

SCRIPT_DIR="${BASH_SOURCE%/*}"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/Backend"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones auxiliares
info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

success() {
    echo -e "${GREEN}✅ $1${NC}"
}

warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

error() {
    echo -e "${RED}❌ $1${NC}"
}

# Comandos disponibles
build() {
    info "Compilando proyecto..."
    cd "$BACKEND_DIR"
    ./gradlew clean build -x test
    success "Compilación completada"
}

test() {
    info "Ejecutando tests..."
    cd "$BACKEND_DIR"
    ./gradlew test
    success "Tests completados"
}

docker_build() {
    info "Construyendo imagen Docker..."
    cd "$BACKEND_DIR"
    docker build -t flytrack:local .
    success "Imagen Docker construida: flytrack:local"
}

dev_up() {
    info "Levantando ambiente de desarrollo..."
    cd "$BACKEND_DIR"
    docker-compose up -d
    sleep 5
    success "Ambiente de desarrollo levantado"
    docker-compose ps
}

dev_down() {
    info "Deteniendo ambiente de desarrollo..."
    cd "$BACKEND_DIR"
    docker-compose down
    success "Ambiente de desarrollo detenido"
}

dev_logs() {
    info "Mostrando logs de desarrollo..."
    cd "$BACKEND_DIR"
    docker-compose logs -f
}

staging_up() {
    info "Levantando ambiente de staging..."
    cd "$PROJECT_ROOT"
    docker-compose -f docker-compose.staging.yml up -d
    sleep 5
    success "Ambiente de staging levantado"
    docker-compose -f docker-compose.staging.yml ps
}

staging_down() {
    info "Deteniendo ambiente de staging..."
    cd "$PROJECT_ROOT"
    docker-compose -f docker-compose.staging.yml down
    success "Ambiente de staging detenido"
}

staging_logs() {
    info "Mostrando logs de staging..."
    cd "$PROJECT_ROOT"
    docker-compose -f docker-compose.staging.yml logs -f
}

run() {
    info "Ejecutando aplicación en modo desarrollo..."
    cd "$BACKEND_DIR"
    ./gradlew bootRun
}

clean() {
    info "Limpiando proyecto..."
    cd "$BACKEND_DIR"
    ./gradlew clean
    success "Proyecto limpiado"
}

help() {
    cat << EOF
${BLUE}🚀 FlyTrack DevOps Script${NC}

Comandos disponibles:

${GREEN}Desarrollo Local:${NC}
  build           - Compila el proyecto
  test            - Ejecuta tests unitarios
  run             - Ejecuta la aplicación
  dev:up          - Levanta MariaDB (docker-compose)
  dev:down        - Detiene MariaDB
  dev:logs        - Muestra logs de desarrollo
  clean           - Limpia el proyecto

${GREEN}Docker:${NC}
  docker:build    - Construye imagen Docker local

${GREEN}Staging:${NC}
  staging:up      - Levanta ambiente de staging completo
  staging:down    - Detiene ambiente de staging
  staging:logs    - Muestra logs de staging

${GREEN}Utilidades:${NC}
  help            - Muestra esta ayuda
  status          - Muestra estado de contenedores

Ejemplos:
  ./scripts/flytrack.sh build
  ./scripts/flytrack.sh test
  ./scripts/flytrack.sh dev:up
  ./scripts/flytrack.sh run

EOF
}

status() {
    info "Estado de desarrollo:"
    docker-compose -f "$BACKEND_DIR/compose.yaml" ps

    echo ""
    info "Estado de staging:"
    docker-compose -f "$PROJECT_ROOT/docker-compose.staging.yml" ps 2>/dev/null || warning "Staging no levantado"
}

# Ejecutar comando
case "${1:-help}" in
    build)
        build
        ;;
    test)
        test
        ;;
    docker:build)
        docker_build
        ;;
    dev:up)
        dev_up
        ;;
    dev:down)
        dev_down
        ;;
    dev:logs)
        dev_logs
        ;;
    staging:up)
        staging_up
        ;;
    staging:down)
        staging_down
        ;;
    staging:logs)
        staging_logs
        ;;
    run)
        run
        ;;
    clean)
        clean
        ;;
    status)
        status
        ;;
    help|--help|-h)
        help
        ;;
    *)
        error "Comando no reconocido: $1"
        echo ""
        help
        exit 1
        ;;
esac

