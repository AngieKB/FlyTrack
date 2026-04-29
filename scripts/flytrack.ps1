# Script para gestionar el ambiente FlyTrack (Windows)
# Uso: .\scripts\flytrack.ps1 [comando]

param(
    [Parameter(Position = 0)]
    [string]$Command = "help"
)

$ErrorActionPreference = "Stop"

# Obtener rutas
$SCRIPT_DIR = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
$PROJECT_ROOT = Split-Path -Path $SCRIPT_DIR -Parent
$BACKEND_DIR = Join-Path -Path $PROJECT_ROOT -ChildPath "Backend"

# Funciones auxiliares
function Info {
    param([string]$Message)
    Write-Host "INFO: $Message" -ForegroundColor Blue
}

function Success {
    param([string]$Message)
    Write-Host "OK: $Message" -ForegroundColor Green
}

function Warning {
    param([string]$Message)
    Write-Host "WARNING: $Message" -ForegroundColor Yellow
}

function ErrorMsg {
    param([string]$Message)
    Write-Host "ERROR: $Message" -ForegroundColor Red
}

# Comandos
function Build {
    Info "Compilando proyecto..."
    Push-Location $BACKEND_DIR
    & .\gradlew.bat clean build -x test
    Pop-Location
    Success "Compilacion completada"
}

function Test {
    Info "Ejecutando tests..."
    Push-Location $BACKEND_DIR
    & .\gradlew.bat test
    Pop-Location
    Success "Tests completados"
}

function DockerBuild {
    Info "Construyendo imagen Docker..."
    Push-Location $BACKEND_DIR
    & docker build -t flytrack:local .
    Pop-Location
    Success "Imagen Docker construida: flytrack:local"
}

function DevUp {
    Info "Levantando ambiente de desarrollo..."
    Push-Location $BACKEND_DIR
    & docker-compose up -d
    Pop-Location
    Start-Sleep -Seconds 5
    Success "Ambiente de desarrollo levantado"
    DevStatus
}

function DevDown {
    Info "Deteniendo ambiente de desarrollo..."
    Push-Location $BACKEND_DIR
    & docker-compose down
    Pop-Location
    Success "Ambiente de desarrollo detenido"
}

function DevLogs {
    Info "Mostrando logs de desarrollo..."
    Push-Location $BACKEND_DIR
    & docker-compose logs -f
    Pop-Location
}

function StagingUp {
    Info "Levantando ambiente de staging..."
    Push-Location $PROJECT_ROOT
    & docker-compose -f docker-compose.staging.yml up -d
    Pop-Location
    Start-Sleep -Seconds 5
    Success "Ambiente de staging levantado"
    StagingStatus
}

function StagingDown {
    Info "Deteniendo ambiente de staging..."
    Push-Location $PROJECT_ROOT
    & docker-compose -f docker-compose.staging.yml down
    Pop-Location
    Success "Ambiente de staging detenido"
}

function StagingLogs {
    Info "Mostrando logs de staging..."
    Push-Location $PROJECT_ROOT
    & docker-compose -f docker-compose.staging.yml logs -f
    Pop-Location
}

function Run {
    Info "Ejecutando aplicación en modo desarrollo..."
    Push-Location $BACKEND_DIR
    & .\gradlew.bat bootRun
    Pop-Location
}

function Clean {
    Info "Limpiando proyecto..."
    Push-Location $BACKEND_DIR
    & .\gradlew.bat clean
    Pop-Location
    Success "Proyecto limpiado"
}

function DevStatus {
    Push-Location $BACKEND_DIR
    & docker-compose ps
    Pop-Location
}

function StagingStatus {
    Push-Location $PROJECT_ROOT
    try {
        & docker-compose -f docker-compose.staging.yml ps
    }
    catch {
        Warning "Staging no levantado"
    }
    Pop-Location
}

function Status {
    Info "Estado de desarrollo:"
    DevStatus

    Write-Host ""
    Info "Estado de staging:"
    StagingStatus
}

function Help {
    Write-Host @"
=== FlyTrack DevOps Script (Windows) ===

Comandos disponibles:

Desarrollo Local:
  build           - Compila el proyecto
  test            - Ejecuta tests unitarios
  run             - Ejecuta la aplicacion
  dev:up          - Levanta MariaDB (docker-compose)
  dev:down        - Detiene MariaDB
  dev:logs        - Muestra logs de desarrollo
  dev:status      - Muestra estado de contenedores de desarrollo
  clean           - Limpia el proyecto

Docker:
  docker:build    - Construye imagen Docker local

Staging:
  staging:up      - Levanta ambiente de staging completo
  staging:down    - Detiene ambiente de staging
  staging:logs    - Muestra logs de staging
  staging:status  - Muestra estado de staging

Utilidades:
  help            - Muestra esta ayuda
  status          - Muestra estado de todos los contenedores

Ejemplos:
  .\scripts\flytrack.ps1 build
  .\scripts\flytrack.ps1 test
  .\scripts\flytrack.ps1 dev:up
  .\scripts\flytrack.ps1 run

"@ -ForegroundColor Cyan
}

# Ejecutar comando
switch ($Command) {
    "build" { Build }
    "test" { Test }
    "docker:build" { DockerBuild }
    "dev:up" { DevUp }
    "dev:down" { DevDown }
    "dev:logs" { DevLogs }
    "dev:status" { DevStatus }
    "staging:up" { StagingUp }
    "staging:down" { StagingDown }
    "staging:logs" { StagingLogs }
    "staging:status" { StagingStatus }
    "run" { Run }
    "clean" { Clean }
    "status" { Status }
    "help" { Help }
    "--help" { Help }
    "-h" { Help }
    default {
        ErrorMsg "Comando no reconocido: $Command"
        Write-Host ""
        Help
        exit 1
    }
}





